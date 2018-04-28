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

package com.jonnyzzz.teamcity.plugins.node.common


/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 27.04.13 9:58
 */
class GruntBean {
  val gruntConfigurationParameter : String = "grunt"

  val runTypeName: String = "jonnyzzz.grunt"
  val file: String = "jonnyzzz.grunt.file"
  val targets: String = "jonnyzzz.grunt.tasks"
  val commandLineParameterKey : String = "jonnyzzz.commandLine"
  val gruntMode : String = "jonnyzzz.grunt.mode"
  val gruntModeDefault : GruntExecutionMode = GruntExecutionMode.NPM

  val gruntModes : List<GruntExecutionMode>
          get() = arrayListOf(*GruntExecutionMode.values())

  fun parseMode(text : String?) : GruntExecutionMode?
         = gruntModes.firstOrNull { text == it.value } ?: gruntModeDefault

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

enum class GruntExecutionMode(val title : String,
                                     val value : String) {
  NPM("NPM package from project", "npm"),
  GLOBAL("System-wide grunt", "global"),
}
