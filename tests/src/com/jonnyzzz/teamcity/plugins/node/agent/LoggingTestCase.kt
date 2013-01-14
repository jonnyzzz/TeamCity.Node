package com.jonnyzzz.teamcity.plugins.node.agent

import jetbrains.buildServer.BaseTestCase
import jetbrains.buildServer.util.StringUtil
import org.testng.Assert
import org.testng.annotations.BeforeMethod
import com.jonnyzzz.teamcity.plugins.node.common.join

public open class LoggingTestCase(): BaseTestCase() {
  private var myLog = arrayListOf<String>()

  protected fun log(message: String): Unit {
    myLog add message
  }

  BeforeMethod
  protected override fun setUp(): Unit {
    super.setUp()
    myLog = arrayListOf<String>()
  }

  protected open fun assertLog(vararg gold: String): Unit {
    val actual = myLog  join "\n"
    val expected = gold join "\n"
    Assert.assertEquals(actual, expected)
  }
}
