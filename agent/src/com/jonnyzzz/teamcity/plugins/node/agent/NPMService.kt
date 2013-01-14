package com.jonnyzzz.teamcity.plugins.node.agent

import jetbrains.buildServer.agent.runner.BuildServiceAdapter
import com.jonnyzzz.teamcity.plugins.node.common.NPMBean
import jetbrains.buildServer.agent.runner.ProgramCommandLine
import jetbrains.buildServer.RunBuildException

/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 14.01.13 22:13
 */
public class NPMService() : ServiceBase() {
  private val bean = NPMBean()


  public override fun makeProgramCommandLine(): ProgramCommandLine {
    val arguments = arrayListOf<String>()

    arguments add "/c"
    arguments add "npm"
    //add [options section]
    arguments addAll fetchArguments(bean.commandLineParameterKey)
    arguments add "install"

    arguments add "&"
    arguments add "npm"
    arguments add "test"

    //TODO: commandline arguments
    return createProgramCommandline("cmd", arguments)
  }
}

