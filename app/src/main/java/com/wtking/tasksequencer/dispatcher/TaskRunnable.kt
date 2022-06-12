package com.wtking.tasksequencer.dispatcher

import android.os.Process
import com.wtking.tasksequencer.base.BaseTask

/**
 * author: WentaoKing
 * created on: 2022/5/23
 * description: 对task封装的Runnable
 */
class TaskRunnable(
    private val mTask: BaseTask,
    private val mTaskDispatcher: TaskDispatcher
) : Runnable {

    override fun run() {
        Process.setThreadPriority(mTask.priority())
        // 等待被唤醒
        mTask.waitToNotify()
        // 执行度具体任务
        mTask.run()
        // 唤醒依赖它的任务
        mTaskDispatcher.notifyDependedTasks(mTask)
    }

}