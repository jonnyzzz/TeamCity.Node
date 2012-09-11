This is sample IDEA project for creating plugins for TeamCity.

In this sample you will find
=============================
- TeamCity server and agent plugin bindle
- Plugin version will be patched if building with IDEA build runner in TeamCity
- Run configuration `server` to run/debug plugin under TeamCity (use `http://localhost:8111/bs`)
- One jar containing
-- server plugin classes
-- server plugin web resources (with pre-configured IDEA settings to support references to TeamCity)
- Uses `$TeamCityDistribution$` IDEA path variable as path to TeamCity home (unpacked .tar.gz or .exe distribution)
- Bunch of libraries for most recent needed TeamCity APIs
- Module with TestNG tests that uses TeamCity Tests API


What's next? 
=============
 - Fork this repository
 - Change project name in IDEA
 - Update `teamcity-server-plugin.xml` to put plugin name, plugin display name and description
 - Rename `server/src/META-INF/build-server-plugin-PLUGIN_NAME.xml` to put your plugin name here and update `server/server.iml`
 - Rename `agent/src/META-INF/build-server-agent-PLUGIN_NAME.xml` to put your plugin name here and update `agent/agent.iml`
 - Update plugin .jar file name in `plugin` and `common-jar` artifacts
 - Update plugin .zip file name in `plugin-zip` artifact
 - Have fun!


Steps to fork template to a given repository
===========================================
 - call git init or create new repo and local copy
 - git remote add template `git://github.com/jonnyzzz/TeamCity.PluginTemplate.git`
 - git fetch template
 - git merge template/master
 - git remote rm template

Those steps makes you repo contain default template indise. 
It's most easiest way to start.


License
=======
You may do what ever you like with those sources. 
or I could also say the license is MIT.