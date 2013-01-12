package com.jonnyzzz.teamcity.plugins.node.server

import com.jonnyzzz.teamcity.plugins.node.common.NodeBean
import com.jonnyzzz.teamcity.plugins.node.common.isEmptyOrSpaces
import jetbrains.buildServer.requirements.Requirement
import jetbrains.buildServer.requirements.RequirementType
import jetbrains.buildServer.serverSide.InvalidProperty
import jetbrains.buildServer.serverSide.PropertiesProcessor

public open class NodeJsRunType(): RunTypeBase() {
  private val bean: NodeBean = NodeBean()

  public override fun getType(): String {
    return bean.RunTypeName
  }

  public override fun getDisplayName(): String?  {
    return "Node.js"
  }
  public override fun getDescription(): String? {
    return "Starts javascript files under Node.js runtime"
  }
  protected override fun getEditJsp(): String {
    return "node.edit.jsp"
  }
  protected override fun getViewJsp(): String {
    return "node.view.jsp"
  }

  public override fun getRunnerPropertiesProcessor(): PropertiesProcessor {
    return object : PropertiesProcessor {
      public override fun process(parameters: Map<String, String>?): MutableCollection<InvalidProperty>? {
        val result = arrayListOf<InvalidProperty>()
        if (parameters == null) return result

        val mode = bean.findExecutionMode(parameters)
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

  public override fun describeParameters(parameters: Map<String, String>): String {
    var builder = StringBuilder()
    val mode = bean.findExecutionMode(parameters)
    if (mode != null) {
      builder.append("Execute: ${mode.Description}\n")

      if (mode == bean.ExecutionModeFile) {
        builder.append("File: ${parameters[bean.ExecutionModeFile.Parameter]}")
      }
    }

    return builder.toString()
  }

  public override fun getDefaultRunnerProperties(): MutableMap<String, String>? {
    return hashMapOf<String, String>()
  }

  public override fun getRunnerSpecificRequirements(runParameters: Map<String, String>): MutableList<Requirement>? {
    val result = arrayListOf<Requirement>()
    val base = super<RunTypeBase>.getRunnerSpecificRequirements(runParameters)
    if (base != null) result.addAll(base)

    //for now there is the only option to use detected node.js
    result.add(Requirement(bean.NodeJSConfigurationParameter, null, RequirementType.EXISTS))

    return result
  }
}
