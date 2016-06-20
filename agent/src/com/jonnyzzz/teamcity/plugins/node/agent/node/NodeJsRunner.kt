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
package com.jonnyzzz.teamcity.plugins.node.agent.node

import jetbrains.buildServer.agent.runner.CommandLineBuildServiceFactory
import jetbrains.buildServer.agent.runner.CommandLineBuildService
import jetbrains.buildServer.agent.AgentBuildRunnerInfo
import jetbrains.buildServer.agent.BuildAgentConfiguration
import com.jonnyzzz.teamcity.plugins.node.common.NodeBean
import com.jonnyzzz.teamcity.plugins.node.agent.JsService
import com.jonnyzzz.teamcity.plugins.node.common.isEmptyOrSpaces

class NodeJsRunnerService() : CommandLineBuildServiceFactory {
  val bean = NodeBean()

  override fun createService(): CommandLineBuildService {
    return NodeJsService()
  }

  override fun getBuildRunnerInfo(): AgentBuildRunnerInfo = object : AgentBuildRunnerInfo{
    override fun getType(): String = bean.runTypeNameNodeJs
    override fun canRun(agentConfiguration: BuildAgentConfiguration): Boolean = true
  }
}

class NodeJsService() : JsService() {
  override fun getToolPath(): String? {
    val path = runnerParameters[bean.toolPathKey]
    if (path == null || path.isEmptyOrSpaces()) return "node"
    return path.trim()
  }

  override fun getToolName(): String  = "node"
  override fun getGeneratedScriptExt(): String = ".js"
}
