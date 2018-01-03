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
import com.jonnyzzz.teamcity.plugins.node.common.isEmptyOrSpaces

class NodeJsRunType : JsRunTypeBase() {
  override fun getType(): String = bean.runTypeNameNodeJs
  override fun getDisplayName(): String = "Node.js"
  override fun getDescription(): String = "Starts javascript files under Node.js runtime"
  override fun getEditJsp(): String = "node.edit.jsp"
  override fun getViewJsp(): String = "node.view.jsp"

  override fun getRunnerSpecificRequirements(runParameters: Map<String, String>): MutableList<Requirement> {
    val result = arrayListOf<Requirement>()
    val list = super.getRunnerSpecificRequirements(runParameters)
    result.addAll( list)

    if (runParameters[bean.toolPathKey].isEmptyOrSpaces()) {
      //for now there is the only option to use detected node.js
      result.add(Requirement(bean.nodeJSConfigurationParameter, null, RequirementType.EXISTS))
    }

    return result
  }
}

