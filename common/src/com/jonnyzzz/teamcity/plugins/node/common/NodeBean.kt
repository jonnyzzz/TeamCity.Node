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
  public val toolPathKey: String = "node_toolPath"

  public val runTypeNameNodeJs: String = "jonnyzzz.node"
  public val runTypeNamePhantomJs: String = "jonnyzzz.phantom"

  public val nodeJSConfigurationParameter: String = "node.js"

  public fun findExecutionMode(parameters : Map<String?, String?>) : ExecutionModes?
          = executionModeValues.filter { it.value == parameters[executionModeKey] }.first
}

public enum class ExecutionModes(public val value: String,
                                 public val parameter : String,
                                 public val description : String)
{
  File : ExecutionModes("file", "node_file", "File");
  Script : ExecutionModes("script", "node_script_text", "Source Code")
}
