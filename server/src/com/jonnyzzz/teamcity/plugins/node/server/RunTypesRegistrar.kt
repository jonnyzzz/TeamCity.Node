package com.jonnyzzz.teamcity.plugins.node.server

import jetbrains.buildServer.serverSide.RunTypeRegistry

public class RunTypesRegistrar(registry: RunTypeRegistry, types: Collection<RunTypeBase?>) {
  {
    types.filterNotNull().forEach { registry.registerRunType(it) }
  }
}
