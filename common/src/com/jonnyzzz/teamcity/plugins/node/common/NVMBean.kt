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

package com.jonnyzzz.teamcity.plugins.node.common

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 16.08.13 21:42
 */
class NVMBean {
  val NVMUsed : String = "Use_NodeJS_Install_Runner"
  // Whether nvm can be installed on agent
  val NVMInstallable: String = "node.js.nvm.installable"
  val NVMFeatureType: String = "jonnyzzz.nvm"
  val NVMVersion : String = "version"
  val NVMSource : String = "fromSource"
  val NVMURL : String = "fromURL"

  // Comma separated list of installed node.js versions
  val InstalledVersions: String = "node.js.nvm.installed.versions"
  // NVM_DIR
  val Path : String = "node.js.nvm.dir"
  // NVM version (`nvm --version`)
  val Version : String = "node.js.nvm.version"

  val NVM_Creatonix : String = "https://github.com/creationix/nvm/archive/v0.7.0.zip"
  //"https://github.com/creationix/nvm/archive/master.zip"
}
