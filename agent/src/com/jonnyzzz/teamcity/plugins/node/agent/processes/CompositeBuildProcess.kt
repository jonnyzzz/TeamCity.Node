package com.jonnyzzz.teamcity.plugins.node.agent.processes

import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.atomic.AtomicReference
import jetbrains.buildServer.agent.BuildFinishedStatus
import jetbrains.buildServer.agent.BuildProcess

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
public trait CompositeProcessBuilder {
  fun step(p: () -> Unit)
  fun step(p: BuildProcess)
  fun step(p: DelegatingProcessAction)
}

public fun action(p:() -> BuildProcess) : DelegatingProcessAction = object:DelegatingProcessAction {
  override fun startImpl(): BuildProcess = p()
}

public fun compositeBuildProcess(builder: CompositeProcessBuilder.() -> Unit): BuildProcess {
  val proc = CompositeBuildProcessImpl()
  object:CompositeProcessBuilder {
    override fun step(p: () -> Unit) {
      step(object:BuildProcessBase() {
        protected override fun waitForImpl(): BuildFinishedStatus {
          p()
          BuildFinishedStatus.FINISHED_SUCCESS
        }
      })
    }

    override fun step(p: DelegatingProcessAction) {
      step(DelegatingBuildProcess(p))
    }

    override fun step(p: BuildProcess) {
      proc.pushBuildProcess(p)
    }
  }.builder()
  return proc
}

public trait BuildProcessContinuation {
  fun pushBuildProcess(process: BuildProcess)
}


public trait CompositeBuildProcess : BuildProcess, BuildProcessContinuation {
}

public open class CompositeBuildProcessImpl : BuildProcessBase(), CompositeBuildProcess {
  private val myProcessList: BlockingQueue<BuildProcess> = LinkedBlockingQueue<BuildProcess>()
  private val myCurrentProcess: AtomicReference<BuildProcess> = AtomicReference<BuildProcess>()

  public override fun pushBuildProcess(process: BuildProcess) {
    myProcessList.add(process)
  }

  protected override fun interruptImpl() {
    val process: BuildProcess? = myCurrentProcess.get()
    if (process != null)
      process.interrupt()
  }

  public override fun start() {
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
