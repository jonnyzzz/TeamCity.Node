package TeamCityNodePlugin.vcsRoots

import jetbrains.buildServer.configs.kotlin.v2017_2.*
import jetbrains.buildServer.configs.kotlin.v2017_2.vcs.GitVcsRoot

object git___github_com_jonnyzzz_TeamCity_Node_git : GitVcsRoot({
    uuid = "6f7467a2-ad85-4fd5-afd8-a42ba19c617f"
    id = "git___github_com_jonnyzzz_TeamCity_Node_git"
    name = "git://github.com/jonnyzzz/TeamCity.Node.git"
    url = "ssh://git@github.com/jonnyzzz/TeamCity.Node.git"
    branchSpec = """
        +:refs/pull/(*)/head
        +:refs/heads/(*)
    """.trimIndent()
    userNameStyle = GitVcsRoot.UserNameStyle.FULL
    useMirrors = false
    authMethod = uploadedKey {
        userName = "git"
        uploadedKey = "GitHub RW"
    }
})
