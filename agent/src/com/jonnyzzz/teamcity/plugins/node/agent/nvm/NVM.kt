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

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.stream.JsonReader
import com.jonnyzzz.teamcity.plugins.node.agent.logging
import com.jonnyzzz.teamcity.plugins.node.agent.processes.CompositeProcessFactory
import com.jonnyzzz.teamcity.plugins.node.common.*
import jetbrains.buildServer.RunBuildException
import jetbrains.buildServer.agent.*
import jetbrains.buildServer.util.FileUtil
import org.apache.http.client.methods.HttpGet
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.util.*
import java.util.zip.ZipInputStream

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
                val latest: NVMLatestReleaseFetcher,
                val facade: CompositeProcessFactory) : AgentBuildRunner {
  private val bean = NVMBean();

  override fun createBuildProcess(runningBuild: AgentRunningBuild, context: BuildRunnerContext): BuildProcess {
    val nvmHome = runningBuild.agentConfiguration.getCacheDirectory("jonnyzzz.nvm")
    val version = context.runnerParameters[bean.NVMVersion]
    val fromSource = if(!context.runnerParameters[bean.NVMSource].isEmptyOrSpaces()) "-s " else ""
    val url = context.runnerParameters[bean.NVMURL] ?: (latest.getVersion()?.let { "https://github.com/creationix/nvm/archive/$it.zip" }) ?: bean.NVM_Creatonix

    return context.logging {
      facade.compositeBuildProcess(runningBuild) {
        execute("Download", "Fetching NVM") {
          message("Downloading creatonix/nvm...")
          message("from $url")
          downloader.downloadNVM(nvmHome, url)
          message("NVM downloaded into $nvmHome")
        }
        script("Install", "Installing Node.js v$version",nvmHome.path) {
          ". $nvmHome/nvm.sh" n "nvm install $fromSource $version"
        }
        script("Use", "Selecting Node.js v$version", nvmHome.path) {
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

class NVMLatestReleaseFetcher(val http: HttpClientWrapper) {
  companion object {
    private val LOG = log4j(NVMLatestReleaseFetcher::class.java)
    private val URL = "https://api.github.com/repos/creationix/nvm/releases/latest"
    private val ONE_HOUR_SECONDS = 60 * 60
    private val HALF_DAY_SECONDS = 12 * ONE_HOUR_SECONDS
  }

  @Transient private var version: String? = null
  @Transient private var etag: String? = null
  @Transient private var error: Boolean = false
  @Transient private var nextCheckTime: Long = 0


  fun getVersion(): String? {
    if (nextCheckTime > Date().time) {
      if (error) return null
      if (version != null) return version
    }

    // Do actual check
    synchronized(this) {
      if (nextCheckTime > Date().time) {
        if (error) return null
        if (version != null) return version
      }
      doGetVersion()
    }

    return version
  }

  private fun error(message: String) {
    LOG.error(message)
    error = true
    version = null
    etag = null
    nextCheckTime = Date().time + ONE_HOUR_SECONDS
  }

  private fun success(version: String, etag: String?) {
    LOG.info("Fetched latest version of nvm: $version")
    error = false
    this.version = version
    this.etag = etag
    nextCheckTime = Date().time + HALF_DAY_SECONDS
  }

  private fun doGetVersion() {
    error = false

    val request = HttpGet(URL)
    request.setHeader("Accept", "application/json")
    etag?.let { request.setHeader("If-None-Match", etag) }

    http.execute(request) {
      val status = statusLine!!.statusCode
      if (status == 304) {
        nextCheckTime = Date().time + HALF_DAY_SECONDS
        return@execute
      }
      if (status != 200) {
        return@execute error("Cannot check latest release: $status returned")
      }

      val entity = entity ?: return@execute error("No data was returned")

      val etag = getFirstHeader("ETag")?.value

      entity.content.use {
        val reader = JsonReader(InputStreamReader(it))
        val json: JsonObject
        try {
          json = Gson().fromJson<JsonObject>(reader, JsonObject::class.java)
        } catch(e: Exception) {
          return@execute error("Cannot parse response json")
        }
        val tag_name = json.get("tag_name")?.asString ?: return@execute error("'tag_name' is empty")
        return@execute success(tag_name, etag)
      }
    }
  }
}
