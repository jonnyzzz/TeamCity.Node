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

package com.jonnyzzz.teamcity.plugins.node.agent.gulp

import com.jonnyzzz.teamcity.plugins.node.agent.processes.ExecutorProxy
import com.jonnyzzz.teamcity.plugins.node.agent.*
import jetbrains.buildServer.agent.AgentBuildRunnerInfo
import jetbrains.buildServer.agent.BuildAgentConfiguration
import jetbrains.buildServer.agent.runner.CommandLineBuildServiceFactory
import jetbrains.buildServer.agent.runner.CommandLineBuildService
import jetbrains.buildServer.agent.runner.ProgramCommandLine
import jetbrains.buildServer.RunBuildException
import com.jonnyzzz.teamcity.plugins.node.common.fetchArguments
import com.jonnyzzz.teamcity.plugins.node.common.resolve
import com.jonnyzzz.teamcity.plugins.node.common.div
import java.util.TreeMap
import com.jonnyzzz.teamcity.plugins.node.common.tempFile
import com.jonnyzzz.teamcity.plugins.node.common.TempFileName
import com.jonnyzzz.teamcity.plugins.node.common.smartDelete
import com.google.gson.Gson
import com.jonnyzzz.teamcity.plugins.node.common.GulpBean
import com.jonnyzzz.teamcity.plugins.node.common.GulpExecutionMode

/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 27.04.13 10:20
 */

public class GulpServiceFactory(val proxy: ExecutorProxy): CommandLineBuildServiceFactory {
  private val bean = GulpBean()

  public override fun getBuildRunnerInfo(): AgentBuildRunnerInfo = object : AgentBuildRunnerInfo {
    public override fun getType(): String = bean.runTypeName
    public override fun canRun(agentConfiguration: BuildAgentConfiguration): Boolean = true
  }

  public override fun createService(): CommandLineBuildService = GulpSession()
}

public class GulpSession : BaseService() {
  private val bean = GulpBean()

  private fun gulpExecutable() : String =
          if (getAgentConfiguration().getSystemInfo().isWindows())
            "gulp.cmd"
          else
            "gulp"

  private fun gulpExecutablePath() : String {
    val mode = bean.parseMode(getRunnerParameters()[bean.gulpMode])
    val cmd = gulpExecutable()

    when(mode) {
      GulpExecutionMode.NPM -> {
        val gulp = getWorkingDirectory() / "node_modules" / ".bin" / cmd

        if (!gulp.isFile()) {
          throw RunBuildException(
                  "Failed to find ${cmd} under ${getWorkingDirectory()}/node_modules/.bin.\n" +
                          "Please install 'gulp' as project-local Node.js NPM package")
        }
        return gulp.getPath()
      }

      GulpExecutionMode.GLOBAL -> return cmd

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
        throw RunBuildException("Failed to find file at path: ${file}")
      }
      arguments add "--gulpfile"
      arguments add file.getPath()
    }

    val parameters = TreeMap<String, String>()
    parameters.putAll(getBuildParameters().getSystemProperties())

    val parametersAll = TreeMap<String, String>()
    parametersAll.putAll(getConfigParameters())
    parametersAll.putAll(getBuildParameters().getAllParameters())

    arguments addAll generateDefaultTeamCityParametersJSON()

    arguments addAll bean.parseCommands(getRunnerParameters()[bean.targets])
    arguments addAll getRunnerParameters()[bean.commandLineParameterKey].fetchArguments()

    return execute(gulpExecutablePath(), arguments)
  }

}
