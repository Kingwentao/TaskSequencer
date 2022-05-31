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
class AppStartTaskOne : BaseTask() {

    companion object {
        private const val TAG = "AppStartTaskOne"
    }

    override val dependOnTaskList: List<Class<out BaseTask>>
        get() = mutableListOf(AppStartTaskTwo::class.java)

    override fun run() {
        Log.d(TAG, "AppStartTaskOne run: start")
        Thread.sleep(3000)
        Log.d(TAG, "AppStartTaskOne run: end")
    }

}