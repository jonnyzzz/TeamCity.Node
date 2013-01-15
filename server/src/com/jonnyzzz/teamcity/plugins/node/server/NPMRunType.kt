package com.jonnyzzz.teamcity.plugins.node.server

import jetbrains.buildServer.requirements.Requirement
import jetbrains.buildServer.requirements.RequirementType
import jetbrains.buildServer.serverSide.InvalidProperty
import jetbrains.buildServer.serverSide.PropertiesProcessor
import com.jonnyzzz.teamcity.plugins.node.common.NodeBean
import com.jonnyzzz.teamcity.plugins.node.common.isEmptyOrSpaces
import com.jonnyzzz.teamcity.plugins.node.common.NPMBean
import com.jonnyzzz.teamcity.plugins.node.common.join

/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 14.01.13 21:57
 */

public class NPMRunType : RunTypeBase() {
  private val bean = NPMBean()

  public override fun getType(): String = bean.runTypeName
  public override fun getDisplayName(): String? = "Node.js NPM"
  public override fun getDescription(): String? = "Starts NPM"
  protected override fun getEditJsp(): String = "node.npm.edit.jsp"
  protected override fun getViewJsp(): String = "node.npm.view.jsp"

  public override fun getRunnerPropertiesProcessor(): PropertiesProcessor {
    return object : PropertiesProcessor {
      public override fun process(parameters: Map<String?, String?>?): MutableCollection<InvalidProperty?>?
        = arrayListOf<InvalidProperty?>()
    }
  }

  public override fun describeParameters(parameters: Map<String?, String?>): String {
    return "Run targets: ${bean.parseCommands(parameters[bean.npmCommandsKey]) join ", "}"
  }

  public override fun getDefaultRunnerProperties(): MutableMap<String?, String?>? {
    return hashMapOf<String?, String?>(bean.npmCommandsKey to bean.npmCommandsDefault)
  }

  public override fun getRunnerSpecificRequirements(runParameters: Map<String?, String?>): MutableList<Requirement?>? {
    val result = arrayListOf<Requirement?>()
    val base = super<RunTypeBase>.getRunnerSpecificRequirements(runParameters)
    if (base != null) result.addAll(base)

    //for now there is the only option to use detected node.js
    result.add(Requirement(bean.nodeJSNPMConfigurationParameter, null, RequirementType.EXISTS))
    return result
  }
}
