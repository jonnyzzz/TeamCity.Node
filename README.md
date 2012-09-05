This is sample IDEA project for creating plugins for TeamCity.

In this sample you will find:
=============================
- TeamCity server plugin
- With teamcity-plugin.xml
- Plugin version will be patched if building with IDEA build runner in TeamCity
- Run configuration to run/debug plugin under TeamCity (use http://localhost:8111)
- One jar containing
-- server plugin classes
-- server plugin web resources (with pre-configured IDEA settings to support references to TeamCity)
- Uses $TeamCityDistribution$ IDEA path variable as path to TeamCity home (unpacked .tar.gz or .exe distribution)
- Bunch of libraries for most recent needed TeamCity APIs
- Module with TestNG tests that uses TeamCity Tests API


What's next? 
=============
 - Fork this repository, 
 - Rename plugin jar's with right name 
   (replace PLUGIN with your plugin name in artifacts and teamcity-server-plugin.xml)
 - Have fun!


Steps to copy start with a given repository
===========================================
 - call git init or create new repo and local copy
 - git remote add template git://github.com/jonnyzzz/TeamCity.PluginTemplate.git
 - git fetch template
 - git merge template/master
 - git remote rm template
Those steps makes you repo contain default template indise. 
It's most easiest way to start.


License:
=========
You may do what ever you like with those sources. 
or I could also say the license is MIT.