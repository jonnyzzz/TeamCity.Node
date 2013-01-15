package com.jonnyzzz.teamcity.plugins.node.server

import jetbrains.buildServer.requirements.Requirement
import jetbrains.buildServer.requirements.RequirementType
import jetbrains.buildServer.serverSide.InvalidProperty
import jetbrains.buildServer.serverSide.PropertiesProcessor
import com.jonnyzzz.teamcity.plugins.node.common.NodeBean
import com.jonnyzzz.teamcity.plugins.node.common.isEmptyOrSpaces
import java.util.InvalidPropertiesFormatException

public class NodeJsRunType : JsRunTypeBase() {
  public override fun getType(): String = bean.runTypeNameNodeJs
  public override fun getDisplayName(): String? = "Node.js"
  public override fun getDescription(): String? = "Starts javascript files under Node.js runtime"
  protected override fun getEditJsp(): String = "node.edit.jsp"
  protected override fun getViewJsp(): String = "node.view.jsp"
}

public class PhantomJsRunType : JsRunTypeBase() {
  public override fun getType(): String = bean.runTypeNamePhantomJs
  public override fun getDisplayName(): String? = "Phantom.JS"
  public override fun getDescription(): String? = "Starts javascript files under Phantom.JS runtime"
  protected override fun getEditJsp(): String = "phantom.edit.jsp"
  protected override fun getViewJsp(): String = "phantom.view.jsp"

  protected override fun validateParameters(parameters: Map<String?, String?>?): MutableCollection<InvalidProperty?> {
    val result = super<JsRunTypeBase>.validateParameters(parameters)

    if (parameters != null) {
      if (parameters[bean.toolPathKey].isEmptyOrSpaces()) {
        result add InvalidProperty(bean.toolPathKey, "Path to Phantom.JS sould be specified")
      }
    }

    return result
  }
}
