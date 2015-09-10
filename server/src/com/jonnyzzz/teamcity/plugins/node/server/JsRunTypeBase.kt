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

import jetbrains.buildServer.requirements.RequirementType
import jetbrains.buildServer.requirements.Requirement
import com.jonnyzzz.teamcity.plugins.node.common.isEmptyOrSpaces
import jetbrains.buildServer.serverSide.InvalidProperty
import jetbrains.buildServer.serverSide.PropertiesProcessor
import com.jonnyzzz.teamcity.plugins.node.common.NodeBean

/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 15.01.13 22:06
 */

public abstract class JsRunTypeBase : RunTypeBase() {
  protected val bean : NodeBean = NodeBean()

  public abstract override fun getType(): String
  public abstract override fun getDisplayName(): String
  public abstract override fun getDescription(): String

  public override fun getRunnerPropertiesProcessor(): PropertiesProcessor {
    val that = this;
    return object : PropertiesProcessor {
      public override fun process(p0: Map<String, String>?): MutableCollection<InvalidProperty>? {
        if (p0 == null) return arrayListOf()
        return that.validateParameters(p0)
      }
    }
  }

  protected open fun validateParameters(parameters: Map<String, String>): MutableCollection<InvalidProperty> {
    val result = arrayListOf<InvalidProperty>()

    val mode = bean.findExecutionMode(parameters)
    if (mode == null) {
      result.add(InvalidProperty(bean.executionModeKey, "Execution Mode must be selected"))
    } else {
      val content = parameters[mode.parameter]
      if (content.isEmptyOrSpaces()) {
        result.add(InvalidProperty(mode.parameter, "${mode.description} should not be empty"))
      }
    }
    return result;
  }


  public override fun describeParameters(parameters: Map<String, String>): String {
    val builder = StringBuilder()
    val mode = bean.findExecutionMode(parameters)
    if (mode != null) {
      builder.append("Execute: ${mode.description}\n")

      if (mode == bean.executionModeFile) {
        builder.append("File: ${parameters[bean.executionModeFile.parameter]}")
      }
    }

    return builder.toString()
  }

  public override fun getDefaultRunnerProperties(): MutableMap<String, String>? = hashMapOf()
}
