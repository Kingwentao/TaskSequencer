package com.wtking.tasksequencer.dispatcher

import com.wtking.tasksequencer.base.ITask

/**
 * author: WentaoKing
 * created on: 2022/5/23
 * description: 对task封装的Runnable
 */
class TaskRunnable(private val mTask: ITask): Runnable {

    override fun run() {
        mTask.run()
    }

}