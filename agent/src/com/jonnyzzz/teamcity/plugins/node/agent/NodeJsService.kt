package com.jonnyzzz.teamcity.plugins.node.agent

import jetbrains.buildServer.RunBuildException
import jetbrains.buildServer.agent.runner.ProgramCommandLine
import jetbrains.buildServer.agent.runner.BuildServiceAdapter
import jetbrains.buildServer.util.FileUtil
import com.jonnyzzz.teamcity.plugins.node.common.NodeBean
import com.jonnyzzz.teamcity.plugins.node.common.ExecutionModes
import com.jonnyzzz.teamcity.plugins.node.common.isEmptyOrSpaces
import com.jonnyzzz.teamcity.plugins.node.common.resolve
import com.jonnyzzz.teamcity.plugins.node.common.splitHonorQuotes

/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 12.01.13 0:58
 */

public class NodeJsService() : BuildServiceAdapter() {
  private val bean = NodeBean()

  public override fun makeProgramCommandLine(): ProgramCommandLine {
    val mode = bean.findExecutionMode(getRunnerParameters())

    if (mode == null) {
      throw RunBuildException("Execution mode was not specified")
    }

    val arguments = arrayListOf<String>()

    //add [options section]
    arguments.addAll(fetchArguments(bean.commandLineParameterKey))

    //add script file
    if (mode == ExecutionModes.File) {
      val filePath = getRunnerParameters()[mode.parameter]
      if (filePath == null || filePath.isEmptyOrSpaces()) {
        throw RunBuildException("Script file path was not specified")
      }

      val file = getCheckoutDirectory().resolve(filePath)
      if (!file.isFile()) {
        throw RunBuildException("Failed to find File at path: ${filePath}")
      }

      arguments.add(file.getPath())
    } else if (mode == ExecutionModes.Script) {
      throw RunBuildException("Not Implemented yet for ${mode}")
    } else {
      throw RunBuildException("Unknown exection mode: ${mode}")
    }

    //add script options
    arguments.addAll(fetchArguments(bean.scriptParameterKey))

    //TODO: commandline arguments
    return createProgramCommandline("node", arguments)
  }

  private fun fetchArguments(runnerParametersKey : String) : Collection<String> {
    val custom = getRunnerParameters().get(runnerParametersKey);
    if (custom == null || custom.isEmptyOrSpaces()) return listOf<String>()

    return custom
            .split("[\\r\\n]+")
            .map { it.trim() }
            .filter { it.isEmptyOrSpaces() }
            .flatMap{ it.splitHonorQuotes() }
  }

}
