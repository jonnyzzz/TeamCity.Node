package com.jonnyzzz.teamcity.plugins.node.server

import com.jonnyzzz.teamcity.plugins.node.common.NodeConstants
import java.util.ArrayList
import java.util.HashMap
import jetbrains.buildServer.serverSide.InvalidProperty
import jetbrains.buildServer.serverSide.PropertiesProcessor
import jetbrains.buildServer.serverSide.RunType

public open class NodeJsRunType(): RunTypeBase() {
  public override fun getType(): String {
    return NodeConstants.NODE_RUN_TYPE
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
        return result;
      }
    }
  }
  protected override fun getEditJsp(): String = "node.edit.jsp"
  protected override fun getViewJsp(): String = "node.view.jsp"

  public open fun describeParameters(): String {
    return ""
  }

  public override fun getDefaultRunnerProperties(): MutableMap<String, String>? {
    return HashMap<String, String>()
  }

  public override fun describeParameters(parameters: Map<String, String>): String {
    return super<RunTypeBase>.describeParameters(parameters)
  }
}
