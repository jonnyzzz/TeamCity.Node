package com.jonnyzzz.teamcity.plugins.node.agent.processes

import jetbrains.buildServer.RunBuildException
import jetbrains.buildServer.agent.BuildProcess
import jetbrains.buildServer.agent.BuildRunnerContext
import java.io.File

public trait CommandlineBuildProcessFactory {
  open fun executeCommandLine(hostContext: BuildRunnerContext,
                              program: String,
                              argz: Collection<String>,
                              workingDir: File,
                              additionalEnvironment: Map<String, String>) : BuildProcess
}
