package com.jonnyzzz.teamcity.plugins.node.agent.processes

import jetbrains.buildServer.RunBuildException
import jetbrains.buildServer.agent.BuildFinishedStatus
import jetbrains.buildServer.agent.BuildProcess
import java.util.concurrent.atomic.AtomicReference

public open class DelegatingBuildProcess(val action: DelegatingProcessAction): BuildProcessBase() {
  private val myReference = AtomicReference<BuildProcess>()

  protected override fun interruptImpl(): Unit {
    super.interruptImpl()

    val process = myReference.get()
    if (process != null)
      process.interrupt()
  }

  protected override fun waitForImpl(): BuildFinishedStatus {
    try
    {
      val process = action.startImpl()
      myReference.set(process)
      if (isInterrupted())
        return BuildFinishedStatus.INTERRUPTED

      process.start()
      return process.waitFor()
    } finally {
      myReference.set(null)
      action.finishedImpl()
    }
  }
}

public trait DelegatingProcessAction {
  open fun startImpl(): BuildProcess
  open fun finishedImpl(): Unit
}
