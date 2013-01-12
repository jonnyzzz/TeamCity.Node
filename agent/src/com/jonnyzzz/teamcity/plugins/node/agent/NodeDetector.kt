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

/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 12.01.13 1:04
 */
public class NodeJsDetector(events : EventDispatcher<AgentLifeCycleListener>,
                            config : BuildAgentConfiguration) {
  {
    val LOG = log4j(javaClass<NodeJsDetector>())
    val bean = NodeBean()
    events.addListener(object : AgentLifeCycleAdapter() {
      public override fun beforeAgentConfigurationLoaded(agent: BuildAgent) {
        val cmd = GeneralCommandLine()
        cmd.setExePath("node")
        cmd.addParameter("--version")

        val array = JavaHelper.EMPTY_BYTES
        val run = SimpleCommandLineProcessRunner.runCommand(
                cmd, array, object : ProcessRunCallbackAdapter() {
          public override fun getOutputIdleSecondsTimeout(): Int? = 1
        })

        if (run == null || run.getExitCode() != 0) {
          LOG.info("Node.js was not found")
          return
        }

        val version = run.getStdout().trim()
        LOG.info("Node.js ${version} was detected")
        config.addConfigurationParameter(bean.nodeJSConfigurationParameter, version)
      }
    })
  }
}
