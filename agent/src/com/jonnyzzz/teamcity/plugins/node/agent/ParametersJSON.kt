/*
 * Copyright 2013-2014 Eugene Petrenko
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
import java.util.TreeMap
import com.google.gson.Gson


private fun BaseService.generateTeamCityProperties(builder : MutableMap<String,String>.() -> Unit) : File {
  val file = io("Failed to create temp file") {
    getAgentTempDirectory() tempFile TempFileName("teamcity", ".json")
  }
  disposeLater { file.smartDelete() }

  val map = TreeMap<String, String>()
  map.builder()
  val text = Gson().toJson(map)!!
  io("Failed to create parameters file: ${file}") {
    writeUTF(file, text)
  }
  return file
}

public fun BaseService.generateSystemParametersJSON() : File
        = generateTeamCityProperties { putAll(getBuildParameters().getSystemProperties()) }

public fun BaseService.generateAllParametersJSON(): File
        = generateTeamCityProperties {
          putAll(getConfigParameters())
          putAll(getBuildParameters().getAllParameters())
        }
