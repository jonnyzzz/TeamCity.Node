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
import jetbrains.buildServer.serverSide.PropertiesProcessor
import com.jonnyzzz.teamcity.plugins.node.common.BowerBean
import com.jonnyzzz.teamcity.plugins.node.common.NodeBean
import com.jonnyzzz.teamcity.plugins.node.common.BowerExecutionMode

/*
 * Copyright 2000-2013 Eugene Petrenko
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
/**
 * Created by Victor Mosin (vicmosin@gmail.com)
 * Date: 01.02.17 9:15
 */

class BowerRunType : RunTypeBase() {
  private val bean = BowerBean()

  override fun getType(): String = bean.runTypeName
  override fun getDisplayName(): String = "Bower"
  override fun getDescription(): String = "Executes Bower tasks"
  override fun getEditJsp(): String = "bower.edit.jsp"
  override fun getViewJsp(): String = "bower.view.jsp"

  override fun getRunnerPropertiesProcessor(): PropertiesProcessor = PropertiesProcessor { arrayListOf() }

  override fun describeParameters(parameters: Map<String, String>): String
          = "Run targets: ${bean.parseCommands(parameters[bean.targets]).joinToString(", ")}"

  override fun getDefaultRunnerProperties(): MutableMap<String, String>
          = hashMapOf(bean.bowerMode to bean.bowerModeDefault.value)

  override fun getRunnerSpecificRequirements(runParameters: Map<String, String>): MutableList<Requirement> {
    val result = arrayListOf<Requirement>()
    result.addAll(super.getRunnerSpecificRequirements(runParameters))

    result.add(Requirement(NodeBean().nodeJSConfigurationParameter, null, RequirementType.EXISTS))

    if (bean.parseMode(runParameters[bean.bowerMode]) == BowerExecutionMode.GLOBAL) {
      result.add(Requirement(bean.bowerConfigurationParameter, null, RequirementType.EXISTS))
    }

    return result
  }
}
