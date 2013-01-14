package com.jonnyzzz.teamcity.plugins.node.agent.processes

import com.jonnyzzz.teamcity.plugins.node.agent.processes.BuildProcessTestCase.*
import jetbrains.buildServer.RunBuildException
import jetbrains.buildServer.agent.BuildFinishedStatus
import jetbrains.buildServer.agent.BuildProcess
import org.testng.Assert
import org.testng.annotations.Test
import java.util.concurrent.atomic.AtomicReference
import jetbrains.buildServer.agent.BuildFinishedStatus.FINISHED_SUCCESS
import jetbrains.buildServer.agent.BuildFinishedStatus.INTERRUPTED

public class DelegatingBuildProcessTest(): BuildProcessTestCase() {
  Test
  public fun test_FailedToCreateDelegate()  {
    val aaa = DelegatingBuildProcess(object : LoggingAction(FINISHED_SUCCESS) {
      public override fun startImpl(): BuildProcess {
        super.startImpl()
        throw RunBuildException("aaa")
      }
    })

    Assert.assertFalse(aaa.isFinished())
    assertRunException(aaa, "aaa")
    assertLog("start-impl", "finish-impl")
    Assert.assertTrue(aaa.isFinished())
  }

  Test
  public fun test_interrupted_before_start()  {
    val aaa = DelegatingBuildProcess(LoggingAction(FINISHED_SUCCESS))
    aaa.interrupt()
    assertRunSuccessfully(aaa, INTERRUPTED)

    assertLog()
    Assert.assertTrue(aaa.isFinished())
    Assert.assertTrue(aaa.isInterrupted())
  }

  Test
  public fun test_interrupted_in_startImpl() {
    var bp: BuildProcess? = null
    val aaa = DelegatingBuildProcess(object : LoggingAction(FINISHED_SUCCESS) {
      public override fun startImpl(): BuildProcess {
        bp!!.interrupt()
        return super.startImpl()
      }
    })
    bp = aaa
    assertRunSuccessfully(aaa, INTERRUPTED)
    assertLog("start-impl", "finish-impl")
    Assert.assertTrue(aaa.isFinished())
    Assert.assertTrue(aaa.isInterrupted())
  }

  Test
  public fun test_interrupted_in_process_start() {
    var bp: BuildProcess? = null
    val aaa = DelegatingBuildProcess(object : LoggingActionBase() {
      protected override fun createSub(): RecordingBuildProcess {
        return object : RecordingBuildProcess("i", BuildFinishedStatus.FINISHED_SUCCESS) {
          public override fun start(): Unit {
            super.start()
            bp!!.interrupt()
          }
        }
      }
    })
    bp = aaa
    assertRunSuccessfully(aaa, INTERRUPTED)
    assertLog("start-impl", "start-i", "interrupt-i", "waitFor-i", "finish-impl")
    Assert.assertTrue(aaa.isFinished())
    Assert.assertTrue(aaa.isInterrupted())
  }

  Test
  public fun test_interrupted_in_process_finish()  {
    var bp : BuildProcess? = null
    val aaa = DelegatingBuildProcess(
            object : LoggingActionBase() {
      protected override fun createSub(): RecordingBuildProcess {
        return object : RecordingBuildProcess("i", BuildFinishedStatus.FINISHED_SUCCESS) {
          public override fun waitFor(): BuildFinishedStatus {
            bp!!.interrupt()
            return super.waitFor()
          }
        }
      }
    })
    bp = aaa

    assertRunSuccessfully(aaa, INTERRUPTED)
    assertLog("start-impl", "start-i", "interrupt-i", "waitFor-i", "finish-impl")
    Assert.assertTrue(aaa.isFinished())
    Assert.assertTrue(aaa.isInterrupted())
  }

  Test
  public fun test_interrupted_in_process_finishImpl() {
    var bp : BuildProcess? = null
    val aaa = DelegatingBuildProcess(
            object : LoggingAction(BuildFinishedStatus.FINISHED_SUCCESS) {
      public override fun finishedImpl(): Unit {
        bp!!.interrupt()
        super.finishedImpl()
      }
    })
    bp = aaa
    assertRunSuccessfully(aaa, INTERRUPTED)
    assertLog("start-impl", "start-i", "waitFor-i", "finish-impl")
    Assert.assertTrue(aaa.isFinished())
    Assert.assertTrue(aaa.isInterrupted())
  }

  Test(dataProvider = "buildFinishStatuses"  )
  public fun test_with_sub_action(status: BuildFinishedStatus) {
    var aaa = DelegatingBuildProcess(LoggingAction(status))
    assertRunSuccessfully(aaa, status)
    assertLog("start-impl", "start-i", "waitFor-i", "finish-impl")
    Assert.assertTrue(aaa.isFinished())
  }

  private open class LoggingAction(val status: BuildFinishedStatus): LoggingActionBase() {
    protected override fun createSub(): RecordingBuildProcess = RecordingBuildProcess("i", status)
  }

  private abstract class LoggingActionBase(): DelegatingProcessAction {
    public override fun startImpl(): BuildProcess {
      log("start-impl")
      return createSub()
    }
    protected abstract fun createSub(): RecordingBuildProcess
    public override fun finishedImpl(): Unit {
      log("finish-impl")
    }
  }
}
