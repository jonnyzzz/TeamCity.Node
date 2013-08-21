/*
 * Copyright 2013-2013 Eugene Petrenko
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
package com.jonnyzzz.teamcity.plugins.node.agent

import jetbrains.buildServer.RunBuildException
import jetbrains.buildServer.agent.runner.ProgramCommandLine
import jetbrains.buildServer.agent.runner.BuildServiceAdapter
import jetbrains.buildServer.util.FileUtil
import com.jonnyzzz.teamcity.plugins.node.common.NodeBean
import com.jonnyzzz.teamcity.plugins.node.common.ExecutionModes
import com.jonnyzzz.teamcity.plugins.node.common.isEmptyOrSpaces
import com.jonnyzzz.teamcity.plugins.node.common.resolve
import com.jonnyzzz.teamcity.plugins.node.common.TempFileName
import com.jonnyzzz.teamcity.plugins.node.common.tempFile
import com.jonnyzzz.teamcity.plugins.node.common.log4j
import com.jonnyzzz.teamcity.plugins.node.common.smartDelete
import com.jonnyzzz.teamcity.plugins.node.common.fetchArguments
import org.apache.log4j.Logger
import com.jonnyzzz.teamcity.plugins.node.agent.processes.ScriptWrappingCommandLineGenerator
import jetbrains.buildServer.agent.runner.SimpleProgramCommandLine

/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 15.01.13 1:00
 */

public abstract class BaseService : BuildServiceAdapter() {
  private val disposables = linkedListOf<() -> Unit>()
  protected val LOG : Logger = log4j(this.javaClass)

  protected fun disposeLater(action : () -> Unit) {
    disposables add action
  }

  override fun afterProcessFinished() {
    super<BuildServiceAdapter>.afterProcessFinished()

    disposables.forEach { it() }
  }

  protected fun execute(executable:String, arguments:List<String>) : ProgramCommandLine {
    val that = this
    return object:ScriptWrappingCommandLineGenerator<ProgramCommandLine>(getRunnerContext()) {
      override fun execute(executable: String, args: List<String>): ProgramCommandLine
              = SimpleProgramCommandLine(build, executable, args)
      override fun disposeLater(action: () -> Unit) = that.disposeLater(action)
    }.generate(executable, arguments)
  }
}

public abstract class JsService() : BaseService() {
  protected val bean : NodeBean = NodeBean()

  public override fun makeProgramCommandLine(): ProgramCommandLine {
    val mode = bean.findExecutionMode(getRunnerParameters())

    if (mode == null) {
      throw RunBuildException("Execution mode was not specified")
    }

    val arguments = arrayListOf<String>()

    //add [options section]
    arguments addAll fetchArguments(bean.commandLineParameterKey)

    //add script file
    if (mode == ExecutionModes.File) {
      val filePath = getRunnerParameters()[mode.parameter]
      if (filePath == null || filePath.isEmptyOrSpaces()) {
        throw RunBuildException("Script file path was not specified")
      }

      val file = getCheckoutDirectory().resolve(filePath)
      if (!file.isFile()) {
        throw RunBuildException("Failed to find File at path: ${file}")
      }

      arguments.add(file.getPath())
    } else if (mode == ExecutionModes.Script) {
      val scriptText = getRunnerParameters()[mode.parameter]
      if (scriptText == null || scriptText.isEmptyOrSpaces()) {
        throw RuntimeException("Script was not defined or empty")
      }

      val tempScript = io("Failed to create temp file") {
        getAgentTempDirectory() tempFile TempFileName(getToolName(), getGeneratedScriptExtImpl())
      }

      disposeLater { tempScript.smartDelete() }

      io("Failed to write script to temp file") {
        FileUtil.writeFileAndReportErrors(tempScript, scriptText);
      }
      LOG.info("Generated script was saved to file: ${tempScript}")

      //so add generated script as commandline parameter
      arguments add tempScript.getPath()
    } else {
      throw RunBuildException("Unknown exection mode: ${mode}")
    }

    //add script options
    arguments addAll fetchArguments(bean.scriptParameterKey)

    val executable = getToolPath()
    if (executable == null) {
      throw RunBuildException("Path to tool was not specified")
    }

    return execute(executable, arguments)
  }

  protected abstract fun getToolPath() : String?
  protected abstract fun getToolName() : String
  protected abstract fun getGeneratedScriptExt() : String

  private fun getGeneratedScriptExtImpl() : String {
    var ext = getGeneratedScriptExt()
    while(ext.startsWith(".")) ext = ext.substring(1)
    return "." + ext
  }

  protected fun fetchArguments(runnerParametersKey : String) : Collection<String> {
    return getRunnerParameters().get(runnerParametersKey).fetchArguments()
  }
}
