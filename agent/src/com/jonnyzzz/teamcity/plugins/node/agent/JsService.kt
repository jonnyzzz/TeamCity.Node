/*
 * Copyright 2000-2013 Eugene Petrenko
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
import java.io.IOException
import com.jonnyzzz.teamcity.plugins.node.common.log4j
import com.jonnyzzz.teamcity.plugins.node.common.smartDelete
import com.jonnyzzz.teamcity.plugins.node.common.fetchArguments
import org.apache.log4j.Logger
import java.io.File

/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 15.01.13 1:00
 */

public trait NodePhantomInfo {
  public fun getToolPath(): String?
  public fun getToolName(): String
  public fun getGeneratedScriptExt(): String
}

public trait NodePhantomContext {
  fun getRunnerParameters(): Map<String, String>
  fun getCheckoutDirectory(): File
  fun getAgentTempDirectory(): File
}

public trait NodePhantomBuilder<ProgramCommandLine> {
  fun disposeLater(action: () -> Unit)
  fun createProgramCommandline(file: String, args: List<String>): ProgramCommandLine
}

public class NodePhantomStrategy<ProgramCommandLine>(val info: NodePhantomInfo,
                                                     val context: NodePhantomContext,
                                                     val builder: NodePhantomBuilder<ProgramCommandLine>) {
  private val bean: NodeBean = NodeBean()
  private val LOG: Logger = log4j(javaClass)


  public fun makeProgramCommandLine(): ProgramCommandLine {
    val mode = bean.findExecutionMode(context.getRunnerParameters())

    if (mode == null) {
      throw RunBuildException("Execution mode was not specified")
    }

    val arguments = arrayListOf<String>()

    //add [options section]
    arguments addAll fetchArguments(bean.commandLineParameterKey)

    //add script file
    if (mode == ExecutionModes.File) {
      val filePath = context.getRunnerParameters()[mode.parameter]
      if (filePath == null || filePath.isEmptyOrSpaces()) {
        throw RunBuildException("Script file path was not specified")
      }

      val file = context.getCheckoutDirectory().resolve(filePath)
      if (!file.isFile()) {
        throw RunBuildException("Failed to find File at path: ${filePath}")
      }

      arguments.add(file.getPath())
    } else if (mode == ExecutionModes.Script) {
      val scriptText = context.getRunnerParameters()[mode.parameter]
      if (scriptText == null || scriptText.isEmptyOrSpaces()) {
        throw RuntimeException("Script was not defined or empty")
      }

      val tempScript = io("Failed to create temp file") {
        context.getAgentTempDirectory() tempFile TempFileName(info.getToolName(), getGeneratedScriptExtImpl())
      }

      builder disposeLater { tempScript.smartDelete() }

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

    val executable = info.getToolPath()
    if (executable == null) {
      throw RunBuildException("Path to tool was not specified")
    }

    return builder.createProgramCommandline(executable, arguments)
  }

  inline private fun io<T>(errorMessage: String, body: () -> T): T {
    try {
      return body()
    } catch (e: IOException) {
      throw RunBuildException("${errorMessage}. ${e.getMessage()}", e)
    }
  }

  inline private fun getGeneratedScriptExtImpl(): String {
    var ext = info.getGeneratedScriptExt()
    while(ext.startsWith(".")) ext = ext.substring(1)
    return "." + ext
  }

  inline private fun fetchArguments(runnerParametersKey: String): Collection<String> {
    return context.getRunnerParameters().get(runnerParametersKey).fetchArguments()
  }

}

public abstract class JsService() : BuildServiceAdapter(), NodePhantomInfo {
  protected val bean: NodeBean = NodeBean()
  protected val LOG: Logger = log4j(javaClass)
  private val disposables = linkedListOf<() -> Unit>()

  public override fun makeProgramCommandLine(): ProgramCommandLine {
    val that = this
    return NodePhantomStrategy<ProgramCommandLine>(
      this,
      object:NodePhantomContext {
        override fun getRunnerParameters(): Map<String, String> = that.getRunnerParameters()
        override fun getCheckoutDirectory(): File = that.getCheckoutDirectory()
        override fun getAgentTempDirectory(): File = that.getAgentTempDirectory()
      },
      object:NodePhantomBuilder<ProgramCommandLine>{
      override fun createProgramCommandline(file: String, args: List<String>): ProgramCommandLine = that.createProgramCommandline(file, args)
      override fun disposeLater(action: () -> Unit) {
          disposables add action
        }
      }
    ).makeProgramCommandLine()
  }

  public override fun afterProcessFinished() {
    super<BuildServiceAdapter>.afterProcessFinished()

    disposables.forEach { it() }
  }
}
