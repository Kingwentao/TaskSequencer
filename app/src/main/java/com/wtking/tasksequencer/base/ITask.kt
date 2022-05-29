package com.wtking.tasksequencer.base

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
     * 依赖的task
     */
    val dependOnTask: List<Class<out ITask>>

    /**
     * 任务优先级
     */
    fun priority(): Int

    /**
     * 执行的任务
     */
    fun run()

}