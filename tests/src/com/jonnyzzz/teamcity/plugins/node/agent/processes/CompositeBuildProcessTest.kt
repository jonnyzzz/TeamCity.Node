package com.jonnyzzz.teamcity.plugins.node.agent.processes

import com.jonnyzzz.teamcity.plugins.node.agent.processes.BuildProcessTestCase.*
import jetbrains.buildServer.RunBuildException
import jetbrains.buildServer.agent.BuildFinishedStatus
import org.testng.Assert
import org.testng.annotations.Test

public class CompositeBuildProcessTest() : BuildProcessTestCase() {
  Test
  public fun test_empty_build_process() {
    val i = CompositeBuildProcessImpl()
    assertRunSuccessfully(i, BuildFinishedStatus.FINISHED_SUCCESS)
  }

  Test(dataProvider = "buildFinishStatuses")
  public fun test_one_build_process(result: BuildFinishedStatus): Unit {
    val i = CompositeBuildProcessImpl()
    i.pushBuildProcess(RecordingBuildProcess("1", result))

    assertRunSuccessfully(i, (if (result == BuildFinishedStatus.INTERRUPTED )
      BuildFinishedStatus.FINISHED_SUCCESS
    else  result))
    assertLog("start-1", "waitFor-1")
  }

  Test
  public fun test_stopOnFirstError() {
    val i = CompositeBuildProcessImpl()
    i.pushBuildProcess(RecordingBuildProcess("1", BuildFinishedStatus.FINISHED_FAILED))
    i.pushBuildProcess(RecordingBuildProcess("2", BuildFinishedStatus.FINISHED_SUCCESS))
    assertRunSuccessfully(i, BuildFinishedStatus.FINISHED_FAILED)
    assertLog("start-1", "waitFor-1")
  }

  Test
  public fun test_stopOnFirstError2() {
    val i = CompositeBuildProcessImpl()
    i.pushBuildProcess(RecordingBuildProcess("1", BuildFinishedStatus.FINISHED_SUCCESS))
    i.pushBuildProcess(RecordingBuildProcess("2", BuildFinishedStatus.FINISHED_FAILED))
    i.pushBuildProcess(RecordingBuildProcess("3", BuildFinishedStatus.FINISHED_SUCCESS))
    assertRunSuccessfully(i, BuildFinishedStatus.FINISHED_FAILED)
    assertLog("start-1", "waitFor-1", "start-2", "waitFor-2")
  }

  Test
  public fun test_stopOnStartException()  {
    val i = CompositeBuildProcessImpl()
    i.pushBuildProcess(RecordingBuildProcess("1", BuildFinishedStatus.FINISHED_SUCCESS))
    i.pushBuildProcess(RecordingBuildProcess("2", BuildFinishedStatus.FINISHED_SUCCESS, RunBuildException("aaa")))
    i.pushBuildProcess(RecordingBuildProcess("3", BuildFinishedStatus.FINISHED_SUCCESS))
    assertRunException(i, "aaa")
    assertLog("start-1", "waitFor-1", "start-2")
  }

  Test
  public fun test_stopOnWaitForException()  {
    val i = CompositeBuildProcessImpl()
    i.pushBuildProcess(RecordingBuildProcess("1", BuildFinishedStatus.FINISHED_SUCCESS))
    i.pushBuildProcess(RecordingBuildProcess("2", BuildFinishedStatus.FINISHED_SUCCESS, null,RunBuildException("aaa")))
    i.pushBuildProcess(RecordingBuildProcess("3", BuildFinishedStatus.FINISHED_SUCCESS))
    assertRunException(i, "aaa")
    assertLog("start-1", "waitFor-1", "start-2", "waitFor-2")
  }

  Test
  public fun test_emptyInterrupted() {
    val i = CompositeBuildProcessImpl()
    i.interrupt()

    Assert.assertFalse(i.isFinished())
    Assert.assertTrue(i.isInterrupted())
    assertRunSuccessfully(i, BuildFinishedStatus.INTERRUPTED)
    Assert.assertTrue(i.isInterrupted())
    Assert.assertTrue(i.isFinished())
  }

  Test
  public fun test_interruptCalledForFirst() {
    val i = CompositeBuildProcessImpl()
    i.pushBuildProcess(object : RecordingBuildProcess("1", BuildFinishedStatus.FINISHED_SUCCESS) {
      public override fun start(): Unit {
        super<RecordingBuildProcess>.start()
        i.interrupt()
      }
    })
    i.pushBuildProcess(RecordingBuildProcess("f", BuildFinishedStatus.FINISHED_SUCCESS))
    assertRunSuccessfully(i, BuildFinishedStatus.INTERRUPTED)
    assertLog("start-1", "interrupt-1", "waitFor-1")
  }

  Test
  public fun test_interruptCalledForFirst_WaitFor() {
    val i = CompositeBuildProcessImpl()
    i.pushBuildProcess(object : RecordingBuildProcess("1", BuildFinishedStatus.FINISHED_SUCCESS) {
      public override fun waitFor(): BuildFinishedStatus {
        i.interrupt()
        return super.waitFor()
      }
    })
    i.pushBuildProcess(RecordingBuildProcess("f", BuildFinishedStatus.FINISHED_SUCCESS))
    assertRunSuccessfully(i, BuildFinishedStatus.INTERRUPTED)
    assertLog("start-1", "interrupt-1", "waitFor-1")
  }

  Test
  public fun test_interruptCalledForTwo() {
    val i = CompositeBuildProcessImpl()
    i.pushBuildProcess(RecordingBuildProcess("0", BuildFinishedStatus.FINISHED_SUCCESS))
    i.pushBuildProcess(object : RecordingBuildProcess("1", BuildFinishedStatus.FINISHED_SUCCESS) {
      public override fun start(): Unit {
        super.start()
        i.interrupt()
      }
    })
    i.pushBuildProcess(RecordingBuildProcess("f", BuildFinishedStatus.FINISHED_SUCCESS))
    assertRunSuccessfully(i, BuildFinishedStatus.INTERRUPTED)
    assertLog("start-0", "waitFor-0", "start-1", "interrupt-1", "waitFor-1")
  }

  Test
  public fun test_interruptCalledForTwo_WaitFor() {
    val i = CompositeBuildProcessImpl()
    i.pushBuildProcess(RecordingBuildProcess("0", BuildFinishedStatus.FINISHED_SUCCESS))
    i.pushBuildProcess(object : RecordingBuildProcess("1", BuildFinishedStatus.FINISHED_SUCCESS) {
      public override fun waitFor(): BuildFinishedStatus {
        i.interrupt()
        return super.waitFor()
      }
    })
    i.pushBuildProcess(RecordingBuildProcess("f", BuildFinishedStatus.FINISHED_SUCCESS))
    assertRunSuccessfully(i, BuildFinishedStatus.INTERRUPTED)
    assertLog("start-0", "waitFor-0", "start-1", "interrupt-1", "waitFor-1")
  }
}
