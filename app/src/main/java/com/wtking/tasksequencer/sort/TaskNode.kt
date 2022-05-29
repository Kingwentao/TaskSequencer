package com.wtking.tasksequencer.sort


/**
 * author: created by wentaoKing
 * date: created in 2022/5/25
 * description: 任务节点
 * @property inCount 入度
 * @property outCount 出度
 * @property next 依赖的下一个节点
 */
class TaskNode {
    var inCount: Int = 0
    var outCount: Int = 0
    var next: MutableList<TaskNode> = mutableListOf()
}