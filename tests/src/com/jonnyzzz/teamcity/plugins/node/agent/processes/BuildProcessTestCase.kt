package com.jonnyzzz.teamcity.plugins.node.agent.processes

import com.jonnyzzz.teamcity.plugins.node.agent.LoggingTestCase
import jetbrains.buildServer.RunBuildException
import jetbrains.buildServer.agent.BuildFinishedStatus
import jetbrains.buildServer.agent.BuildProcess
import org.jetbrains.annotations.Nullable
import org.testng.Assert
import org.testng.annotations.DataProvider

public open class BuildProcessTestCase() : LoggingTestCase() {
  DataProvider(name = "buildFinishStatuses")
  public open fun buildStatuses(): Array<Array<Any>> {
    val list = arrayListOf<Array<Any>>()
    for (`val` in BuildFinishedStatus.values())
    {
      list.add(array<Any>(`val`))
    }
    return list.toArray(array<Array<Any>>())
  }

  protected open fun assertRunSuccessfully(proc: BuildProcess, result: BuildFinishedStatus): Unit {
    var status: BuildFinishedStatus? = null
    try
    {
      proc.start()
      status = proc.waitFor()
    } catch (e: RunBuildException) {
      Assert.fail("Failed with exception " + e)
    }

    Assert.assertEquals(status, result)
  }

  protected open fun assertRunException(proc: BuildProcess, message: String): Unit {
    try
    {
      proc.start()
      proc.waitFor()
      Assert.fail("Exception expected")
    } catch (e: RunBuildException) {
      Assert.assertTrue((e.getMessage()?.contains(message))!!, e.toString())
    }
  }

  public open inner class RecordingBuildProcess(val id: String,
                                     val resultStatus: BuildFinishedStatus,
                                     val startException: Throwable? = null,
                                     val finishException: Throwable? = null): BuildProcess {
    public override fun start(): Unit {
      log("start-" + id)
      throwExceptionIfPossible(startException)
    }

    private fun throwExceptionIfPossible(ex: Throwable?): Unit {
      if (ex != null) throw ex
    }

    public override fun isInterrupted(): Boolean = false
    public override fun isFinished(): Boolean = false

    public override fun interrupt(): Unit {
      log("interrupt-" + id)
    }

    public override fun waitFor(): BuildFinishedStatus {
      log("waitFor-" + id)
      throwExceptionIfPossible(finishException)
      return resultStatus
    }
  }
}
