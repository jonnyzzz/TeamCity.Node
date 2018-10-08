/*
 * Copyright 2013-2015 Eugene Petrenko
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
import kotlin.text.Regex

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 05.08.13 9:35
 */
///https://github.com/creationix/nvm
///http://ghb.freshblurbs.com/blog/2011/05/07/install-node-js-and-express-js-nginx-debian-lenny.html
class NVMDownloader(val http:HttpClientWrapper) {
  private val LOG = log4j(NVMDownloader::class.java)

  private fun error(url:String, message:String) : Throwable {
    throw RunBuildException("Failed to download NVM from $url. $message")
  }

  private fun error(url:String, message:String, e:Throwable) : Throwable {
    throw RunBuildException("Failed to download NVM from $url. $message. ${e.message}", e)
  }

  fun downloadNVM(dest : File, url : String) {
    val httpGet = HttpGet(url)
    http.execute(httpGet) {
      val status = statusLine!!.statusCode
      if (status != 200) throw error(url, "$status returned")

      val entity = entity ?: throw error(url, "No data was returned")

      catchIO(ZipInputStream(entity.content!!), {error(url, "Failed to extract NVM", it)}) { zip ->
        FileUtil.createEmptyDir(dest)

        var hasFiles = false
        while(true) {
          val ze = zip.nextEntry ?: break
          if (ze.isDirectory) continue
          val name = ze.name.replace("\\", "/").trimStart("/").replace(Regex("^nvm-[^/]+/(.*)"), "$1")
          LOG.debug("nvm content: $name")

          if (name.startsWith(".")) continue
          if (name.startsWith("test/")) continue

          val file = dest / name

          file.parentFile?.mkdirs()
          catchIO(BufferedOutputStream(FileOutputStream(file)), {error(url, "Failed to create $file", it)}) {
            zip.copyTo(it)
            hasFiles = true
          }
        }

        if (!hasFiles) error(url, "Downloaded package contains no files")
      }
    }
  }
}

class NVMRunner(val downloader : NVMDownloader,
                       val facade : CompositeProcessFactory) : AgentBuildRunner {
  private val bean = NVMBean();

  override fun createBuildProcess(runningBuild: AgentRunningBuild, context: BuildRunnerContext): BuildProcess {
    val nvmHome = runningBuild.agentConfiguration.getCacheDirectory("jonnyzzz.nvm")
    val version = context.runnerParameters[bean.NVMVersion]
    val fromSource = if(!context.runnerParameters[bean.NVMSource].isEmptyOrSpaces()) "-s " else ""
    val url = context.runnerParameters[bean.NVMURL] ?: bean.NVM_Creatonix
    var isWindows = runningBuild.agentConfiguration.systemInfo.isWindows

    return context.logging {
      facade.compositeBuildProcess(runningBuild) {
        execute("Download", "Fetching NVM") {
          message("Downloading creatonix/nvm...")
          message("from $url")
          downloader.downloadNVM(nvmHome, url)
          message("NVM downloaded into $nvmHome")
        }
        
        script("Install", "Installing Node.js v$version",nvmHome.path) {
          if (isWindows)
            "nvm install $fromSource $version"
          else
            ". $nvmHome/nvm.sh" n "nvm install $fromSource $version"
        }

        script("Use", "Selecting Node.js v$version", nvmHome.path) {
          if (isWindows)
            "nvm use $version"
          else
            ". $nvmHome/nvm.sh" n "nvm use $version" n "eval \${TEAMCITY_CAPTURE_ENV}"
        }
      }
    }
  }

  override fun getRunnerInfo(): AgentBuildRunnerInfo = object:AgentBuildRunnerInfo {
    override fun getType(): String = bean.NVMFeatureType

    override fun canRun(agentConfiguration: BuildAgentConfiguration): Boolean = true
  }
}
