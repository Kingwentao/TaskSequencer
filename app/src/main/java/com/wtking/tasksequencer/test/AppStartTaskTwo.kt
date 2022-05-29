package com.wtking.tasksequencer.test

import android.util.Log
import com.wtking.tasksequencer.base.BaseTask
import com.wtking.tasksequencer.base.ITask
import com.wtking.tasksequencer.bean.TaskType

/**
 * author: WentaoKing
 * created on: 2022/5/23
 * description:
 */
class AppStartTaskTwo : ITask, BaseTask() {

    companion object {
        private const val TAG = "AppStartTaskTwo"
    }

    override val dependOnTask: List<Class<out ITask>>
        get() = mutableListOf(AppStartTaskThree::class.java)

    override fun run() {
        Log.d(TAG, "AppStartTaskTwo run: start")
        Thread.sleep(5000)
        Log.d(TAG, "AppStartTaskTwo run: end")
    }

}