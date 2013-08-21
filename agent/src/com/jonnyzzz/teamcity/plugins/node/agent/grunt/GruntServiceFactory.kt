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

package com.jonnyzzz.teamcity.plugins.node.agent.grunt

import com.jonnyzzz.teamcity.plugins.node.agent.processes.ExecutorProxy
import com.jonnyzzz.teamcity.plugins.node.common.GruntBean
import com.jonnyzzz.teamcity.plugins.node.agent.*
import jetbrains.buildServer.agent.AgentBuildRunnerInfo
import jetbrains.buildServer.agent.BuildAgentConfiguration
import jetbrains.buildServer.agent.runner.CommandLineBuildServiceFactory
import jetbrains.buildServer.agent.runner.CommandLineBuildService
import jetbrains.buildServer.agent.runner.ProgramCommandLine
import jetbrains.buildServer.RunBuildException
import com.jonnyzzz.teamcity.plugins.node.common.GruntExecutionMode
import com.jonnyzzz.teamcity.plugins.node.common.fetchArguments
import com.jonnyzzz.teamcity.plugins.node.common.resolve
import com.jonnyzzz.teamcity.plugins.node.common.div
import java.util.TreeMap
import com.jonnyzzz.teamcity.plugins.node.common.tempFile
import com.jonnyzzz.teamcity.plugins.node.common.TempFileName
import com.jonnyzzz.teamcity.plugins.node.common.smartDelete
import com.google.gson.Gson

/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 27.04.13 10:20
 */

public class GruntServiceFactory(val proxy: ExecutorProxy): CommandLineBuildServiceFactory {
  private val bean = GruntBean()

  public override fun getBuildRunnerInfo(): AgentBuildRunnerInfo = object : AgentBuildRunnerInfo {
    public override fun getType(): String = bean.runTypeName
    public override fun canRun(agentConfiguration: BuildAgentConfiguration): Boolean = true
  }

  public override fun createService(): CommandLineBuildService = GruntSession()
}

public class GruntSession : BaseService() {
  private val bean = GruntBean()

  private fun gruntExecutable() : String =
          if (getAgentConfiguration().getSystemInfo().isWindows())
            "grunt.cmd"
          else
            "grunt"

  private fun gruntExecutablePath() : String {
    val mode = bean.parseMode(getRunnerParameters()[bean.gruntMode])
    val cmd = gruntExecutable()

    when(mode) {
      GruntExecutionMode.NPM -> {
        val grunt = getWorkingDirectory() / "node_modules" / ".bin" / cmd

        if (!grunt.isFile()) {
          throw RunBuildException(
                  "Failed to find ${gruntExecutable()} under ${getWorkingDirectory()}.\n" +
                  "Please install grunt and grunt-cli as project-local Node.js NPM packages")
        }
        return grunt.getPath()
      }

      GruntExecutionMode.GLOBAL -> return cmd

      else ->
        throw RunBuildException("Unexpected execution mode ${mode}")
    }
  }

  public override fun makeProgramCommandLine(): ProgramCommandLine {
    val arguments = arrayListOf<String>()
    arguments add "--no-color"

    val filePath = getRunnerParameters()[bean.file]
    if (filePath != null) {
      val file = getCheckoutDirectory().resolve(filePath)
      if (!file.isFile()) {
        throw RunBuildException("Failed to find File at path: ${file}")
      }
      arguments add "--gruntfile"
      arguments add file.getPath()
    }

    val parameters = TreeMap<String, String>()
    parameters.putAll(getBuildParameters().getSystemProperties())

    val parametersAll = TreeMap<String, String>()
    parametersAll.putAll(getConfigParameters())
    parametersAll.putAll(getBuildParameters().getAllParameters())

    arguments add "--teamcity.properties.all=" + generateTeamCityProperties { putAll(parametersAll) }
    arguments add "--teamcity.properties=" + generateTeamCityProperties { putAll(parameters) }

    arguments addAll getRunnerParameters()[bean.commandLineParameterKey].fetchArguments()
    arguments addAll bean.parseCommands(getRunnerParameters()[bean.targets])

    return execute(gruntExecutablePath(), arguments)
  }

  private fun generateTeamCityProperties(builder : MutableMap<String,String>.() -> Unit) : String {
    val file = io("Failed to create temp file") {
      getAgentTempDirectory() tempFile TempFileName("teamcity", ".json")
    }
    disposeLater { file.smartDelete() }

    val map = TreeMap<String, String>()
    map.builder()
    val text = Gson().toJson(map)!!
    io("Failed to create parameters file: ${file}") {
      writeUTF(file, text)
    }
    return file.getPath()
  }
}
