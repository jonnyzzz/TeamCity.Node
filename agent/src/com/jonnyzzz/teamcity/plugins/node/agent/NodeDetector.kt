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

package com.jonnyzzz.teamcity.plugins.node.agent

import com.jonnyzzz.teamcity.plugins.node.common.*
import com.jonnyzzz.teamcity.plugins.node.common.NodeBean
import com.jonnyzzz.teamcity.plugins.node.common.log4j
import jetbrains.buildServer.agent.AgentLifeCycleAdapter
import jetbrains.buildServer.agent.AgentLifeCycleListener
import jetbrains.buildServer.agent.BuildAgent
import jetbrains.buildServer.agent.BuildAgentConfiguration
import jetbrains.buildServer.util.EventDispatcher
import com.jonnyzzz.teamcity.plugins.node.agent.processes.ProcessExecutor
import com.jonnyzzz.teamcity.plugins.node.agent.processes.execution
import com.jonnyzzz.teamcity.plugins.node.agent.processes.succeeded
import java.io.File
import com.jonnyzzz.teamcity.plugins.node.common.NVMBean
import com.jonnyzzz.teamcity.plugins.node.common.NPMBean
import com.jonnyzzz.teamcity.plugins.node.common.GruntBean

/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 12.01.13 1:04
 */
public class NodeToolsDetector(events: EventDispatcher<AgentLifeCycleListener>,
                               val config: BuildAgentConfiguration,
                               val exec : ProcessExecutor) {
  private val LOG = log4j(this.javaClass)
  private val bean = NodeBean();

  fun detectNVMTool() {
    with(config.getSystemInfo()) {
      when {
        isWindows() -> {
          log4j(javaClass).info("Node NVM installer runner is not availabe: Windows is not supported")
        }

        !(isMac() || isUnix()) -> {
          log4j(javaClass).info("Node NVM installer runner is not availabe")
        }

        !File("/bin/bash").isFile() -> {
          log4j(javaClass).info("Node NVM installer runner is not availabe: /bin/bash not found")
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

  fun detectNodeTool(executable: String, configParameterName: String, versionPreprocess : (String) -> String = {it}) {
    val run = exec runProcess execution(executable, "--version")
    when {
      run.succeeded() -> {
        val version = versionPreprocess(run.stdOut.trim())
        LOG.info("${executable} ${version} was detected")
        config.addConfigurationParameter(configParameterName, version)
        return
      }
      else -> {
        LOG.info("${executable} was not found or failed, exitcode: ${run.exitCode}")
        LOG.info("StdOut: ${run.stdOut}")
        LOG.info("StdErr: ${run.stdErr}")
      }
    }
  }

  {
    events.addListener(object : AgentLifeCycleAdapter() {
      public override fun beforeAgentConfigurationLoaded(agent: BuildAgent) {
        detectNVMTool()

        detectNodeTool("node", NodeBean().nodeJSConfigurationParameter) {
          it trimStart "v"
        }

        detectNodeTool("npm", NPMBean().nodeJSNPMConfigurationParameter)

        detectNodeTool("grunt", GruntBean().gruntConfigurationParameter) {
          it.trimStart("grunt-cli").trim().trimStart("v")
        }

        detectNodeTool("gulp", GulpBean().gruntConfigurationParameter) {
          it.substring(it.lastIndexOf(' ')).trim()
        }
      }
    })
  }
}
