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

package com.jonnyzzz.teamcity.plugins.node.agent

import com.jonnyzzz.teamcity.plugins.node.common.*
import jetbrains.buildServer.agent.*
import jetbrains.buildServer.util.EventDispatcher
import com.jonnyzzz.teamcity.plugins.node.agent.processes.ProcessExecutor
import com.jonnyzzz.teamcity.plugins.node.agent.processes.execution
import com.jonnyzzz.teamcity.plugins.node.agent.processes.succeeded
import java.io.File
import com.jonnyzzz.teamcity.plugins.node.common.NVMBean
import com.jonnyzzz.teamcity.plugins.node.common.NPMBean
import com.jonnyzzz.teamcity.plugins.node.common.GruntBean
import com.jonnyzzz.teamcity.plugins.node.common.BowerBean

/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 12.01.13 1:04
 */
class NodeToolsDetector(events: EventDispatcher<AgentLifeCycleListener>,
                               val config: BuildAgentConfiguration,
                               val exec : ProcessExecutor) {
  private val LOG = log4j(this.javaClass)

  fun detectNVMTool() {
    with(config.systemInfo) {
      when {
        isWindows && !File(System.getenv("APPDATA") + "\\nvm\\nvm.exe").isFile -> {
          log4j(javaClass).info("Node NVM installer runner is not available.")
        }

        !(isMac || isUnix || isWindows) -> {
          log4j(javaClass).info("Node NVM installer runner is not available")
        }

        !isWindows &&!File("/bin/bash").isFile -> {
            log4j(javaClass).info("Node NVM installer runner is not available: /bin/bash not found")
          }

        else -> {
          val ref = NVMBean().NVMUsed
          with(config) {
            addConfigurationParameter(NPMBean().nodeJSNPMConfigurationParameter, ref)
            addConfigurationParameter(NodeBean().nodeJSConfigurationParameter, ref)
            addConfigurationParameter(NVMBean().NVMAvailable, "yes")
          }
        }
      }
    }
  }

  fun detectNodeTool(executable: String, configParameterName: String, versionPreProcess: (String) -> String = {it}) {
    val run = exec.runProcess(execution(executable, "--version"))
    when {
      run.succeeded() -> {
        val version = versionPreProcess(run.stdOut.trim())
        LOG.info("$executable $version was detected")
        config.addConfigurationParameter(configParameterName, version)
        return
      }
      else -> {
        LOG.info("$executable was not found or failed, exitcode: ${run.exitCode}")
        LOG.info("StdOut: ${run.stdOut}")
        LOG.info("StdErr: ${run.stdErr}")
      }
    }
  }

  init {
    events.addListener(object : AgentLifeCycleAdapter() {
      override fun beforeAgentConfigurationLoaded(agent: BuildAgent) {
        detectNVMTool()

        detectNodeTool("node", NodeBean().nodeJSConfigurationParameter) {
          it.trimStart("v")
        }

        detectNodeTool("npm", NPMBean().nodeJSNPMConfigurationParameter)
        detectNodeTool("yarn", YarnBean().nodeJSYarnConfigurationParameter)

        detectNodeTool("grunt", GruntBean().gruntConfigurationParameter) {
          it.trimStart("grunt-cli").trim().trimStart("v")
        }

        detectNodeTool("bower", BowerBean().bowerConfigurationParameter)

        detectNodeTool("gulp", GulpBean().gulpConfigurationParameter) {
          val firstLine = it.lines()[0].trim()

          val lastSpaceIndex = firstLine.lastIndexOf(' ')
          if (lastSpaceIndex > 0) {
            firstLine.substring(lastSpaceIndex).trim()
          } else {
            firstLine
          }
        }
      }
    })
  }
}
