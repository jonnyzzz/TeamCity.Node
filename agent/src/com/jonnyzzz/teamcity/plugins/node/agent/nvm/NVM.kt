/*
 * Copyright 2013-2013 Eugene Petrenko
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

package com.jonnyzzz.teamcity.plugins.node.agent.nvm

import java.io.File
import org.apache.http.client.methods.HttpGet
import jetbrains.buildServer.RunBuildException
import java.util.zip.ZipInputStream
import jetbrains.buildServer.util.FileUtil
import java.io.FileOutputStream
import java.io.BufferedOutputStream
import jetbrains.buildServer.util.EventDispatcher
import jetbrains.buildServer.agent.AgentLifeCycleListener
import jetbrains.buildServer.agent.AgentLifeCycleAdapter
import jetbrains.buildServer.agent.AgentRunningBuild
import jetbrains.buildServer.agent.BuildRunnerContext
import com.jonnyzzz.teamcity.plugins.node.common.*
import jetbrains.buildServer.agent.AgentBuildFeature
import com.jonnyzzz.teamcity.plugins.node.agent.block
import jetbrains.buildServer.agent.plugins.beans.PluginDescriptor
import jetbrains.buildServer.agent.BuildAgentConfiguration
import jetbrains.buildServer.agent.BuildProcessFacade
import jetbrains.buildServer.runner.SimpleRunnerConstants

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 05.08.13 9:35
 */
///https://github.com/creationix/nvm
///http://ghb.freshblurbs.com/blog/2011/05/07/install-node-js-and-express-js-nginx-debian-lenny.html
public class NVMDownloader(val http:HttpClientWrapper) {
  private val LOG = log4j(javaClass<NVMDownloader>())
  private val url = "https://github.com/creationix/nvm/archive/master.zip"

  private fun error(message:String, e:Throwable? = null) : Throwable {
    if (e == null) {
      throw RunBuildException("Failed to download NVM from ${url}. ${message}")
    } else {
      throw RunBuildException("Failed to download NVM from ${url}. ${message}. ${e.getMessage()}", e)
    }
  }

  public fun downloadNVM(dest : File) {
    http.execute(HttpGet(url)) {
      val status = getStatusLine()!!.getStatusCode()
      if (status != 200) throw error("${status} returned")
      val entity = getEntity()

      if (entity == null) throw error("No data was returned")
      val contentType = entity.getContentType()?.getValue()
      if ("application/zip" != contentType) throw error("Invalid content-type: ${contentType}")

      catchIO(ZipInputStream(entity.getContent()!!), {error("Failed to extract NVM", it)}) { zip ->
        FileUtil.createEmptyDir(dest)

        while(true) {
          val ze = zip.getNextEntry()
          if (ze == null) break
          if (ze.isDirectory()) continue
          val name = ze.getName().replace("\\", "/").trimStart("/").trimStart("nvm-master/")
          LOG.debug("nvm content: ${name}")

          if (name startsWith ".") continue
          if (name startsWith "test/") continue

          val file = dest / name
          catchIO(BufferedOutputStream(FileOutputStream(file)), {error("Failed to create ${file}", it)}) {
            zip.copyTo(it)
          }
        }
      }
    }
  }
}


public class NVMListener(val events:EventDispatcher<AgentLifeCycleListener>,
                         val processor : NVMProcessor) {
  private val bean = NVMBean();

  {
    events.addListener(object: AgentLifeCycleAdapter() {
      private fun AgentRunningBuild.feature() = getBuildFeaturesOfType(bean.NVMFeatureType).firstOrEmpty()
      private fun BuildRunnerContext.feature() = getBuild().feature()

        public override fun buildStarted(runningBuild: AgentRunningBuild) {
          val feature = runningBuild.feature()
          if (feature != null) processor.buildStarted(runningBuild, feature)
        }

        public override fun beforeRunnerStart(runner: BuildRunnerContext) {
          val feature = runner.feature()
          if (feature != null) processor.runnerStarted(runner, feature)

        }
      }
    )
  }
}

public class NVMProcessor(val downloader : NVMDownloader,
                          val plugin : PluginDescriptor,
                          val config : BuildAgentConfiguration,
                          val facade : BuildProcessFacade) {
  private val bean = NVMBean();
  private val LOG = log4j(javaClass<NVMProcessor>());

  public fun buildStarted(build : AgentRunningBuild, feature : AgentBuildFeature ) {
    val nvmHome = config.getCacheDirectory("nvm")
    val version = feature.getParameters()[bean.NVMVersion]

    val logger = build.getBuildLogger()
    logger.block("NVM") {
      block("Download", "Fetching NVM") {
        message("Downloading creatonix/nvm...")
        downloader.downloadNVM(nvmHome)
        message("NVM downloaded into ${nvmHome}")
      }

      block("Install", "Installing Node.js ${version}") {
        val commandLine = "!/bin/bash\n. ${nvmHome}/nvm.sh\nnvm use ${version}\n\${TEAMCITY_CAPTURE_ENV}"
        LOG.info("Executing NVM command: ${commandLine}")
        val ctx = facade.createBuildRunnerContext(build, SimpleRunnerConstants.TYPE, nvmHome.getPath())
        ctx.addRunnerParameter(SimpleRunnerConstants.USE_CUSTOM_SCRIPT, "true");
        ctx.addRunnerParameter(SimpleRunnerConstants.SCRIPT_CONTENT, commandLine);

        val callable = facade.createExecutable(build, ctx)
        callable.start()
        callable.waitFor()
      }
    }
  }

  public fun runnerStarted(runner : BuildRunnerContext, feature : AgentBuildFeature ) {

  }
}
