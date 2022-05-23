package com.wtking.tasksequencer.base

import com.wtking.tasksequencer.bean.TaskType

/**
 * author: WentaoKing
 * created on: 2022/5/23
 * description: 任务接口
 */
interface ITask {

    fun taskType(): TaskType

    /**
     * 任务优先级
     */
    fun priority(): Int

    /**
     * 执行任务
     */
    fun run()

}