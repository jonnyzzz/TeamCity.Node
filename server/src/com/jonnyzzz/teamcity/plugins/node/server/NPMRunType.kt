package com.jonnyzzz.teamcity.plugins.node.server
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

import jetbrains.buildServer.requirements.Requirement
import jetbrains.buildServer.requirements.RequirementType
import jetbrains.buildServer.serverSide.InvalidProperty
import jetbrains.buildServer.serverSide.PropertiesProcessor
import com.jonnyzzz.teamcity.plugins.node.common.NodeBean
import com.jonnyzzz.teamcity.plugins.node.common.isEmptyOrSpaces
import com.jonnyzzz.teamcity.plugins.node.common.NPMBean
import com.jonnyzzz.teamcity.plugins.node.common.join

/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 14.01.13 21:57
 */

public class NPMRunType : RunTypeBase() {
  private val bean = NPMBean()

  public override fun getType(): String = bean.runTypeName
  public override fun getDisplayName(): String = "Node.js NPM"
  public override fun getDescription(): String = "Starts NPM"
  protected override fun getEditJsp(): String = "node.npm.edit.jsp"
  protected override fun getViewJsp(): String = "node.npm.view.jsp"

  public override fun getRunnerPropertiesProcessor(): PropertiesProcessor {
    return object : PropertiesProcessor {
      public override fun process(parameters: Map<String, String>?): MutableCollection<InvalidProperty>?
              = arrayListOf()
    }
  }

  public override fun describeParameters(parameters: Map<String, String>): String {
    return "Run targets: ${bean.parseCommands(parameters[bean.npmCommandsKey]) join ", "}"
  }

  public override fun getDefaultRunnerProperties(): MutableMap<String, String>?
          = hashMapOf(bean.npmCommandsKey to bean.npmCommandsDefault)

  public override fun getRunnerSpecificRequirements(runParameters: Map<String, String>): MutableList<Requirement> {
    val result = arrayListOf<Requirement>()
    result addAll super.getRunnerSpecificRequirements(runParameters)

    if (runParameters[bean.toolPathKey].isEmptyOrSpaces()) {
      //for now there is the only option to use detected node.js
      result.add(Requirement(bean.nodeJSNPMConfigurationParameter, null, RequirementType.EXISTS))
    }
    return result
  }
}
