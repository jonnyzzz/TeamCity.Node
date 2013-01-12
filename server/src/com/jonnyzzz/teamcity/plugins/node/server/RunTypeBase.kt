package com.jonnyzzz.teamcity.plugins.node.server

import jetbrains.buildServer.serverSide.PropertiesProcessor
import jetbrains.buildServer.serverSide.RunType
import jetbrains.buildServer.web.openapi.PluginDescriptor
import org.springframework.beans.factory.annotation.Autowired

public abstract class RunTypeBase(): RunType() {
  Autowired
  public var descriptor: PluginDescriptor? = null

  protected abstract fun getEditJsp(): String
  protected abstract fun getViewJsp(): String

  public override fun getEditRunnerParamsJspFilePath(): String {
    return descriptor?.getPluginResourcesPath(getEditJsp())!!
  }
  public override fun getViewRunnerParamsJspFilePath(): String {
    return descriptor?.getPluginResourcesPath(getViewJsp())!!
  }
}
