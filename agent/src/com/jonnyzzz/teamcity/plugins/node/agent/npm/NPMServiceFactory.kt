package com.jonnyzzz.teamcity.plugins.node.agent.npm

import com.jonnyzzz.teamcity.plugins.node.agent.processes.ExecutorProxy
import com.jonnyzzz.teamcity.plugins.node.common.NPMBean
import jetbrains.buildServer.agent.runner.CommandLineBuildServiceFactory
import jetbrains.buildServer.agent.runner.CommandLineBuildService
import jetbrains.buildServer.agent.AgentBuildRunnerInfo
import jetbrains.buildServer.agent.BuildAgentConfiguration
import jetbrains.buildServer.agent.runner.MultiCommandBuildSessionFactory
import jetbrains.buildServer.agent.BuildRunnerContext
import jetbrains.buildServer.agent.runner.MultiCommandBuildSession
import jetbrains.buildServer.agent.runner.CommandExecution
import jetbrains.buildServer.agent.BuildFinishedStatus
import jetbrains.buildServer.agent.runner.LoggingProcessListener
import jetbrains.buildServer.agent.BuildProgressLogger
import jetbrains.buildServer.agent.runner.ProgramCommandLine
import jetbrains.buildServer.agent.runner.TerminationAction
import jetbrains.buildServer.agent.runner.SimpleProgramCommandLine
import com.jonnyzzz.teamcity.plugins.node.common.fetchArguments
import jetbrains.buildServer.serverSide.BuildTypeOptions
import jetbrains.buildServer.agent.impl.buildStages.BuildFinishStage
import com.jonnyzzz.teamcity.plugins.node.agent.processes.Execution
import com.jonnyzzz.teamcity.plugins.node.agent.processes.execution

/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 15.01.13 22:55
 */

public class NPMServiceFactory(val proxy : ExecutorProxy) : MultiCommandBuildSessionFactory {
  val bean = NPMBean()

  public override fun createSession(p0: BuildRunnerContext): MultiCommandBuildSession = NPMSession(proxy, p0)

  public override fun getBuildRunnerInfo(): AgentBuildRunnerInfo = object : AgentBuildRunnerInfo{
    public override fun getType(): String = bean.runTypeName
    public override fun canRun(agentConfiguration: BuildAgentConfiguration): Boolean = true
  }
}

public class NPMSession(val proxy : ExecutorProxy,
                        val runner : BuildRunnerContext) : MultiCommandBuildSession {
  private val bean = NPMBean()
  private var iterator : Iterator<NPMCommandExecution> = listOf<NPMCommandExecution>().iterator()
  private var previousStatus = BuildFinishedStatus.FINISHED_SUCCESS

  public override fun sessionStarted() {
    val logger = runner.getBuild().getBuildLogger()
    val extra = runner.getRunnerParameters()[bean.commandLineParameterKey].fetchArguments()
    val checkExitCode = runner.getBuild().getBuildTypeOptionValue(BuildTypeOptions.BT_FAIL_ON_EXIT_CODE) ?: true
    val onExitCode : (Int) -> Unit =
        if (!checkExitCode)
          {code -> }
        else
          {code -> previousStatus = when(code) {
            0 -> BuildFinishedStatus.FINISHED_SUCCESS
            else -> BuildFinishedStatus.FINISHED_FAILED
          }
          }

    iterator =
            bean.parseCommands(runner.getRunnerParameters()[bean.npmCommandsKey])
                    .map{ NPMCommandExecution(
                    logger,
                    "npm ${it}",
                    commandline(runner, Execution("npm", extra + it)),
                    onExitCode)
            }.iterator()
  }

  private fun commandline(runner : BuildRunnerContext, e : Execution) : ProgramCommandLine {
    val p = proxy.proxy(e)
    return SimpleProgramCommandLine(
                runner,
                p.program,
                p.arguments)
  }

  public override fun getNextCommand(): CommandExecution? {
    if (previousStatus != BuildFinishedStatus.FINISHED_SUCCESS) return null
    if (iterator.hasNext()) return iterator.next()
    return null;
  }

  public override fun sessionFinished(): BuildFinishedStatus? {
    return previousStatus
  }
}

public class NPMCommandExecution(val logger : BuildProgressLogger,
                                 val blockName : String,
                                 val cmd : ProgramCommandLine,
                                 val onFinished : (Int) -> Unit) : LoggingProcessListener(logger), CommandExecution {

  public override fun makeProgramCommandLine(): ProgramCommandLine = cmd

  public override fun beforeProcessStarted() {
    logger.activityStarted(blockName, "npm");
  }

  public override fun processFinished(exitCode: Int) {
    super<LoggingProcessListener>.processFinished(exitCode)
    logger.activityFinished(blockName, "npm");
    onFinished(exitCode)
  }

  public override fun interruptRequested(): TerminationAction = TerminationAction.KILL_PROCESS_TREE
  public override fun isCommandLineLoggingEnabled(): Boolean = true
}
