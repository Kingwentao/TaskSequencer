package com.wtking.tasksequencer.base

import android.os.Process.THREAD_PRIORITY_FOREGROUND
import android.os.Process.THREAD_PRIORITY_LOWEST
import androidx.annotation.IntRange
import com.wtking.tasksequencer.bean.TaskType

/**
 * author: WentaoKing
 * created on: 2022/5/23
 * description: 任务接口
 */
interface ITask {

    /**
     * 任务的类型
     */
    val taskType: TaskType

    /**
     * 所依赖的task
     */
    val dependOnTaskList: List<Class<out ITask>>

    /**
     * 任务所在线程的优先级
     */
    @IntRange(from = THREAD_PRIORITY_FOREGROUND.toLong(), to = THREAD_PRIORITY_LOWEST.toLong())
    fun priority(): Int

    /**
     * 执行的任务内容
     */
    fun run()

}