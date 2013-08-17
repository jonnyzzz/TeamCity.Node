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

package com.jonnyzzz.teamcity.plugins.node.agent.processes

import jetbrains.buildServer.agent.BuildProcess
import jetbrains.buildServer.agent.BuildFinishedStatus

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 17.08.13 12:31
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
