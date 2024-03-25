/*
 * Copyright 2013-2024 Eugene Petrenko
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

import jetbrains.buildServer.agent.*
import jetbrains.buildServer.agent.impl.BuildRunnerContextFactory
import jetbrains.buildServer.agent.impl.RunnerParametersUpdater
import jetbrains.buildServer.agent.impl.runner.CallRunnerService
import jetbrains.buildServer.agentServer.BuildRunnerData
import jetbrains.buildServer.runner.SimpleRunnerConstants
import java.util.concurrent.atomic.AtomicInteger


class CommandRunnerSubProcess(private val callRunnerService: CallRunnerService,
                              private val runnerParametersUpdater: RunnerParametersUpdater) {

    private val customStepsCounter = AtomicInteger()

    fun createBuildProcess(build: AgentRunningBuild, commandLine: String, workingDir: String): BuildProcess {
        val ctx = createBuildRunnerContext(build, SimpleRunnerConstants.TYPE, workingDir)
        ctx.addRunnerParameter(SimpleRunnerConstants.USE_CUSTOM_SCRIPT, "true");
        ctx.addRunnerParameter(SimpleRunnerConstants.SCRIPT_CONTENT, commandLine);

        return callRunnerService.createBuildProcess(build, ctx)
    }

    private fun createBuildRunnerContext(build: AgentRunningBuild, runType: String, workingDir: String): BuildRunnerContext {

        val id = "custom_" + customStepsCounter.incrementAndGet();
        val runnerParams = runnerParametersUpdater.fetchRunnerParameters(build, BuildRunnerData(
                id,
                runType,
                "",
                false,
                emptyMap<String, String>(),
                emptyMap<String, String>()
        ))

        val service = (build as AgentRunningBuildEx).getPerBuildService(BuildRunnerContextFactory::class.java)
                ?: throw RuntimeException("Failed to find " + BuildRunnerContextFactory::class.java + " in build context")

        return service.createBuildRunnerContext(build, MyBuildRunnerSettings(id, runType, workingDir, runnerParams))
    }

    class MyBuildRunnerSettings(
            private val id: String,
            private val runType: String,
            private val workingDir: String,
            private val runnerParams: MutableMap<String, String>) : BuildRunnerSettings {

        override fun getRunType(): String = runType

        override fun getName(): String = ""

        override fun getId(): String = id

        override fun isEnabled() = true
        override fun hasChildren() = false
        override fun getChildren(): MutableList<BuildRunnerSettings> = mutableListOf()

        override fun getParent(): BuildRunnerSettings? = null

        override fun getRunnerParameters(): MutableMap<String, String> = runnerParams

        override fun getBuildParameters(): MutableMap<String, String> = mutableMapOf()

        override fun getConfigParameters(): MutableMap<String, String> = mutableMapOf()

        override fun getWorkingDirectory(): String = workingDir
    }

}
