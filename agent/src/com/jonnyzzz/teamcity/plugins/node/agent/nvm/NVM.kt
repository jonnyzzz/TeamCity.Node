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
import jetbrains.buildServer.agent.AgentRunningBuild
import jetbrains.buildServer.agent.BuildRunnerContext
import com.jonnyzzz.teamcity.plugins.node.common.*
import jetbrains.buildServer.agent.BuildAgentConfiguration
import jetbrains.buildServer.agent.AgentBuildRunner
import jetbrains.buildServer.agent.AgentBuildRunnerInfo
import jetbrains.buildServer.agent.BuildProcess
import com.jonnyzzz.teamcity.plugins.node.agent.logging
import com.jonnyzzz.teamcity.plugins.node.agent.processes.CompositeProcessFactory

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 05.08.13 9:35
 */
///https://github.com/creationix/nvm
///http://ghb.freshblurbs.com/blog/2011/05/07/install-node-js-and-express-js-nginx-debian-lenny.html
public class NVMDownloader(val http:HttpClientWrapper) {
  private val LOG = log4j(javaClass<NVMDownloader>())

  private fun error(url:String, message:String) : Throwable {
    throw RunBuildException("Failed to download NVM from ${url}. ${message}")
  }

  private fun error(url:String, message:String, e:Throwable) : Throwable {
    throw RunBuildException("Failed to download NVM from ${url}. ${message}. ${e.getMessage()}", e)
  }

  public fun downloadNVM(dest : File, url : String) {
    val httpGet = HttpGet(url)
    http.execute(httpGet) {
      val status = getStatusLine()!!.getStatusCode()
      if (status != 200) throw error(url, "${status} returned")

      val entity = getEntity()
      if (entity == null) throw error(url, "No data was returned")

      catchIO(ZipInputStream(entity.getContent()!!), {error(url, "Failed to extract NVM", it)}) { zip ->
        FileUtil.createEmptyDir(dest)

        var hasFiles = false
        while(true) {
          val ze = zip.getNextEntry()
          if (ze == null) break
          if (ze.isDirectory()) continue
          val name = ze.getName().replace("\\", "/").trimStart("/").trimStart("nvm-master/")
          LOG.debug("nvm content: ${name}")

          if (name startsWith ".") continue
          if (name startsWith "test/") continue

          val file = dest / name
          catchIO(BufferedOutputStream(FileOutputStream(file)), {error(url, "Failed to create ${file}", it)}) {
            zip.copyTo(it)
            hasFiles = true
          }
        }

        if (!hasFiles) error(url, "Downloaded package contains no files")
      }
    }
  }
}

public class NVMRunner(val downloader : NVMDownloader,
                       val facade : CompositeProcessFactory) : AgentBuildRunner {
  private val bean = NVMBean();
  private val LOG = log4j(javaClass<NVMRunner>());

  public override fun createBuildProcess(runningBuild: AgentRunningBuild, context: BuildRunnerContext): BuildProcess {
    val nvmHome = runningBuild.getAgentConfiguration().getCacheDirectory("jonnyzzz.nvm")
    val version = context.getRunnerParameters()[bean.NVMVersion]
    val fromSource = if(!context.getRunnerParameters()[bean.NVMSource].isEmptyOrSpaces()) "-s " else ""
    val url = context.getRunnerParameters()[bean.NVMURL] ?: bean.NVM_Creatonix

    return context.logging {
      facade.compositeBuildProcess(runningBuild) {
        execute("Download", "Fetching NVM") {
          message("Downloading creatonix/nvm...")
          message("from ${url}")
          downloader.downloadNVM(nvmHome, url)
          message("NVM downloaded into ${nvmHome}")
        }
        script("Install", "Installing Node.js v${version}",nvmHome.getPath()) {
          ". ${nvmHome}/nvm.sh" n "nvm install ${fromSource} ${version}"
        }
        script("Use", "Selecting Node.js v${version}",nvmHome.getPath()) {
          ". ${nvmHome}/nvm.sh" n "nvm use ${version}" n "\${TEAMCITY_CAPTURE_ENV}"
        }
      }
    }
  }

  public override fun getRunnerInfo(): AgentBuildRunnerInfo = object:AgentBuildRunnerInfo {
    public override fun getType(): String = bean.NVMFeatureType

    public override fun canRun(agentConfiguration: BuildAgentConfiguration): Boolean = true
  }
}
