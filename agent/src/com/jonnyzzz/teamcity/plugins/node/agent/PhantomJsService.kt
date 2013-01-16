package com.jonnyzzz.teamcity.plugins.node.agent
/*
 * Copyright 2000-2013 Eugene Petrenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import jetbrains.buildServer.RunBuildException
import jetbrains.buildServer.agent.runner.ProgramCommandLine
import jetbrains.buildServer.agent.runner.BuildServiceAdapter
import jetbrains.buildServer.util.FileUtil
import com.jonnyzzz.teamcity.plugins.node.common.NodeBean
import com.jonnyzzz.teamcity.plugins.node.common.ExecutionModes
import com.jonnyzzz.teamcity.plugins.node.common.isEmptyOrSpaces
import com.jonnyzzz.teamcity.plugins.node.common.resolve
import com.jonnyzzz.teamcity.plugins.node.common.splitHonorQuotes
import com.jonnyzzz.teamcity.plugins.node.common.TempFileName
import com.jonnyzzz.teamcity.plugins.node.common.tempFile
import java.io.IOException
import java.io.File
import com.jonnyzzz.teamcity.plugins.node.common.log4j
import com.jonnyzzz.teamcity.plugins.node.common.smartDelete

/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 15.01.13 0:56
 */


public class PhantomJsService() : JsService() {
  protected override fun getToolPath(): String? = getRunnerParameters()[bean.toolPathKey]

  protected override fun getToolName(): String = "phantom"

  protected override fun getGeneratedScriptExt(): String
          = "." + (getRunnerParameters()[bean.phantomJsExtensionKey] ?: bean.phantomJsExtensionDefault)
}
