package com.jonnyzzz.teamcity.plugins.node.agent

/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 15.01.13 1:00
 */
import jetbrains.buildServer.RunBuildException
import jetbrains.buildServer.agent.runner.ProgramCommandLine
import jetbrains.buildServer.agent.runner.BuildServiceAdapter
import jetbrains.buildServer.util.FileUtil
import com.jonnyzzz.teamcity.plugins.node.common.NodeBean
import com.jonnyzzz.teamcity.plugins.node.common.ExecutionModes
import com.jonnyzzz.teamcity.plugins.node.common.isEmptyOrSpaces
import com.jonnyzzz.teamcity.plugins.node.common.resolve
import com.jonnyzzz.teamcity.plugins.node.common.splitHonorQuotes
import com.jonnyzzz.teamcity.plugins.node.common.TempFileName
import com.jonnyzzz.teamcity.plugins.node.common.tempFile
import java.io.IOException
import java.io.File
import com.jonnyzzz.teamcity.plugins.node.common.log4j
import com.jonnyzzz.teamcity.plugins.node.common.smartDelete

public open abstract class JsService() : ServiceBase() {
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
        throw RunBuildException("Failed to find File at path: ${filePath}")
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

    return createProgramCommandline(executable, arguments)
  }

  protected abstract fun getToolPath() : String?
  protected abstract fun getToolName() : String
  protected abstract fun getGeneratedScriptExt() : String

  private fun getGeneratedScriptExtImpl() : String {
    var ext = getGeneratedScriptExt()
    while(ext.startsWith(".")) ext = ext.substring(1)
    return "." + ext
  }

}
