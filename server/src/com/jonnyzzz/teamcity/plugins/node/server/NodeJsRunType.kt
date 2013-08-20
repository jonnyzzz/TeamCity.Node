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

import jetbrains.buildServer.requirements.Requirement
import jetbrains.buildServer.requirements.RequirementType
import jetbrains.buildServer.serverSide.InvalidProperty
import jetbrains.buildServer.serverSide.PropertiesProcessor
import com.jonnyzzz.teamcity.plugins.node.common.NodeBean
import com.jonnyzzz.teamcity.plugins.node.common.isEmptyOrSpaces
import java.util.InvalidPropertiesFormatException
import com.jonnyzzz.teamcity.plugins.node.common.ExecutionModes

public class NodeJsRunType : JsRunTypeBase() {
  public override fun getType(): String = bean.runTypeNameNodeJs
  public override fun getDisplayName(): String = "Node.js"
  public override fun getDescription(): String = "Starts javascript files under Node.js runtime"
  protected override fun getEditJsp(): String = "node.edit.jsp"
  protected override fun getViewJsp(): String = "node.view.jsp"

  public override fun getRunnerSpecificRequirements(runParameters: Map<String, String>): MutableList<Requirement> {
    val result = arrayListOf<Requirement>()
    val list = super.getRunnerSpecificRequirements(runParameters)
    if (list != null) result addAll list

    if (runParameters[bean.toolPathKey].isEmptyOrSpaces()) {
      //for now there is the only option to use detected node.js
      result.add(Requirement(bean.nodeJSConfigurationParameter, null, RequirementType.EXISTS))
    }

    return result
  }
}

