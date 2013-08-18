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
import jetbrains.buildServer.serverSide.parameters.AbstractBuildParametersProvider
import jetbrains.buildServer.serverSide.SBuild
import com.jonnyzzz.teamcity.plugins.node.common.*
import jetbrains.buildServer.serverSide.PropertiesProcessor
import jetbrains.buildServer.serverSide.InvalidProperty
import jetbrains.buildServer.requirements.Requirement
import jetbrains.buildServer.agent.AgentRuntimeProperties

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
          = "Node.js v" + parameters[bean.NVMVersion]

  public override fun getRunnerSpecificRequirements(runParameters: Map<String, String>): MutableList<Requirement> {
    //TODO: check OS is linux or Mac OS
    return arrayListOf()
  }
}

public class NVMParametersProvider() : AbstractBuildParametersProvider() {
  private val bean = NVMBean()

  public override fun getParameters(build: SBuild, emulationMode: Boolean): MutableMap<String, String> {
    val def = super<AbstractBuildParametersProvider>.getParameters(build, emulationMode)

    val bt = build.getBuildType()
    if (bt == null) return def

    val feature = bt.getResolvedSettings().getBuildRunners().find { it.getType() == bean.NVMFeatureType }
    if (feature == null) return def

    val version = feature.getParameters()[bean.NVMVersion]
    if (version == null) return def

    val v : String = version
    return hashMapOf(
            bean.NVMUsed to v,
            NodeBean().nodeJSConfigurationParameter to v,
            NPMBean().nodeJSNPMConfigurationParameter to v
    ) + def
  }
}
