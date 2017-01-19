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
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 14.01.13 22:02
 */

class YarnBean {
  val nodeJSYarnConfigurationParameter: String = "node.js.yarn"

  val runTypeName: String = "jonnyzzz.yarn"
  val commandLineParameterKey: String = "yarn_execution_args"
  val yarnCommandsKey: String = "yarn_commands"
  val yarnCommandsDefault: String = "install\r\ntest"
  val toolPathKey : String = "yarn_toolPath"

  fun parseCommands(text: String?): Collection<String> {
    if (text == null || text.isEmpty())
      return listOf()
    else
      return text
              .lines()
              .map { it.trim() }
              .filterNot { it.isEmptyOrSpaces() }
  }
}
