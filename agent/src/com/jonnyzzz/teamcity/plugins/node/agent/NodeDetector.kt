package com.jonnyzzz.teamcity.plugins.node.agent

import com.intellij.execution.configurations.GeneralCommandLine
import com.jonnyzzz.teamcity.plugins.node.common.NPMBean
import com.jonnyzzz.teamcity.plugins.node.common.NodeBean
import com.jonnyzzz.teamcity.plugins.node.common.log4j
import jetbrains.buildServer.SimpleCommandLineProcessRunner
import jetbrains.buildServer.SimpleCommandLineProcessRunner.ProcessRunCallbackAdapter
import jetbrains.buildServer.agent.AgentLifeCycleAdapter
import jetbrains.buildServer.agent.AgentLifeCycleListener
import jetbrains.buildServer.agent.BuildAgent
import jetbrains.buildServer.agent.BuildAgentConfiguration
import jetbrains.buildServer.util.EventDispatcher
import com.jonnyzzz.teamcity.plugins.node.agent.processes.ProcessExecutor
import com.jonnyzzz.teamcity.plugins.node.agent.processes.execution
import com.jonnyzzz.teamcity.plugins.node.agent.processes.succeeded

/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 12.01.13 1:04
 */
public class NodeToolsDetector(events: EventDispatcher<AgentLifeCycleListener>,
                               val config: BuildAgentConfiguration,
                               val exec : ProcessExecutor) {
  private val LOG = log4j(this.javaClass)
  private val bean = NodeBean();

  fun detectNodeTool(executable: String, configParameterName: String, versionPreprocess : (String) -> String = {it}) {
    val run = exec runProcess execution(executable, "--version")

    if (!run.succeeded()) {
      LOG.info("${executable} was not found or failed")
      return
    }

    val version = versionPreprocess(run.stdOut.trim())
    LOG.info("${executable} ${version} was detected")
    config.addConfigurationParameter(configParameterName, version)
  }

  {
    events.addListener(object : AgentLifeCycleAdapter() {
      public override fun beforeAgentConfigurationLoaded(agent: BuildAgent) {
        detectNodeTool("node", NodeBean().nodeJSConfigurationParameter) {
          if (it.startsWith("v")) it.substring(1) else it
        }
        detectNodeTool("npm", NPMBean().nodeJSNPMConfigurationParameter)
      }
    })
  }
}
