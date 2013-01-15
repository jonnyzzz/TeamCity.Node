package com.jonnyzzz.teamcity.plugins.node.server

import jetbrains.buildServer.requirements.RequirementType
import jetbrains.buildServer.requirements.Requirement
import com.jonnyzzz.teamcity.plugins.node.common.isEmptyOrSpaces
import jetbrains.buildServer.serverSide.InvalidProperty
import jetbrains.buildServer.serverSide.PropertiesProcessor
import com.jonnyzzz.teamcity.plugins.node.common.NodeBean

/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 15.01.13 22:06
 */

public abstract class JsRunTypeBase : RunTypeBase() {
  protected val bean : NodeBean = NodeBean()

  public abstract override fun getType(): String
  public abstract override fun getDisplayName(): String?
  public abstract override fun getDescription(): String?

  public override fun getRunnerPropertiesProcessor(): PropertiesProcessor {
    val that = this;
    return object : PropertiesProcessor {
      public override fun process(p0: Map<String?, String?>?): MutableCollection<InvalidProperty?>? {
        if (p0 == null) return arrayListOf<InvalidProperty?>()
        return that.validateParameters(p0)
      }
    }
  }

  protected open fun validateParameters(parameters: Map<String?, String?>?): MutableCollection<InvalidProperty?> {
    val result = arrayListOf<InvalidProperty?>()
    if (parameters == null) return result

    val mode = bean.findExecutionMode(parameters)
    if (mode == null) {
      result.add(InvalidProperty(bean.executionModeKey, "Execution Mode must be selected"))
    } else {
      val content = parameters[mode.parameter]
      if (content.isEmptyOrSpaces()) {
        result.add(InvalidProperty(mode.parameter, "${mode.description} sbould not be empty"))
      }
    }
    return result;
  }


  public override fun describeParameters(parameters: Map<String?, String?>): String {
    var builder = StringBuilder()
    val mode = bean.findExecutionMode(parameters)
    if (mode != null) {
      builder.append("Execute: ${mode.description}\n")

      if (mode == bean.executionModeFile) {
        builder.append("File: ${parameters[bean.executionModeFile.parameter]}")
      }
    }

    return builder.toString()
  }

  public override fun getDefaultRunnerProperties(): MutableMap<String?, String?>? {
    return hashMapOf<String?, String?>()
  }

  public override fun getRunnerSpecificRequirements(runParameters: Map<String?, String?>): MutableList<Requirement?>? {
    val result = arrayListOf<Requirement?>()
    val base = super<RunTypeBase>.getRunnerSpecificRequirements(runParameters)
    if (base != null) result.addAll(base)

    //for now there is the only option to use detected node.js
    result.add(Requirement(bean.nodeJSConfigurationParameter, null, RequirementType.EXISTS))

    return result
  }
}
