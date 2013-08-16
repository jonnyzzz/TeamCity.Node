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

import jetbrains.buildServer.serverSide.BuildFeature
import com.jonnyzzz.teamcity.plugins.node.common.NVMBean
import jetbrains.buildServer.web.openapi.PluginDescriptor
import jetbrains.buildServer.serverSide.parameters.AbstractBuildParametersProvider
import jetbrains.buildServer.serverSide.SBuild
import com.jonnyzzz.teamcity.plugins.node.common.NodeBean
import com.jonnyzzz.teamcity.plugins.node.common.NPMBean
import com.jonnyzzz.teamcity.plugins.node.common.*

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 16.08.13 21:43
 */
public class NVMFeature(val plugin : PluginDescriptor) : BuildFeature() {
  private val bean = NVMBean()

  public override fun getType(): String = bean.NVMFeatureType
  public override fun getDisplayName(): String = "Install Node.js and NPM"
  public override fun getEditParametersUrl(): String? = plugin.getPluginResourcesPath("node.nvm.jsp")
  public override fun describeParameters(params: Map<String, String>): String = "Node.js v" + params[bean.NVMVersion]
  public override fun getDefaultParameters(): MutableMap<String, String> = hashMapOf(bean.NVMVersion to "0.10.0")
  public override fun isMultipleFeaturesPerBuildTypeAllowed(): Boolean = false
}

public class NVMParametersProvider : AbstractBuildParametersProvider() {
  private val bean = NVMBean()

  public override fun getParameters(build: SBuild, emulationMode: Boolean): MutableMap<String, String> {
    val def = super<AbstractBuildParametersProvider>.getParameters(build, emulationMode)

    val bt = build.getBuildType()
    if (bt == null) return def

    val feature = bt.getResolvedSettings().getBuildFeatures().find { it.getType() == bean.NVMFeatureType }
    if (feature == null) return def

    val version = feature.getParameters()[bean.NVMVersion]
    if (version == null) return def

    val v : String = version
    return hashMapOf(
            NodeBean().nodeJSConfigurationParameter to v,
            NPMBean().nodeJSNPMConfigurationParameter to v
    ) + def
  }
}
