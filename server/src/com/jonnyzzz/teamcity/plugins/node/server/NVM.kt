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

package com.jonnyzzz.teamcity.plugins.node.server

import jetbrains.buildServer.web.openapi.PluginDescriptor
import com.jonnyzzz.teamcity.plugins.node.common.*
import jetbrains.buildServer.serverSide.PropertiesProcessor
import jetbrains.buildServer.serverSide.InvalidProperty
import jetbrains.buildServer.requirements.Requirement
import jetbrains.buildServer.serverSide.buildDistribution.StartingBuildAgentsFilter
import jetbrains.buildServer.serverSide.buildDistribution.AgentsFilterContext
import jetbrains.buildServer.serverSide.buildDistribution.AgentsFilterResult
import jetbrains.buildServer.serverSide.BuildPromotionManager
import jetbrains.buildServer.requirements.RequirementType
import jetbrains.buildServer.serverSide.buildDistribution.SimpleWaitReason

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 16.08.13 21:43
 */
public class NVMRunType(val plugin : PluginDescriptor) : RunTypeBase() {
  private val bean = NVMBean()

  public override fun getType(): String = bean.NVMFeatureType

  public override fun getDisplayName(): String = "Node.js NVM Installer"
  public override fun getDescription(): String = "Install Node.js of specified version using NVM"

  protected override fun getEditJsp(): String = "node.nvm.edit.jsp"
  protected override fun getViewJsp(): String = "node.nvm.view.jsp"

  public override fun getRunnerPropertiesProcessor(): PropertiesProcessor
          = PropertiesProcessor{ arrayListOf<InvalidProperty>() }

  public override fun getDefaultRunnerProperties(): MutableMap<String, String>?
          = hashMapOf(bean.NVMVersion to "0.10")

  public override fun describeParameters(parameters: Map<String, String>): String
          = "Install Node.js v" + parameters[bean.NVMVersion]

  public override fun getRunnerSpecificRequirements(runParameters: Map<String, String>): MutableList<Requirement> {
    return arrayListOf(Requirement(bean.NVMAvailable, null, RequirementType.EXISTS))
  }
}

public class NVMBuildStartPrecondition(val promos : BuildPromotionManager) : StartingBuildAgentsFilter {
  private val nodeBean = NodeBean()
  private val npmBean = NPMBean()
  private val nvmBean = NVMBean()
  private val runTypes = hashSetOf(nodeBean.runTypeNameNodeJs, npmBean.runTypeName)

  public override fun filterAgents(context: AgentsFilterContext): AgentsFilterResult {
    val result = AgentsFilterResult()
    val promoId = context.getStartingBuild().getBuildPromotionInfo().getId()
    val buildType = promos.findPromotionById(promoId)?.getBuildType()

    if (buildType == null) return result

    val runners = buildType.getBuildRunners() filter { buildType.isEnabled(it.getId()) }

    //if nothing found => skip
    if (runners.isEmpty()) return result

    //if our nodeJS and NPM runners are not used
    if (!runners.any { runner -> runTypes.contains(runner.getType())}) return result

    val version = runners.firstOrNull { it.getType() == nvmBean.NVMFeatureType }
           ?.getParameters()
           ?.get(nvmBean.NVMVersion)

    //skip checks if NVM feature version was specified
    if (version != null) return result

    //if not, let's filter unwanted agents
    val agents = context.getAgentsForStartingBuild() filter { agent ->
      //allow only if there were truly-detected NVM/NPM on the agent
      with(agent.getConfigurationParameters()) {
        get(nodeBean.nodeJSConfigurationParameter) != nvmBean.NVMUsed
        &&
        get(npmBean.nodeJSNPMConfigurationParameter) != nvmBean.NVMUsed
      }
    }

    if (agents.isEmpty()) {
      result setWaitReason SimpleWaitReason("Please add 'Node.js NVM Installer' build runner")
    } else {
      result setFilteredConnectedAgents agents
    }

    return result
  }
}
