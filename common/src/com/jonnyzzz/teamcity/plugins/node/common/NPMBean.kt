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
