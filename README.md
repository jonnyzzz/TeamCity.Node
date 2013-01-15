TeamCity Node plugin
====================

This is a plugin for TeamCity that provides [Node.js](http://nodejs.org/) and [Phantom.JS](http://phantomjs.org) build runners. 
Plugin is implemented with [JetBrains Kotlin](http://kotlin.jetbrains.org/)
Release builds are found under releases branch

Features
========
Plugin detects system wide installed node.js and reports it's version as configuration parameter `node.js`
There is also `node.js` build runner to run node.js on given `.js` file or javascript source code 
It also provides Phantom.JS build runner to run from given `.js` or `.coffee` file or source code


License
==========
Apache 2.0

Supported Versions
==================

Plugin is test to work with TeamCity 7.1. 
It should work with 7.0 (and maybe 6.5) under JDK 1.6

Downloading Build
=================

There will be a build on TeamCity. TBD.
For now, see `releases` branch for binary releases

Installation
============
To install plugin, simply put downloaded plugin `.zip` file into `<TeamCity Data Directory>/plugins` folder and restart TeamCity Server. 
For more details, there is [documentation](http://confluence.jetbrains.net/display/TCD7/Installing+Additional+Plugins)


Building
=========

Use IDEA build runner in TeamCity of
Intellij IDEA 12 with Kotlin plugin to develop


In this repo you will find
=============================
- TeamCity server and agent plugin bindle
- Plugin version will be patched if building with IDEA build runner in TeamCity
- Run configuration `server` to run/debug plugin under TeamCity (use `http://localhost:8111/bs`)
- pre-configured IDEA settings to support references to TeamCity
- Uses `$TeamCityDistribution$` IDEA path variable as path to TeamCity home (unpacked .tar.gz or .exe distribution)
- Bunch of libraries for most recent needed TeamCity APIs
- Module with TestNG tests that uses TeamCity Tests API


Note
====

This plugin was created with [https://github.com/jonnyzzz/TeamCity.PluginTemplate](TeamCity Plugin Template)
