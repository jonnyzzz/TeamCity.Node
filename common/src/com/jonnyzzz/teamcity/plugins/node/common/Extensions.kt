package com.jonnyzzz.teamcity.plugins.node.common

import java.io.File
import jetbrains.buildServer.util.FileUtil

/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 12.01.13 0:41
 */

fun String?.isEmptyOrSpaces() : Boolean = com.intellij.openapi.util.text.StringUtil.isEmptyOrSpaces(this)

fun String.splitHonorQuotes() : List<String> = jetbrains.buildServer.util.StringUtil.splitHonorQuotes(this).filterNotNull()

fun File.resolve(relativePath : String) : File = FileUtil.resolvePath(this, relativePath)
