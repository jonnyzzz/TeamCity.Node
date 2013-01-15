package com.jonnyzzz.teamcity.plugins.node.agent.processes

import jetbrains.buildServer.agent.BuildAgentConfiguration
import com.intellij.execution.configurations.GeneralCommandLine
import jetbrains.buildServer.SimpleCommandLineProcessRunner
import jetbrains.buildServer.SimpleCommandLineProcessRunner.ProcessRunCallbackAdapter
import com.jonnyzzz.teamcity.plugins.node.common.log4j

/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 15.01.13 22:23
 */

public trait ExecutorProxy {
  ///proxy execution, for example by adding cmd call on windows or /bin/sh on linux or mac
  fun proxy(e : Execution) : Execution
}

data public class Execution(public val program : String, public val arguments : List<String>)
fun execution(program : String, vararg arguments : String) : Execution = Execution(program, arguments.toList())
fun execution(program : String, base : List<String>, vararg arguments : String) : Execution = Execution(program, base + arguments.toList())

fun Execution.shift(newProgram : String, vararg arguments : String)
        = Execution(newProgram, arguments.toList() + listOf(this.program) + this.arguments)

public class IdenticalExecutionProxy : ExecutorProxy {
  override fun proxy(e: Execution): Execution = e
}

public class ShellBasedExecutionProxy(val config : BuildAgentConfiguration) : ExecutorProxy {
  override fun proxy(e: Execution): Execution {
    if (config.getSystemInfo().isWindows()) {
      return e.shift("cmd.exe", "/c")
    }

    return e
  }
}


data public class ExecutionResult(public val stdOut : String,
                                  public val stdErr : String,
                                  public val exitCode : Int,
                                  public val error : Throwable?)

fun ExecutionResult.succeeded() : Boolean = this.error == null && exitCode == 0

public trait ProcessExecutor {
  fun runProcess(p : Execution) : ExecutionResult
}


public class ProcessExecutorImpl : ProcessExecutor {
  private val LOG = log4j(this.javaClass)

  override fun runProcess(p: Execution): ExecutionResult {
    LOG.info("Starting process: ${p}");

    val cmd = GeneralCommandLine()
    cmd.setExePath(p.program)
    cmd.addParameters(p.arguments);
    val run = SimpleCommandLineProcessRunner.runCommand(
            cmd, byteArray(), object : ProcessRunCallbackAdapter() {
      public override fun getOutputIdleSecondsTimeout(): Int? = 1
    })!!

    return ExecutionResult(
            run.getStdout().trim(),
            run.getStderr().trim(),
            run.getExitCode(),
            run.getException())
  }
}

public class ProxyAwareExecutorImpl(val host : ProcessExecutor,
                                    val proxy : ExecutorProxy) : ProcessExecutor {
  override fun runProcess(p: Execution): ExecutionResult {
    return host runProcess (proxy proxy p)
  }
}
