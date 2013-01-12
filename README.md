This is a plugin for TeamCity that provides Node.js build runner. 

Plugin is implemented with (http://kotlin.jetbrains.org/)[JetBrains Kotlin]

Release builds are found under releases branch

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


Building
=========

Use IDEA build runner in TeamCity of
Intellij IDEA 12 to develop


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