TeamCity Node plugin
====================

This is a plugin for TeamCity that provides following build runners
- [Node.js](http://nodejs.org/) 
- [NPM](https://npmjs.org/)
- [Phantom.JS](http://phantomjs.org) 
- [Grunt](http://gruntjs.com)
- [NVM](https://github.com/creationix/nvm)
- [Gulp](http://gulpjs.com/)


Plugin is implemented with [JetBrains Kotlin](http://kotlin.jetbrains.org/)


Features
========
- Environment detection
 - Plugin detects node.js in system PATH `node.js` and reports it's version as configuration parameter `node.js`.
 - Plugin detects system wide installed `npm` and reports it's version as configuration parameter `node.js.npm`
- Build Runners
 - `node.js` build runner to run `.js` file or source code
 - `node.js NPM` build runner to execute NPM commands
 - `node.js NVM Installer` build runner to install/update to selected version of Node.js
 - `Phantom.JS` build runner to run `.js`, `.coffee` (or other script) file or source code.
 - `Grunt` build runner to run your grunt scripts. It's assumes you have `grunt` and `grunt-cli` NPM packages installed to local repo
 - `Gulp` build runner to run you gulp scripts


License
==========
Apache 2.0

Supported Versions
==================

Plugin is tested to work with TeamCity 7.1, 8.0, 8.1, 9.0 and 9.1.
It should work with 7.0 (and maybe 6.5)

Agent and server are expected to run JRE 1.7 (or maybe JRE 1.6)

Downloading Build
=================

Download the latest build from [TeamCity](http://teamcity.jetbrains.com/viewType.html?buildTypeId=bt434) 

Installation
============

* Download the plugin build (binaries) from http://teamcity.jetbrains.com/guestAuth/repository/download/bt434/.lastSuccessful/jonnyzzz.node.zip
* Make sure downloaded `.zip` file is not corrupted
* Put the downloaded plugin `.zip` file into `<TeamCity Data Directory>/plugins` folder
* Restart the TeamCity Server 
* Open ```Administration | Plugins``` and check you see the plugin listed

For more details, there is [documentation](http://confluence.jetbrains.net/display/TCD7/Installing+Additional+Plugins)


Building
=========
- Define `$TeamCityDistribution$` IDEA path variable with path to TeamCity home (unpacked `.tar.gz` or installed `.exe` distribution).
- Add tomcat application server named `Tomcat 7` into IDEA settings from TeamCity distribution path
- Use IDEA build runner in TeamCity of Intellij IDEA 12 with Kotlin plugin to develop

In this repo you will find
=============================
- TeamCity server and agent plugin bundle
- Plugin version will be patched if building with IDEA build runner in TeamCity
- Run configuration `server` to run/debug plugin under TeamCity (use `http://localhost:8111/bs`)
- pre-configured IDEA settings to support references to TeamCity
- Uses `$TeamCityDistribution$` IDEA path variable as path to TeamCity home (unpacked .tar.gz or .exe distribution)
- Bunch of libraries for most recent needed TeamCity APIs
- Module with TestNG tests that uses TeamCity Tests API


Note
====

This plugin was created with [TeamCity Plugin Template](https://github.com/jonnyzzz/TeamCity.PluginTemplate)

This is my (Eugene Petrenko) private home project

You may support my home projects:
[![Donate](https://www.paypalobjects.com/en_US/i/btn/btn_donate_LG.gif)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=Y94ALDEKVZT3Y)
