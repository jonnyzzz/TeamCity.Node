package com.jonnyzzz.teamcity.plugins.node.server

import org.springframework.ui.ExtendedModelMap

/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 11.01.13 23:44
 */

public class NodeBean {
  public val ExecutionModeKey : String = "node_execution_mode"
  public val ExecutionModeValues: Array<ExecutionModes> = ExecutionModes.values()
  public val ExecutionModeFile: ExecutionModes = ExecutionModes.File
  public val ExecutionModeScript: ExecutionModes = ExecutionModes.Script
  public val CommandLineParameterKey : String = "node_execution_args"
}

public enum class ExecutionModes(val Value: String, val Parameter : String, val Description : String)
{
  File : ExecutionModes("file", "node_file", "File");
  Script : ExecutionModes("script", "node_script_text", "Source Code")
}
