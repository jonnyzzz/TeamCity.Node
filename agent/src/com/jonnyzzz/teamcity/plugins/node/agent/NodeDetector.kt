package com.jonnyzzz.teamcity.plugins.node.agent

import jetbrains.buildServer.util.EventDispatcher
import jetbrains.buildServer.agent.AgentLifeCycleListener
import jetbrains.buildServer.agent.AgentLifeCycleAdapter
import jetbrains.buildServer.agent.BuildAgent
import jetbrains.buildServer.agent.BuildAgentConfiguration
import jetbrains.buildServer.SimpleCommandLineProcessRunner
import java.io.File
import com.intellij.execution.configurations.GeneralCommandLine
import jetbrains.buildServer.SimpleCommandLineProcessRunner.ProcessRunCallback
import jetbrains.buildServer.SimpleCommandLineProcessRunner.ProcessRunCallbackAdapter
import com.jonnyzzz.teamcity.plugins.node.common.NodeBean
import com.jonnyzzz.teamcity.plugins.node.common.log4j
import com.jonnyzzz.teamcity.plugins.node.common.NPMBean

/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 12.01.13 1:04
 */
public class NodeToolsDetector(events : EventDispatcher<AgentLifeCycleListener>,
                               config : BuildAgentConfiguration) {
  {
    val LOG = log4j(this.javaClass)
    val bean = NodeBean()
    events.addListener(object : AgentLifeCycleAdapter() {
      public override fun beforeAgentConfigurationLoaded(agent: BuildAgent) {
        fun detectNodeTool(executable : String, configParameterName : String) {
          val cmd = GeneralCommandLine()
          cmd.setExePath("cmd")
          cmd.addParameter("/c")
          cmd.addParameter(executable)
          cmd.addParameter("--version")

          val run = SimpleCommandLineProcessRunner.runCommand(
                  cmd, byteArray(), object : ProcessRunCallbackAdapter() {
            public override fun getOutputIdleSecondsTimeout(): Int? = 1
          })

          if (run == null || run.getExitCode() != 0) {
            LOG.info("${executable} was not found")
            return
          }

          var version = run.getStdout().trim()
          if (version.startsWith("v")) {
            version = version.substring(1)
          }
          LOG.info("${executable} ${version} was detected")
          config.addConfigurationParameter(configParameterName, version)
        }

        detectNodeTool("node", NodeBean().nodeJSConfigurationParameter)
        detectNodeTool("npm", NPMBean().nodeJSNPMConfigurationParameter)
      }

    })
  }
}
