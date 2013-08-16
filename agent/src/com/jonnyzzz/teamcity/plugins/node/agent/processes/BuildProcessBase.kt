package com.jonnyzzz.teamcity.plugins.node.agent.processes
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

import jetbrains.buildServer.RunBuildException
import jetbrains.buildServer.agent.BuildFinishedStatus
import jetbrains.buildServer.agent.BuildProcess
import java.util.concurrent.atomic.AtomicBoolean

public abstract class BuildProcessBase: BuildProcess {
  private val myIsInterrupted: AtomicBoolean = AtomicBoolean()
  private val myIsFinished: AtomicBoolean = AtomicBoolean()

  public override fun isInterrupted(): Boolean {
    return myIsInterrupted.get()
  }

  public override fun isFinished(): Boolean {
    return myIsFinished.get()
  }

  public override fun interrupt() {
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

  protected open fun interruptImpl() { }
  public override fun start() { }
}
