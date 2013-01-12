package com.jonnyzzz.teamcity.plugins.node.agent

import jetbrains.buildServer.agent.runner.CommandLineBuildServiceFactory
import jetbrains.buildServer.agent.runner.CommandLineBuildService
import jetbrains.buildServer.agent.AgentBuildRunnerInfo
import jetbrains.buildServer.agent.BuildAgentConfiguration
import com.jonnyzzz.teamcity.plugins.node.common.NodeBean
import jetbrains.buildServer.agent.runner.BuildServiceAdapter
import jetbrains.buildServer.agent.runner.ProgramCommandLine

/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 12.01.13 0:52
 */
public class NodeJsRunnerService : CommandLineBuildServiceFactory {
  private val bean : NodeBean = NodeBean()

  public override fun createService(): CommandLineBuildService {
    return NodeJsService()
  }

  public override fun getBuildRunnerInfo(): AgentBuildRunnerInfo = object : AgentBuildRunnerInfo{
    public override fun getType(): String = bean.runTypeName
    public override fun canRun(agentConfiguration: BuildAgentConfiguration): Boolean = true
  }
}
