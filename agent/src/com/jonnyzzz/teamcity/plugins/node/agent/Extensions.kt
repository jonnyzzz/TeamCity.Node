/*
 * Copyright 2013-2013 Eugene Petrenko
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

package com.jonnyzzz.teamcity.plugins.node.agent

import jetbrains.buildServer.agent.BuildProgressLogger
import jetbrains.buildServer.messages.DefaultMessagesInfo
import jetbrains.buildServer.agent.BuildRunnerContext
import jetbrains.buildServer.agent.AgentRunningBuild
import com.jonnyzzz.teamcity.plugins.node.agent.processes.DelegatingProcessAction
import jetbrains.buildServer.agent.BuildProcess
import java.io.IOException
import jetbrains.buildServer.RunBuildException


/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 16.08.13 22:40
 */

inline fun BuildProgressLogger.block(name: String, description: String = name): () -> Unit {
  this.logMessage(DefaultMessagesInfo.createBlockStart(name, description, "jonnyzzz"))

  return{
    this.logMessage(DefaultMessagesInfo.createBlockEnd(name, "jonnyzzz"))
  }
}

inline fun <T> BuildProgressLogger.block(name: String, description: String = name, action: BuildProgressLogger.() -> T): T {
  val d = this.block(name, description)
  try {
    return action()
  } finally {
    d()
  }
}

inline fun <T> AgentRunningBuild.logging(f: BuildProgressLogger.() -> T): T = getBuildLogger().f()
inline fun <T> BuildRunnerContext.logging(f: BuildProgressLogger.() -> T): T = getBuild().logging(f)


/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 18.08.13 22:38
 */

inline fun io<T>(errorMessage: String, body: () -> T): T {
  try {
    return body()
  } catch (e: IOException) {
    throw RunBuildException("${errorMessage}. ${e.getMessage()}", e)
  }
}

