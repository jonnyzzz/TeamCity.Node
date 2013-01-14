package com.jonnyzzz.teamcity.plugins.node.agent.processes

import jetbrains.buildServer.RunBuildException
import jetbrains.buildServer.agent.BuildFinishedStatus
import jetbrains.buildServer.agent.BuildProcess
import java.util.concurrent.atomic.AtomicBoolean

public abstract class BuildProcessBase(): BuildProcess {
  private val myIsInterrupted: AtomicBoolean = AtomicBoolean()
  private val myIsFinished: AtomicBoolean = AtomicBoolean()

  public override fun isInterrupted(): Boolean {
    return myIsInterrupted.get()
  }

  public override fun isFinished(): Boolean {
    return myIsFinished.get()
  }

  public override fun interrupt(): Unit {
    myIsInterrupted.set(true)
    interruptImpl()
  }

  public override fun waitFor(): BuildFinishedStatus {
    try
    {
      if (isInterrupted())
        return BuildFinishedStatus.INTERRUPTED

      val status = waitForImpl()
      if (isInterrupted())
        return BuildFinishedStatus.INTERRUPTED

      return status
    } finally {
      myIsFinished.set(true)
    }
  }
  protected abstract fun waitForImpl(): BuildFinishedStatus

  protected open fun interruptImpl(): Unit { }
  public override fun start(): Unit { }
}
