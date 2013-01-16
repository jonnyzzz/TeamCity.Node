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
import java.util.InvalidPropertiesFormatException
import com.jonnyzzz.teamcity.plugins.node.common.ExecutionModes

public class NodeJsRunType : JsRunTypeBase() {
  public override fun getType(): String = bean.runTypeNameNodeJs
  public override fun getDisplayName(): String? = "Node.js"
  public override fun getDescription(): String? = "Starts javascript files under Node.js runtime"
  protected override fun getEditJsp(): String = "node.edit.jsp"
  protected override fun getViewJsp(): String = "node.view.jsp"
}

public class PhantomJsRunType : JsRunTypeBase() {
  public override fun getType(): String = bean.runTypeNamePhantomJs
  public override fun getDisplayName(): String? = "Phantom.JS"
  public override fun getDescription(): String? = "Starts javascript files under Phantom.JS runtime"
  protected override fun getEditJsp(): String = "phantom.edit.jsp"
  protected override fun getViewJsp(): String = "phantom.view.jsp"

  protected override fun validateParameters(parameters: Map<String?, String?>?): MutableCollection<InvalidProperty?> {
    val result = super<JsRunTypeBase>.validateParameters(parameters)

    if (parameters != null) {
      if (parameters[bean.toolPathKey].isEmptyOrSpaces()) {
        result add InvalidProperty(bean.toolPathKey, "Path to Phantom.JS sould be specified")
      }

      val mode = bean.findExecutionMode(parameters)
      if (mode == ExecutionModes.Script && parameters[bean.phantomJsExtensionKey].isEmptyOrSpaces()) {
        result add InvalidProperty(bean.phantomJsExtensionKey, "Extension for generated script is not defined")
      }
    }

    return result
  }

  public override fun getDefaultRunnerProperties(): MutableMap<String?, String?>? {
    return hashMapOf(bean.phantomJsExtensionKey to bean.phantomJsExtensionDefault)
  }
}
