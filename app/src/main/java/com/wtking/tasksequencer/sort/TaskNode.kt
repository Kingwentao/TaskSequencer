package com.wtking.tasksequencer.sort

import com.wtking.tasksequencer.base.BaseTask


/**
 * author: created by wentaoKing
 * date: created in 2022/5/25
 * description: 任务节点
 * @property inCount 入度
 * @property outCount 出度
 * @property next 依赖的下一个节点
 */
data class TaskNode(
    val value: Class<out BaseTask>,
    var inCount: Int = 0,
    var outCount: Int = 0,
    var next: MutableList<TaskNode> = mutableListOf()
) {

    override fun equals(other: Any?): Boolean {
        return this.value == (other as TaskNode).value
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}