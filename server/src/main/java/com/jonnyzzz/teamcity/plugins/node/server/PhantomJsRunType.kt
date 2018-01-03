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


import jetbrains.buildServer.serverSide.InvalidProperty
import com.jonnyzzz.teamcity.plugins.node.common.isEmptyOrSpaces
import com.jonnyzzz.teamcity.plugins.node.common.ExecutionModes

class PhantomJsRunType : JsRunTypeBase() {
  override fun getType(): String = bean.runTypeNamePhantomJs
  override fun getDisplayName(): String = "Phantom.JS"
  override fun getDescription(): String = "Starts javascript files under Phantom.JS runtime"
  override fun getEditJsp(): String = "phantom.edit.jsp"
  override fun getViewJsp(): String = "phantom.view.jsp"

  override fun validateParameters(parameters: Map<String, String>): MutableCollection<InvalidProperty> {
    val result = super.validateParameters(parameters)

    if (parameters[bean.toolPathKey].isEmptyOrSpaces()) {
      result.add(InvalidProperty(bean.toolPathKey, "Path to Phantom.JS should be specified"))
    }

    val mode = bean.findExecutionMode(parameters)
    if (mode == ExecutionModes.Script && parameters[bean.phantomJsExtensionKey].isEmptyOrSpaces()) {
      result.add(InvalidProperty(bean.phantomJsExtensionKey, "Extension for generated script is not defined"))
    }

    return result;
  }

  override fun getDefaultRunnerProperties(): MutableMap<String, String>?
          = hashMapOf(bean.phantomJsExtensionKey to bean.phantomJsExtensionDefault)
}
