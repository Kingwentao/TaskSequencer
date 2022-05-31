package com.wtking.tasksequencer.dispatcher

import com.wtking.tasksequencer.base.BaseTask

/**
 * author: WentaoKing
 * created on: 2022/5/23
 * description: 对task封装的Runnable
 */
class TaskRunnable(private val mTask: BaseTask, private val mTaskDispatcher: TaskDispatcher): Runnable {

    override fun run() {
        mTask.waitToNotify()
        mTask.run()
        mTaskDispatcher.notifyDependedTasks(mTask)
        mTask.notifyWait()
    }

}