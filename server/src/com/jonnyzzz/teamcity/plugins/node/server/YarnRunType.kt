/*
 * Copyright 2013-2017 Eugene Petrenko
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
import com.jonnyzzz.teamcity.plugins.node.common.YarnBean

/**
 * Created by Florian Krauthan (mail@fkrauthan.de)
 * Date: 19.01.17
 */

class YarnRunType : RunTypeBase() {
  private val bean = YarnBean()

  override fun getType(): String = bean.runTypeName
  override fun getDisplayName(): String = "Node.js Yarn"
  override fun getDescription(): String = "Starts Yarn"
  override fun getEditJsp(): String = "node.yarn.edit.jsp"
  override fun getViewJsp(): String = "node.yarn.view.jsp"

  override fun getRunnerPropertiesProcessor(): PropertiesProcessor {
    return object : PropertiesProcessor {
      override fun process(parameters: Map<String, String>?): MutableCollection<InvalidProperty>?
              = arrayListOf()
    }
  }

  override fun describeParameters(parameters: Map<String, String>): String
          = "Run targets: ${bean.parseCommands(parameters[bean.yarnCommandsKey]).joinToString(", ")}"

  override fun getDefaultRunnerProperties(): MutableMap<String, String>?
          = hashMapOf(bean.yarnCommandsKey to bean.yarnCommandsDefault)

  override fun getRunnerSpecificRequirements(runParameters: Map<String, String>): MutableList<Requirement> {
    val result = arrayListOf<Requirement>()
    result.addAll(super.getRunnerSpecificRequirements(runParameters))

    if (runParameters[bean.toolPathKey].isEmptyOrSpaces()) {
      //for now there is the only option to use detected node.js
      result.add(Requirement(bean.nodeJSYarnConfigurationParameter, null, RequirementType.EXISTS))
    }
    return result
  }
}
