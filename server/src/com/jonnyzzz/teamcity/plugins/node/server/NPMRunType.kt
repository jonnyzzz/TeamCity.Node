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

package com.jonnyzzz.teamcity.plugins.node.server

import jetbrains.buildServer.requirements.Requirement
import jetbrains.buildServer.requirements.RequirementType
import jetbrains.buildServer.serverSide.InvalidProperty
import jetbrains.buildServer.serverSide.PropertiesProcessor
import com.jonnyzzz.teamcity.plugins.node.common.isEmptyOrSpaces
import com.jonnyzzz.teamcity.plugins.node.common.NPMBean

/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 14.01.13 21:57
 */

class NPMRunType : RunTypeBase() {
  private val bean = NPMBean()

  override fun getType(): String = bean.runTypeName
  override fun getDisplayName(): String = "Node.js NPM"
  override fun getDescription(): String = "Starts NPM"
  override fun getEditJsp(): String = "node.npm.edit.jsp"
  override fun getViewJsp(): String = "node.npm.view.jsp"

  override fun getRunnerPropertiesProcessor(): PropertiesProcessor {
    return object : PropertiesProcessor {
      override fun process(parameters: Map<String, String>?): MutableCollection<InvalidProperty>?
              = arrayListOf()
    }
  }

  override fun describeParameters(parameters: Map<String, String>): String
          = "Run targets: ${bean.parseCommands(parameters[bean.npmCommandsKey]).joinToString(", ")}"

  override fun getDefaultRunnerProperties(): MutableMap<String, String>?
          = hashMapOf(bean.npmCommandsKey to bean.npmCommandsDefault)

  override fun getRunnerSpecificRequirements(runParameters: Map<String, String>): MutableList<Requirement> {
    val result = arrayListOf<Requirement>()
    result.addAll(super.getRunnerSpecificRequirements(runParameters))

    if (runParameters[bean.toolPathKey].isEmptyOrSpaces()) {
      //for now there is the only option to use detected node.js
      result.add(Requirement(bean.nodeJSNPMConfigurationParameter, null, RequirementType.EXISTS))
    }
    return result
  }
}
