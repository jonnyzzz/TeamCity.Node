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
import jetbrains.buildServer.serverSide.PropertiesProcessor
import com.jonnyzzz.teamcity.plugins.node.common.*

class GulpRunType : RunTypeBase() {
  private val bean = GulpBean()

  override fun getType(): String = bean.runTypeName
  override fun getDisplayName(): String = "Gulp"
  override fun getDescription(): String = "Executes Gulp tasks"
  override fun getEditJsp(): String = "gulp.edit.jsp"
  override fun getViewJsp(): String = "gulp.view.jsp"

  override fun getRunnerPropertiesProcessor(): PropertiesProcessor = PropertiesProcessor { arrayListOf() }

  override fun describeParameters(parameters: Map<String, String>): String
          = "Run targets: ${bean.parseCommands(parameters[bean.targets]).joinToString(", ")}"

  override fun getDefaultRunnerProperties(): MutableMap<String, String>
          = hashMapOf(bean.gulpMode to bean.gulpModeDefault.value)

  override fun getRunnerSpecificRequirements(runParameters: Map<String, String>): MutableList<Requirement> {
    val result = arrayListOf<Requirement>()
    result.addAll(super.getRunnerSpecificRequirements(runParameters))

    result.add(Requirement(NodeBean().nodeJSConfigurationParameter, null, RequirementType.EXISTS))

    if (bean.parseMode(runParameters[bean.gulpMode]) == GulpExecutionMode.GLOBAL) {
      result.add(Requirement(bean.gulpConfigurationParameter, null, RequirementType.EXISTS))
    }

    return result
  }
}
