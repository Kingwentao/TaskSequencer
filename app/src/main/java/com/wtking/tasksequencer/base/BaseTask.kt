package com.wtking.tasksequencer.base

import com.wtking.tasksequencer.bean.TaskType

/**
 * author: WentaoKing
 * created on: 2022/5/23
 * description: 任务基础代码，一般的任务可以复用此处代码
 */
abstract class BaseTask : ITask {

    override val taskType: TaskType
        get() = TaskType.IO_BOUND

    override val dependOnTask: List<Class<out ITask>>
        get() = mutableListOf()

    override fun priority(): Int {
        return 10
    }

}