package com.wtking.tasksequencer.sort

import android.util.Log
import com.wtking.tasksequencer.base.ITask
import java.util.*
import kotlin.collections.HashMap

/**
 * author: WentaoKing
 * created on: 2022/5/23
 * description: 拓扑排序任务
 */
class TopologicalSortTask {

    companion object {
        private const val TAG = "TopologicalSortTask"
    }

    fun getSortedTask(tasks: List<ITask>): List<ITask> {
        val fromTasks = mutableMapOf<Class<out ITask>, ITask>()
        for (task in tasks) {
            fromTasks[task::class.java] = task
        }
        // 构建图结构
        val taskGraph = TasksGraph.createTaskGraph(tasks)
        // 所有节点的入度
        val tasksNodeInCount = HashMap<TaskNode, Int>()
        // 入度为0的队列
        val zeroList: Queue<TaskNode> = LinkedList()
        for (node in taskGraph.nodes.values) {
            if (node.inCount == 0) {
                zeroList.offer(node)
                Log.d(TAG, "getSortedTask: add $node")
            }
            tasksNodeInCount[node] = node.inCount
        }
        val sortedList = arrayListOf<TaskNode>()
        while (zeroList.isNotEmpty()) {
            val node = zeroList.poll() ?: continue
            sortedList.add(node)
            for (nextNode in node.next) {
                val nextNodeInCount = tasksNodeInCount[nextNode] ?: continue
                tasksNodeInCount[nextNode] = nextNodeInCount - 1
                if (tasksNodeInCount[nextNode] == 0) {
                    Log.d(TAG, "getSortedTask: add $nextNode")
                    zeroList.add(nextNode)
                }
            }
        }
        val result = arrayListOf<ITask>()
        for (sortNode in sortedList) {
            Log.d(TAG, "getSortedTask: $sortNode")
            for (task in taskGraph.nodes.entries) {
                if (sortNode == task.value) {
                    Log.d(TAG, "getSortedTask: ${task.key}")
                    result.add(fromTasks.getValue(task.key))
                    continue
                }
            }
        }
        return result
    }

}