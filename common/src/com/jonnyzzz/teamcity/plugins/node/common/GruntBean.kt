package com.jonnyzzz.teamcity.plugins.node.common

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

/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 27.04.13 9:58
 */
public class GruntBean {
  public val runTypeName: String = "jonnyzzz.grunt"
  public val file: String = "jonnyzzz.grunt.file"
  public val targets: String = "jonnyzzz.grunt.tasks"
  public val commandLineParameterKey : String = "jonnyzzz.commandLine"

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
