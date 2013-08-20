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

package com.jonnyzzz.teamcity.plugins.node.common

/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 14.01.13 22:02
 */

public class NPMBean {
  public val nodeJSNPMConfigurationParameter: String = "node.js.npm"

  public val runTypeName: String = "jonnyzzz.npm"
  public val commandLineParameterKey: String = "npm_execution_args"
  public val npmCommandsKey: String = "npm_commands"
  public val npmCommandsDefault: String = "install\r\ntest"
  public val toolPathKey : String = "npm_toolPath"

  public fun parseCommands(text: String?): Collection<String> {
    if (text == null)
      return listOf<String>()
    else
      return text
              .split("[\r\n]+")
              .map { it.trim() }
              .filterNot { it.isEmpty() }
  }
}
