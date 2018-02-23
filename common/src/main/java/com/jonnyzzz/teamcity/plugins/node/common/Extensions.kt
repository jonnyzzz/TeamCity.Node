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

package com.jonnyzzz.teamcity.plugins.node.common

import java.io.File
import org.apache.log4j.Logger
import java.io.IOException
import jetbrains.buildServer.util.FileUtil
import java.io.Closeable

/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 12.01.13 0:41
 */

fun String?.isEmptyOrSpaces() : Boolean = com.intellij.openapi.util.text.StringUtil.isEmptyOrSpaces(this)

fun String.splitHonorQuotes() : List<String> = jetbrains.buildServer.util.StringUtil.splitHonorQuotes(this).filterNotNull()

infix fun String.n(s:String) : String = this + "\n" + s

fun String?.fetchArguments() : Collection<String> {
  if (this == null || this.isEmptyOrSpaces()) return listOf()

  return this
          .lines()
          .map { it.trim() }
          .filter { !it.isEmptyOrSpaces() }
          .flatMap{ it.splitHonorQuotes() }
}


fun File.resolveEx(relativePath : String) : File = jetbrains.buildServer.util.FileUtil.resolvePath(this, relativePath)

data class TempFileName(val prefix : String, val suffix : String)
fun File.tempFile(details : TempFileName) : File = com.intellij.openapi.util.io.FileUtil.createTempFile(this, details.prefix, details.suffix, true) ?: throw IOException("Failed to create temp file under ${this}")

fun File.smartDelete() = com.intellij.openapi.util.io.FileUtil.delete(this)

//we define this category to have plugin logging without logger configs patching
fun <T> log4j(clazz : Class<T>) : Logger = Logger.getLogger("jetbrains.buildServer.${clazz.name}")!!
operator fun File.div(child : String) : File = File(this, child)
fun String.trimStart(x : String) : String = if (startsWith(x)) substring(x.length) else this

inline fun <T, S:Closeable>using(stream:S, action:(S)->T) : T {
  try {
    return action(stream)
  } finally {
    FileUtil.close(stream)
  }
}

inline fun <T, S:Closeable>catchIO(stream:S, error:(IOException) -> Throwable, action:(S)->T) : T =
  using(stream) {
    try {
      action(stream)
    } catch(e: IOException) {
      throw error(e)
    }
  }

fun <K,V,T:MutableMap<K, V>> T.plus(m:Map<K, V>) : T {
  this.putAll(m)
  return this
}
