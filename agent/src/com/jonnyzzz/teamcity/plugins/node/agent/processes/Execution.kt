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

package com.jonnyzzz.teamcity.plugins.node.agent.processes

import com.jonnyzzz.teamcity.plugins.node.agent.*
import jetbrains.buildServer.agent.BuildAgentConfiguration
import com.intellij.execution.configurations.GeneralCommandLine
import jetbrains.buildServer.SimpleCommandLineProcessRunner
import jetbrains.buildServer.SimpleCommandLineProcessRunner.RunCommandEventsAdapter
import com.jonnyzzz.teamcity.plugins.node.common.log4j
import jetbrains.buildServer.util.FileUtil
import jetbrains.buildServer.agent.BuildRunnerContext
import com.jonnyzzz.teamcity.plugins.node.common.tempFile
import com.jonnyzzz.teamcity.plugins.node.common.TempFileName
import com.jonnyzzz.teamcity.plugins.node.common.smartDelete

/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 15.01.13 22:23
 */

interface ExecutorProxy {
  ///proxy execution, for example by adding cmd call on windows or /bin/sh on linux or mac
  fun proxy(e : Execution) : Execution
}

data public class Execution(val program : String, public val arguments : List<String>)
fun execution(program : String, vararg arguments : String) : Execution = Execution(program, arguments.toList())

fun Execution.shift(newProgram : String, vararg arguments : String)
        = Execution(newProgram, arguments.toList() + listOf(this.program) + this.arguments)

class ShellBasedExecutionProxy(val config : BuildAgentConfiguration) : ExecutorProxy {
  override fun proxy(e: Execution): Execution {
    if (config.systemInfo.isWindows) {
      return e.shift("cmd.exe", "/c")
    }

    return e
  }
}

abstract class ScriptWrappingCommandLineGenerator<ProgramCommandLine>(protected val build: BuildRunnerContext) {
  protected abstract fun execute(executable: String, args: List<String>): ProgramCommandLine
  protected abstract fun disposeLater(action: () -> Unit)

  fun generate(executable: String, arguments: List<String>): ProgramCommandLine {
    log4j(javaClass).info("Executing $executable via wrapping script")
    build.build.buildLogger.message("Executing $executable via wrapping shell script")

    if (build.build.agentConfiguration.systemInfo.isWindows) {
      return execute("cmd", arrayListOf("/c", executable) + arguments)
    } else {
      val scriptToRun = io("Failed to create temp file") {
        build.build.agentTempDirectory.tempFile(TempFileName("wrapper", ".sh"))
      }
      disposeLater { scriptToRun.smartDelete() }
      io("Generate wrapping bash script") {
        FileUtil.writeFileAndReportErrors(scriptToRun, "#!/bin/bash\n$executable \"$@\"");
        FileUtil.setExectuableAttribute(scriptToRun.path, true)
      }

      return execute(scriptToRun.path, arguments)
    }
  }
}

data public class ExecutionResult(val stdOut : String,
                                  val stdErr : String,
                                  val exitCode : Int,
                                  val error : Throwable?)

fun ExecutionResult.succeeded() : Boolean = this.error == null && exitCode == 0

interface ProcessExecutor {
  fun runProcess(p : Execution) : ExecutionResult
}


class ProcessExecutorImpl : ProcessExecutor {
  private val LOG = log4j(this.javaClass)

  override fun runProcess(p: Execution): ExecutionResult {
    LOG.info("Starting process: $p");

    val cmd = GeneralCommandLine()
    cmd.exePath = p.program
    cmd.addParameters(p.arguments);
    val run = SimpleCommandLineProcessRunner.runCommand(
            cmd, byteArrayOf(), object : RunCommandEventsAdapter() {
      override fun getOutputIdleSecondsTimeout(): Int? = 239
    })!!

    val result = ExecutionResult(
            run.stdout.trim(),
            run.stderr.trim(),
            run.exitCode,
            run.exception)

    LOG.debug("Execution completed: $result")
    return result;
  }
}

class ProxyAwareExecutorImpl(val host : ProcessExecutor,
                                    val proxy : ExecutorProxy) : ProcessExecutor {
  override fun runProcess(p: Execution): ExecutionResult {
    return host.runProcess(proxy.proxy(p))
  }
}
