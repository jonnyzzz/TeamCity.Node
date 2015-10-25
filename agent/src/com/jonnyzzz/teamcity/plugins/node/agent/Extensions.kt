/*
 * Copyright 2013-2015 Eugene Petrenko
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
import java.io.IOException
import jetbrains.buildServer.RunBuildException
import java.io.File
import java.io.FileOutputStream
import java.io.BufferedOutputStream
import java.io.OutputStreamWriter
import jetbrains.buildServer.util.FileUtil

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 16.08.13 22:40
 */

fun BuildProgressLogger.block(name: String, description: String = name): () -> Unit {
  this.logMessage(DefaultMessagesInfo.createBlockStart(name, description, "jonnyzzz"))

  return{
    this.logMessage(DefaultMessagesInfo.createBlockEnd(name, "jonnyzzz"))
  }
}

inline fun <T> AgentRunningBuild.logging(f: BuildProgressLogger.() -> T): T = buildLogger.f()
inline fun <T> BuildRunnerContext.logging(f: BuildProgressLogger.() -> T): T = build.logging(f)


/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 18.08.13 22:38
 */

inline fun <T> io(errorMessage: String, body: () -> T): T {
  try {
    return body()
  } catch (e: IOException) {
    throw RunBuildException("$errorMessage. ${e.message}", e)
  }
}

fun writeUTF(file:File, text:String) {
  val writer = OutputStreamWriter(BufferedOutputStream(FileOutputStream(file)), "utf-8")
  try {
    writer.write(text)
  } finally {
    FileUtil.close(writer)
  }
}
