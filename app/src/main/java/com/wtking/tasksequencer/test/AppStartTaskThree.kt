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
class AppStartTaskThree : BaseTask() {

    companion object {
        private const val TAG = "AppStartTaskThree"
    }

    override fun run() {
        Log.d(TAG, "AppStartTaskThree run: start")
        Thread.sleep(7000)
        Log.d(TAG, "AppStartTaskThree run: end")
    }

}