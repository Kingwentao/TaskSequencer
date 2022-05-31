package com.wtking.tasksequencer.base

import com.wtking.tasksequencer.bean.TaskType
import java.util.concurrent.CountDownLatch

/**
 * author: WentaoKing
 * created on: 2022/5/23
 * description: 任务基础代码，一般的任务可以复用此处代码
 */
abstract class BaseTask : ITask {

    private val mDepends = CountDownLatch(dependOnTaskList.size)

    override val taskType: TaskType
        get() = TaskType.IO_BOUND

    override val dependOnTaskList: List<Class<out BaseTask>>
        get() = mutableListOf()

    override fun priority(): Int {
        return 10
    }

    override fun needWait(): Boolean {
        return false
    }

    fun waitToNotify() {
        try {
            mDepends.await()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    fun notifyWait() {
        mDepends.countDown()
    }

}