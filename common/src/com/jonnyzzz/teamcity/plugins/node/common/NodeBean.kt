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
 * Date: 11.01.13 23:44
 */

public class NodeBean {
  public val executionModeKey: String = "node_execution_mode"
  public val executionModeValues: Array<ExecutionModes> = ExecutionModes.values()
  public val executionModeFile: ExecutionModes = ExecutionModes.File
  public val executionModeScript: ExecutionModes = ExecutionModes.Script
  public val commandLineParameterKey: String = "node_execution_args"
  public val scriptParameterKey: String = "node_script_args"
  public val runTypeName: String = "jonnyzzz.node"
  public val toolPathKey: String = "node_toolPath"

  public val phantomJsExtensionKey : String = "phantom_extension"
  public val phantomJsExtensionDefault : String = "js"

  public val runTypeNameNodeJs: String = "jonnyzzz.node"
  public val runTypeNamePhantomJs: String = "jonnyzzz.phantom"

  public val nodeJSConfigurationParameter: String = "node.js"

  public fun findExecutionMode(parameters : Map<String, String>) : ExecutionModes?
          = executionModeValues.find { it.value == parameters[executionModeKey] }
}

public enum class ExecutionModes(public val value: String,
                                 public val parameter : String,
                                 public val description : String)
{
  File : ExecutionModes("file", "node_file", "File");
  Script : ExecutionModes("script", "node_script_text", "Source Code")
}
