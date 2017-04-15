/*
 * Copyright 2013-2017 Eugene Petrenko
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

package com.jonnyzzz.teamcity.plugins.node.agent.bower

import com.jonnyzzz.teamcity.plugins.node.agent.BaseService
import com.jonnyzzz.teamcity.plugins.node.common.BowerBean
import com.jonnyzzz.teamcity.plugins.node.common.BowerExecutionMode
import com.jonnyzzz.teamcity.plugins.node.common.div
import com.jonnyzzz.teamcity.plugins.node.common.fetchArguments
import jetbrains.buildServer.RunBuildException
import jetbrains.buildServer.agent.AgentBuildRunnerInfo
import jetbrains.buildServer.agent.BuildAgentConfiguration
import jetbrains.buildServer.agent.runner.CommandLineBuildService
import jetbrains.buildServer.agent.runner.CommandLineBuildServiceFactory
import jetbrains.buildServer.agent.runner.ProgramCommandLine
import java.util.*

/**
 * Created by Victor Mosin (vicmosin@gmail.com)
 * Date: 01.02.17 8:58
 */

class BowerServiceFactory : CommandLineBuildServiceFactory {
  private val bean = BowerBean()

  override fun getBuildRunnerInfo(): AgentBuildRunnerInfo = object : AgentBuildRunnerInfo {
    override fun getType(): String = bean.runTypeName
    override fun canRun(agentConfiguration: BuildAgentConfiguration): Boolean = true
  }

  override fun createService(): CommandLineBuildService = BowerSession()
}

class BowerSession : BaseService() {
  private val bean = BowerBean()

  private fun bowerExecutable() : String =
          if (agentConfiguration.systemInfo.isWindows)
            "bower.cmd"
          else
            "bower"

  private fun bowerExecutablePath() : String {
    val mode = bean.parseMode(runnerParameters[bean.bowerMode])
    val cmd = bowerExecutable()

    when(mode) {
      BowerExecutionMode.NPM -> {
        val wd = workingDirectory / "node_modules" / ".bin"
        val bower = wd / cmd

        if (!bower.isFile) {
          throw RunBuildException(
                  "Failed to find $cmd under $wd.\n" +
                  "Please install bower as project-local Node.js NPM packages")
        }
        return bower.path
      }

      BowerExecutionMode.GLOBAL -> return cmd

      else ->
        throw RunBuildException("Unexpected execution mode $mode")
    }
  }

  override fun makeProgramCommandLine(): ProgramCommandLine {
    val arguments = arrayListOf<String>()

    val parameters = TreeMap<String, String>()
    parameters.putAll(buildParameters.systemProperties)

    val parametersAll = TreeMap<String, String>()
    parametersAll.putAll(configParameters)
    parametersAll.putAll(buildParameters.allParameters)

    arguments.addAll(generateDefaultTeamCityParametersJSON())

    arguments.addAll(runnerParameters[bean.commandLineParameterKey].fetchArguments())
    arguments.addAll(bean.parseCommands(runnerParameters[bean.targets]))

    return execute(bowerExecutablePath(), arguments)
  }
}
