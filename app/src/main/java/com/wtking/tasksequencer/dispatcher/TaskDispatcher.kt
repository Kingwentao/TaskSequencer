package com.wtking.tasksequencer.dispatcher

import android.util.Log
import com.wtking.tasksequencer.base.ITask
import com.wtking.tasksequencer.bean.TaskType
import com.wtking.tasksequencer.executor.TaskExecutor
import com.wtking.tasksequencer.sort.TopologicalSortTask

/**
 * author: WentaoKing
 * created on: 2022/5/23
 * description: 任务分发器
 */
class TaskDispatcher {

    private val mTasks = mutableListOf<ITask>()

    companion object {
        private const val TAG = "TaskDispatcher"

        fun create(): TaskDispatcher {
            return TaskDispatcher()
        }
    }

    fun addTask(task: ITask) {
        mTasks.add(task)
    }

    fun start() {
        // 只能在主线程执行？
        dispatchTask()
    }

    private fun dispatchTask() {
        val sortTask = TopologicalSortTask().getSortedTask(mTasks)
        for (task in sortTask) {
            Log.d(TAG, "dispatchTask sort $task")
        }
        for (task in mTasks) {
            val isIOBoundType = task.taskType == TaskType.IO_BOUND
            TaskExecutor.instance.getExecutor(isIOBoundType).execute(TaskRunnable(task))
        }
    }

}