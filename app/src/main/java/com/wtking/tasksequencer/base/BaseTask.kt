package com.wtking.tasksequencer.base

import com.wtking.tasksequencer.bean.TaskType

/**
 * author: WentaoKing
 * created on: 2022/5/23
 * description: 任务基础代码，一般的任务可以复用此处代码
 */
abstract class BaseTask: ITask {

    override fun priority(): Int {
        return 10
    }

    override fun taskType(): TaskType {
        return TaskType.IO_BOUND
    }

}