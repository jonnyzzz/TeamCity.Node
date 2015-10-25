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
import com.jonnyzzz.teamcity.plugins.node.common.GruntBean
import com.jonnyzzz.teamcity.plugins.node.common.NodeBean
import com.jonnyzzz.teamcity.plugins.node.common.GruntExecutionMode

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
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 14.01.13 21:57
 */

public class GruntRunType : RunTypeBase() {
  private val bean = GruntBean()

  public override fun getType(): String = bean.runTypeName
  public override fun getDisplayName(): String = "Grunt"
  public override fun getDescription(): String = "Executes Grunt tasks"
  protected override fun getEditJsp(): String = "grunt.edit.jsp"
  protected override fun getViewJsp(): String = "grunt.view.jsp"

  public override fun getRunnerPropertiesProcessor(): PropertiesProcessor = PropertiesProcessor { arrayListOf() }

  public override fun describeParameters(parameters: Map<String, String>): String
          = "Run targets: ${bean.parseCommands(parameters[bean.targets]).joinToString(", ")}"

  public override fun getDefaultRunnerProperties(): MutableMap<String, String>
          = hashMapOf(bean.gruntMode to bean.gruntModeDefault.value)

  public override fun getRunnerSpecificRequirements(runParameters: Map<String, String>): MutableList<Requirement> {
    val result = arrayListOf<Requirement>()
    result.addAll(super.getRunnerSpecificRequirements(runParameters))

    result.add(Requirement(NodeBean().nodeJSConfigurationParameter, null, RequirementType.EXISTS))

    if (bean.parseMode(runParameters[bean.gruntMode]) == GruntExecutionMode.GLOBAL) {
      result.add(Requirement(bean.gruntConfigurationParameter, null, RequirementType.EXISTS))
    }

    return result
  }
}
