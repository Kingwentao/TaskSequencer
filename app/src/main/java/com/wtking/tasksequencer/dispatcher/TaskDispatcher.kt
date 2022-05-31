package com.wtking.tasksequencer.dispatcher

import android.util.Log
import com.wtking.tasksequencer.base.BaseTask
import com.wtking.tasksequencer.bean.TaskType
import com.wtking.tasksequencer.executor.TaskExecutor
import com.wtking.tasksequencer.sort.TopologicalSortTask
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicInteger

/**
 * author: WentaoKing
 * created on: 2022/5/23
 * description: 任务分发器
 */
class TaskDispatcher {

    private lateinit var mSortTask: TopologicalSortTask
    private val mTasks = mutableListOf<BaseTask>()

    private val mNeedWaitTaskCount = AtomicInteger()
    private lateinit var mCountDownLatch: CountDownLatch

    // 所有任务
    private lateinit var mAllTaskMap: HashMap<Class<out BaseTask>, BaseTask>

    // 依赖key的所有task
    private lateinit var mDependedTaskMap: HashMap<Class<out BaseTask>, List<Class<out BaseTask>>>

    companion object {
        private const val TAG = "TaskDispatcher"

        fun create(): TaskDispatcher {
            return TaskDispatcher()
        }
    }

    fun addTask(task: BaseTask): TaskDispatcher {
        mTasks.add(task)
        if (task.needWait()) {
            mNeedWaitTaskCount.getAndIncrement()
        }
        return this
    }

    fun start() {
        // 拿到拓扑排序后的task
        val sortTask = TopologicalSortTask()
        val sortedTaskList = sortTask.getSortedTask(mTasks)
        mSortTask = sortTask
        mAllTaskMap = sortTask.getAllTaskMap()
        mDependedTaskMap = sortTask.getTasksByNode()
        mCountDownLatch = CountDownLatch(mNeedWaitTaskCount.get())
        // 只能在主线程执行？
        dispatchTask(sortedTaskList)
    }

    private fun dispatchTask(sortedTaskList: List<BaseTask>) {
        for (task in sortedTaskList) {
            Log.d(TAG, "dispatchTask sort $task")
        }
        for (task in sortedTaskList) {
            val isIOBoundType = task.taskType == TaskType.IO_BOUND
            TaskExecutor.instance.getExecutor(isIOBoundType).execute(TaskRunnable(task, this))
        }
    }

    /**
     * 通知依赖该task的任务已经完成了
     */
    fun notifyDependedTasks(mTask: BaseTask) {
        Log.d(TAG, "notifyDependedTasks: $mTask is finish...")
        if (mTask.needWait()) {
            Log.d(TAG, "notifyDependedTasks: $mTask is need wait")
            mCountDownLatch.countDown()
            mNeedWaitTaskCount.getAndDecrement()
        }
        val dependTasks = mDependedTaskMap[mTask::class.java]
        if (!dependTasks.isNullOrEmpty()) {
            for (task in dependTasks) {
                mAllTaskMap[task]?.notifyWait()
            }
        }
    }

}