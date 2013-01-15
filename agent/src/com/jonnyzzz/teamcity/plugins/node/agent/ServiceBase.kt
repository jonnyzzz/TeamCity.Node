package com.jonnyzzz.teamcity.plugins.node.agent

import com.jonnyzzz.teamcity.plugins.node.common.log4j
import jetbrains.buildServer.RunBuildException
import java.io.IOException
import jetbrains.buildServer.agent.runner.BuildServiceAdapter
import com.jonnyzzz.teamcity.plugins.node.common.isEmptyOrSpaces
import com.jonnyzzz.teamcity.plugins.node.common.splitHonorQuotes
import jetbrains.buildServer.agent.runner.ProgramCommandLine
import com.jonnyzzz.teamcity.plugins.node.common.fetchArguments

/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 14.01.13 22:14
 */

public abstract class ServiceBase : BuildServiceAdapter() {
  private val disposables = linkedListOf<() -> Unit>()

  protected fun disposeLater(action : () -> Unit) {
    disposables add action
  }

  public override fun afterProcessFinished() {
    super<BuildServiceAdapter>.afterProcessFinished()

    disposables.forEach { it() }
  }

  protected inline fun io<T>(errorMessage: String, body: () -> T): T {
    try {
      return body()
    } catch (e: IOException) {
      throw RunBuildException("${errorMessage}. ${e.getMessage()}", e)
    }
  }

  protected fun fetchArguments(runnerParametersKey : String) : Collection<String> {
    return getRunnerParameters().get(runnerParametersKey).fetchArguments()
  }

  val LOG = log4j(this.javaClass)
}
