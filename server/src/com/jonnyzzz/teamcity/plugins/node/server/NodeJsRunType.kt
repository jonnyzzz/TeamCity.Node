package com.jonnyzzz.teamcity.plugins.node.server

import com.jonnyzzz.teamcity.plugins.node.common.NodeConstants
import java.util.ArrayList
import java.util.HashMap
import jetbrains.buildServer.serverSide.InvalidProperty
import jetbrains.buildServer.serverSide.PropertiesProcessor
import jetbrains.buildServer.serverSide.RunType
import jetbrains.buildServer.util.StringUtil
import com.jonnyzzz.teamcity.plugins.node.common.isEmptyOrSpaces
import com.jonnyzzz.teamcity.plugins.node.common.NodeBean

public open class NodeJsRunType(): RunTypeBase() {
  private val bean : NodeBean = NodeBean()

  public override fun getType(): String {
    return bean.RunType
  }
  public override fun getDisplayName(): String? {
    return "Node.js"
  }
  public override fun getDescription(): String? {
    return "Starts javascript files under Node.js runtime"
  }
  public override fun getRunnerPropertiesProcessor(): PropertiesProcessor {
    return object : PropertiesProcessor {
      public override fun process(parameters: Map<String, String>?): MutableCollection<InvalidProperty>? {
        val result = ArrayList<InvalidProperty>()
        if (parameters == null) return result

        val mode = getExecutionMode(parameters)
        if (mode == null) {
          result.add(InvalidProperty(bean.ExecutionModeKey, "Execution Mode must be selected"))
        } else {
          val content = parameters[mode.Parameter]
          if (content.isEmptyOrSpaces()) {
            result.add(InvalidProperty(mode.Parameter, "${mode.Description} sbould not be empty"))
          }
        }
        return result;
      }
    }
  }
  protected override fun getEditJsp(): String = "node.edit.jsp"
  protected override fun getViewJsp(): String = "node.view.jsp"

  private fun getExecutionMode(parameters : Map<String, String>) : ExecutionModes?
   = bean.ExecutionModeValues.filter { it.Value == parameters[bean.ExecutionModeKey] }.first

  public override fun describeParameters(parameters: Map<String, String>): String {
    var builder = StringBuilder()
    val mode = getExecutionMode(parameters)
    if (mode != null) {
      builder.append("Execute: ${mode.Description}\n")

      if (mode == bean.ExecutionModeFile) {
        builder.append("File: ${parameters[bean.ExecutionModeFile.Parameter]}")
      }
    }

    return builder.toString()
  }

  public override fun getDefaultRunnerProperties(): MutableMap<String, String>? {
    return HashMap<String, String>()
  }
}
