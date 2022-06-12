package com.wtking.tasksequencer.dispatcher

import android.util.Log
import com.wtking.tasksequencer.base.BaseTask
import com.wtking.tasksequencer.bean.TaskType
import com.wtking.tasksequencer.executor.TaskExecutor
import com.wtking.tasksequencer.sort.TopologicalSortTaskHelper
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicInteger

/**
 * author: WentaoKing
 * created on: 2022/5/23
 * description: 任务分发器
 */
class TaskDispatcher {

    // 所有任务
    private val mTasks = mutableListOf<BaseTask>()

    // 任务名和任务类的映射关系
    private lateinit var mAllTaskMap: HashMap<Class<out BaseTask>, BaseTask>

    // 任务名和依赖任务名的映射关系
    private lateinit var mDependedTaskMap: HashMap<Class<out BaseTask>, List<Class<out BaseTask>>>

    // 排序助手
    private lateinit var mSortTaskHelper: TopologicalSortTaskHelper

    companion object {
        private const val TAG = "TaskDispatcher"

        fun create(): TaskDispatcher {
            return TaskDispatcher()
        }
    }

    fun addTask(task: BaseTask): TaskDispatcher {
        mTasks.add(task)
        return this
    }

    fun start() {
        // 拿到拓扑排序后的task
        val sortTaskHelper = TopologicalSortTaskHelper()
        val sortedTaskList = sortTaskHelper.getSortedTask(mTasks)
        mSortTaskHelper = sortTaskHelper
        mAllTaskMap = sortTaskHelper.getAllTaskMap()
        mDependedTaskMap = sortTaskHelper.getTasksByNode()
        dispatchTask(sortedTaskList)
    }

    private fun dispatchTask(sortedTaskList: List<BaseTask>) {
        for (task in sortedTaskList) {
            Log.d(TAG, "dispatchTask sort $task")
            val isIOBoundType = task.taskType == TaskType.IO_BOUND
            TaskExecutor.instance.getExecutor(isIOBoundType).execute(TaskRunnable(task, this))
        }
    }

    /**
     * 通知依赖该task的任务已经完成了
     */
    fun notifyDependedTasks(mTask: BaseTask) {
        Log.d(TAG, "notifyDependedTasks: $mTask is finish...")
        val dependTasks = mDependedTaskMap[mTask::class.java]
        if (!dependTasks.isNullOrEmpty()) {
            for (task in dependTasks) {
                mAllTaskMap[task]?.notifyEndWait()
            }
        }
    }

}