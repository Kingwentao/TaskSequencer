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
class AppStartTaskFour : ITask, BaseTask() {

    companion object{
        private const val TAG = "AppStartTaskFour"
    }

    override val dependOnTask: List<Class<out ITask>>
        get() = mutableListOf(AppStartTaskOne::class.java)

    override fun run() {
        Log.d(TAG, "AppStartTaskFour run: start")
        Thread.sleep(1000)
        Log.d(TAG, "AppStartTaskFour run: end")
    }

}