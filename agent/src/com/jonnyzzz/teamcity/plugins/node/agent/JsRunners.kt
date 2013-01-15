package com.jonnyzzz.teamcity.plugins.node.agent

import jetbrains.buildServer.agent.runner.CommandLineBuildServiceFactory
import jetbrains.buildServer.agent.runner.CommandLineBuildService
import jetbrains.buildServer.agent.AgentBuildRunnerInfo
import jetbrains.buildServer.agent.BuildAgentConfiguration
import com.jonnyzzz.teamcity.plugins.node.common.NodeBean
import jetbrains.buildServer.agent.runner.BuildServiceAdapter
import jetbrains.buildServer.agent.runner.ProgramCommandLine
import com.jonnyzzz.teamcity.plugins.node.common.log4j
import com.jonnyzzz.teamcity.plugins.node.common.NPMBean

/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 12.01.13 0:52
 */
public class NodeJsRunnerService() : CommandLineBuildServiceFactory {
  val bean = NodeBean()

  public override fun createService(): CommandLineBuildService {
    return NodeJsService()
  }

  public override fun getBuildRunnerInfo(): AgentBuildRunnerInfo = object : AgentBuildRunnerInfo{
    public override fun getType(): String = bean.runTypeNameNodeJs
    public override fun canRun(agentConfiguration: BuildAgentConfiguration): Boolean = true
  }
}

public class PhantomJsRunnerService() : CommandLineBuildServiceFactory {
  val bean = NodeBean()

  public override fun createService(): CommandLineBuildService {
    return PhantomJsService()
  }

  public override fun getBuildRunnerInfo(): AgentBuildRunnerInfo = object : AgentBuildRunnerInfo{
    public override fun getType(): String = bean.runTypeNamePhantomJs
    public override fun canRun(agentConfiguration: BuildAgentConfiguration): Boolean = true
  }
}


public class NodeJsNPMRunnerService() : CommandLineBuildServiceFactory {
  val bean = NPMBean()

  public override fun createService(): CommandLineBuildService {
    return NPMService()
  }

  public override fun getBuildRunnerInfo(): AgentBuildRunnerInfo = object : AgentBuildRunnerInfo{
    public override fun getType(): String = bean.runTypeName
    public override fun canRun(agentConfiguration: BuildAgentConfiguration): Boolean = true
  }
}
