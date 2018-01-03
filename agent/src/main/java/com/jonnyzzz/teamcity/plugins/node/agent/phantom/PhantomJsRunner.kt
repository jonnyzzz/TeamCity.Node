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

package com.jonnyzzz.teamcity.plugins.node.agent.phantom

import jetbrains.buildServer.agent.runner.CommandLineBuildServiceFactory
import jetbrains.buildServer.agent.runner.CommandLineBuildService
import jetbrains.buildServer.agent.AgentBuildRunnerInfo
import jetbrains.buildServer.agent.BuildAgentConfiguration
import com.jonnyzzz.teamcity.plugins.node.common.NodeBean
import com.jonnyzzz.teamcity.plugins.node.agent.JsService
/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 12.01.13 0:52
 */

class PhantomJsRunnerService() : CommandLineBuildServiceFactory {
  val bean = NodeBean()

  override fun createService(): CommandLineBuildService {
    return PhantomJsService()
  }

  override fun getBuildRunnerInfo(): AgentBuildRunnerInfo = object : AgentBuildRunnerInfo{
    override fun getType(): String = bean.runTypeNamePhantomJs
    override fun canRun(agentConfiguration: BuildAgentConfiguration): Boolean = true
  }
}

class PhantomJsService() : JsService() {
  override fun getToolPath(): String? = runnerParameters[bean.toolPathKey]

  override fun getToolName(): String = "phantom"

  override fun getGeneratedScriptExt(): String
          = "." + (runnerParameters[bean.phantomJsExtensionKey] ?: bean.phantomJsExtensionDefault)
}
