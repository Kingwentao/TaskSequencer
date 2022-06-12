package com.wtking.tasksequencer.base

import android.os.Process
import com.wtking.tasksequencer.bean.TaskType
import java.util.concurrent.CountDownLatch

/**
 * author: WentaoKing
 * created on: 2022/5/23
 * description: 任务基础代码，一般的任务可以复用此处代码
 */
abstract class BaseTask : ITask {

    private val mDependCountDownLatch = CountDownLatch(dependOnTaskList.size)

    override val taskType: TaskType
        get() = TaskType.IO_BOUND

    override val dependOnTaskList: List<Class<out BaseTask>>
        get() = mutableListOf()

    override fun priority(): Int {
        return Process.THREAD_PRIORITY_BACKGROUND
    }

    fun waitToNotify() {
        try {
            mDependCountDownLatch.await()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    fun notifyEndWait() {
        mDependCountDownLatch.countDown()
    }

}