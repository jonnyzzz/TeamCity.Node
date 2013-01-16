package com.jonnyzzz.teamcity.plugins.node.agent

/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 15.01.13 0:56
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


public class PhantomJsService() : JsService() {
  protected override fun getToolPath(): String? = getRunnerParameters()[bean.toolPathKey]

  protected override fun getToolName(): String = "phantom"

  protected override fun getGeneratedScriptExt(): String
          = "." + (getRunnerParameters()[bean.phantomJsExtensionKey] ?: bean.phantomJsExtensionDefault)
}
