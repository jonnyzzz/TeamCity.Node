/*
 * Copyright 2013-2015 Eugene Petrenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jonnyzzz.teamcity.plugins.node.agent.processes

import jetbrains.buildServer.agent.BuildProcess
import jetbrains.buildServer.agent.BuildFinishedStatus
import jetbrains.buildServer.agent.AgentRunningBuild
import com.jonnyzzz.teamcity.plugins.node.agent.block
import jetbrains.buildServer.agent.BuildProgressLogger
import jetbrains.buildServer.agent.BuildProcessFacade
import com.jonnyzzz.teamcity.plugins.node.common.log4j
import jetbrains.buildServer.runner.SimpleRunnerConstants

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 17.08.13 12:31
 */

interface CompositeProcessFactory {
  fun compositeBuildProcess(build: AgentRunningBuild,
                            builder: CompositeProcessBuilder<Unit>.() -> Unit): BuildProcess
}

class CompositeProcessFactoryImpl(val facade: BuildProcessFacade) : CompositeProcessFactory {
  override fun compositeBuildProcess(build: AgentRunningBuild,
                                     builder: CompositeProcessBuilder<Unit>.() -> Unit): BuildProcess {
    val proc = CompositeBuildProcessImpl()
    object:CompositeProcessBuilderImpl<Unit>(build, facade) {
      override fun push(p: BuildProcess) {
        proc.pushBuildProcess(p)
      }
    }.builder()
    return proc
  }
}

interface CompositeProcessBuilder<R> {
  fun execute(blockName: String, blockDescription: String = blockName, p: () -> Unit): R
  fun delegate(blockName: String, blockDescription: String = blockName, p: () -> BuildProcess): R
  fun script(blockName: String, blockDescription: String = blockName, workingDir: String, script: () -> String): R
}

abstract class CompositeProcessBuilderImpl<R>(val build: AgentRunningBuild,
                                              val facade: BuildProcessFacade) : CompositeProcessBuilder<R> {
  private val logger: BuildProgressLogger
    get() = build.buildLogger

  override fun script(blockName: String, blockDescription: String, workingDir: String, script: () -> String) =
          delegate(blockName, blockDescription) {
            val commandLine = (
            if(build.agentConfiguration.systemInfo.isWindows)
              ""
            else "#!/bin/bash\n\n"
            ) + script()

            log4j(javaClass).info("Executing shell command:\n$commandLine")
            val ctx = facade.createBuildRunnerContext(build, SimpleRunnerConstants.TYPE, workingDir)
            ctx.addRunnerParameter(SimpleRunnerConstants.USE_CUSTOM_SCRIPT, "true");
            ctx.addRunnerParameter(SimpleRunnerConstants.SCRIPT_CONTENT, commandLine);

            facade.createExecutable(build, ctx)
          }

  override fun execute(blockName: String, blockDescription: String, p: () -> Unit): R =
          delegate(blockName, blockDescription) {
            process(p)
          }

  override fun delegate(blockName: String, blockDescription: String, p: () -> BuildProcess): R =
          push(logger.block(blockName, blockDescription, action(p)))


  fun push(p: DelegatingProcessAction): R = push(DelegatingBuildProcess(p))
  protected abstract fun push(p: BuildProcess): R

  private fun process(p: () -> Unit): BuildProcess = object:BuildProcessBase() {
    override fun waitForImpl(): BuildFinishedStatus = with(p()) { BuildFinishedStatus.FINISHED_SUCCESS }
  }

  private fun action(p: () -> BuildProcess): DelegatingProcessAction = object:DelegatingProcessAction {
    override fun startImpl(): BuildProcess = p()
  }

  private fun BuildProgressLogger.block(name: String,
                                        description: String = name,
                                        a: DelegatingProcessAction): DelegatingProcessAction {
    return object:DelegatingProcessAction {
      private var action: () -> Unit = { };
      override fun startImpl(): BuildProcess {
        action = block(name, description)
        return a.startImpl()
      }
      override fun finishedImpl() {
        action()
        a.finishedImpl()
      }
    }
  }
}
