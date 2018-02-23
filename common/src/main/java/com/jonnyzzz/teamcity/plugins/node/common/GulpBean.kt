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


class GulpBean {
  val gulpConfigurationParameter: String = "gulp"

  val runTypeName: String = "jonnyzzz.gulp"
  val file: String = "jonnyzzz.gulp.file"
  val targets: String = "jonnyzzz.gulp.tasks"
  val commandLineParameterKey : String = "jonnyzzz.commandLine"
  val gulpMode : String = "jonnyzzz.gulp.mode"
  val gulpModeDefault : GulpExecutionMode = GulpExecutionMode.NPM

  val gulpModes : List<GulpExecutionMode>
    get() = arrayListOf(*GulpExecutionMode.values())

  fun parseMode(text : String?) : GulpExecutionMode?
          = gulpModes.firstOrNull { text == it.value } ?: gulpModeDefault

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

enum class GulpExecutionMode(val title : String,
                                     val value : String) {
  NPM("NPM package from project", "npm"),
  GLOBAL("System-wide gulp", "global"),
}
