package com.jonnyzzz.teamcity.plugins.node.common

/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 11.01.13 23:44
 */

public open class NodeBean {
  public val ExecutionModeKey : String = "node_execution_mode"
  public val ExecutionModeValues: Array<ExecutionModes> = ExecutionModes.values()
  public val ExecutionModeFile: ExecutionModes = ExecutionModes.File
  public val ExecutionModeScript: ExecutionModes = ExecutionModes.Script
  public val CommandLineParameterKey : String = "node_execution_args"
  public val RunTypeName : String = "jonnyzzz.node"
  public val NodeJSConfigurationParameter : String = "node.js"

  public fun findExecutionMode(parameters : Map<String?, String?>) : ExecutionModes?
          = ExecutionModeValues.filter { it.Value == parameters[ExecutionModeKey] }.first
}

public enum class ExecutionModes(public val Value: String,
                                 public val Parameter : String,
                                 public val Description : String)
{
  File : ExecutionModes("file", "node_file", "File");
  Script : ExecutionModes("script", "node_script_text", "Source Code")
}
