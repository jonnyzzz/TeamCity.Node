package com.jonnyzzz.teamcity.plugins.node.agent
/*
 * Copyright 2000-2013 Eugene Petrenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
