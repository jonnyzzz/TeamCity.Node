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

import com.jonnyzzz.teamcity.plugins.node.agent.processes.ProcessExecutor
import com.jonnyzzz.teamcity.plugins.node.agent.processes.execution
import com.jonnyzzz.teamcity.plugins.node.agent.processes.succeeded
import com.jonnyzzz.teamcity.plugins.node.common.*
import jetbrains.buildServer.agent.AgentLifeCycleAdapter
import jetbrains.buildServer.agent.AgentLifeCycleListener
import jetbrains.buildServer.agent.BuildAgent
import jetbrains.buildServer.agent.BuildAgentConfiguration
import jetbrains.buildServer.util.EventDispatcher
import java.io.File

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
        isWindows -> {
          LOG.info("Node NVM installer runner is not available: Windows is not supported")
        }

        !(isMac || isUnix) -> {
          LOG.info("Node NVM installer runner is not available")
        }

        !File("/bin/bash").isFile -> {
          LOG.info("Node NVM installer runner is not available: /bin/bash not found")
        }

        else -> {
          val ref = NVMBean().NVMUsed
          with(config) {
            addConfigurationParameter(NPMBean().nodeJSNPMConfigurationParameter, ref)
            addConfigurationParameter(NodeBean().nodeJSConfigurationParameter, ref)
            addConfigurationParameter(NVMBean().NVMInstallable, "yes")
          }
        }
      }
    }

    val nvm_dir = locateInstalledNVM()
    if (nvm_dir != null) {
      val version = getNVMVersion(nvm_dir) ?: "N/A"
      LOG.info("NVM installation detected at '$nvm_dir' with version '$version'")
      val versions = getNVMInstalledNodeVersions(nvm_dir)?.joinToString(",").orEmpty()
      LOG.info("NVM installed versions are: '$versions'")
      with(config) {
        addConfigurationParameter(NVMBean().Path, nvm_dir)
        addConfigurationParameter(NVMBean().InstalledVersions, versions)
        addConfigurationParameter(NVMBean().Version, version)
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

        detectNodeTool("grunt", GruntBean().gruntConfigurationParameter) {
          it.trimStart("grunt-cli").trim().trimStart("v")
        }

        detectNodeTool("gulp", GulpBean().gulpConfigurationParameter) {
          val firstLine = it.split("[\r\n]+")[0].trim()

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

  private fun locateInstalledNVM(): String? {
    if (!(config.systemInfo.isUnix || config.systemInfo.isMac)) {
      LOG.info("NVM supported only on Unix and Mac OSX")
      return null
    }

    val nvm_home = System.getenv("NVM_HOME")
    if (nvm_home != null && isNVMInstallation(File(nvm_home))) return nvm_home

    val home = System.getProperty("user.home")
    if (home != null) {
      val candidate = File(home, ".nvm")
      if (isNVMInstallation(candidate)) return candidate.absolutePath
    }

    return null
  }

  private fun getNVMInstalledNodeVersions(nvm_dir: String): List<String>? {
    // Use next command (checked with nvm 0.7.0, 0.31.1) instead of 'nvm ls' as it returns simple list without any colors
    // bash -c ". $NVM_DIR/nvm.sh; echo \"\$(nvm_ls)\""
    val run = runNVMAware(nvm_dir, "echo \"$(nvm_ls)\"")
    if (!run.succeeded()) {
      LOG.warn("Failed to detect installed nvm node.js versions: ${run.stdErr}")
      return null
    }
    return run.stdOut.lineSequence().filter { !it.isEmptyOrSpaces() }.toList()
  }

  private fun getNVMVersion(nvm_dir: String): String? {
    // bash -c ". $NVM_DIR/nvm.sh && nvm --version"
    val run = runNVMAware(nvm_dir, "nvm --version")
    if (!run.succeeded()) {
      LOG.warn("Failed to detect version of nvm at '$nvm_dir': ${run.stdErr}")
      return null
    }
    return run.stdOut.lineSequence().first { !it.isEmptyOrSpaces() }.orEmpty()
  }

  private fun runNVMAware(nvm_dir: String, command: String) = exec.runProcess(execution("bash", "-c", "source \"${nvm_dir.removeSuffix("/")}/nvm.sh\" && $command"))

  private fun isNVMInstallation(root: File): Boolean {
    return root.isDirectory && File(root, "nvm.sh").isFile && File(root, "nvm-exec").isFile
  }
}
