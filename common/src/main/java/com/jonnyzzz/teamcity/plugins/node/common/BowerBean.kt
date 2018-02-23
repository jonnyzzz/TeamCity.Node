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

package com.jonnyzzz.teamcity.plugins.node.common


/**
 * Created by Victor Mosin (vicmosin@gmail.com)
 * Date: 01.02.17 8:55
 */
class BowerBean {
  val bowerConfigurationParameter : String = "bower"

  val runTypeName: String = "jonnyzzz.bower"
  val targets: String = "jonnyzzz.bower.tasks"
  val commandLineParameterKey : String = "jonnyzzz.commandLine"
  val bowerMode : String = "jonnyzzz.bower.mode"
  val bowerModeDefault : BowerExecutionMode = BowerExecutionMode.NPM

  val bowerModes : List<BowerExecutionMode>
          get() = arrayListOf(*BowerExecutionMode.values())

  fun parseMode(text : String?) : BowerExecutionMode?
         = bowerModes.firstOrNull { text == it.value } ?: bowerModeDefault

  fun parseCommands(text: String?): Collection<String> {
    if (text == null)
      return listOf()
    else
      return text
              .lines()
              .map { it.trim() }
              .filterNot { it.isEmpty() }
  }
}

enum class BowerExecutionMode(val title : String,
                              val value : String) {
  NPM("NPM package from project", "npm"),
  GLOBAL("System-wide bower", "global"),
}
