package com.jonnyzzz.teamcity.plugins.node.agent.processes

import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.atomic.AtomicReference
import jetbrains.buildServer.agent.BuildFinishedStatus
import jetbrains.buildServer.agent.BuildProcess

public open class CompositeBuildProcessImpl() : BuildProcessBase(), CompositeBuildProcess {
  private val myProcessList: BlockingQueue<BuildProcess> = LinkedBlockingQueue<BuildProcess>()
  private val myCurrentProcess: AtomicReference<BuildProcess> = AtomicReference<BuildProcess>()

  public override fun pushBuildProcess(process: BuildProcess): Unit {
    myProcessList.add(process)
  }

  protected override fun interruptImpl(): Unit {
    val process: BuildProcess? = myCurrentProcess.get()
    if (process != null)
      process.interrupt()
  }

  public override fun start(): Unit {

  }

  protected override fun waitForImpl(): BuildFinishedStatus {
    if (isInterrupted())
      return BuildFinishedStatus.INTERRUPTED

    val proc = myProcessList.poll()
    if (proc != null) {
      myCurrentProcess.set(proc)
      try {
        proc.start()
        val status = proc.waitFor()
        if (status != BuildFinishedStatus.INTERRUPTED && status != BuildFinishedStatus.FINISHED_SUCCESS)
          return status
      } finally {
        myCurrentProcess.set(null)
      }

      if (isInterrupted())
        return BuildFinishedStatus.INTERRUPTED

      return waitForImpl()
    }

    if (isInterrupted())
      return BuildFinishedStatus.INTERRUPTED

    return BuildFinishedStatus.FINISHED_SUCCESS
  }
}
