package com.wtking.tasksequencer.sort

import android.util.Log
import com.wtking.tasksequencer.base.BaseTask
import java.util.*
import kotlin.collections.HashMap

/**
 * author: WentaoKing
 * created on: 2022/5/23
 * description: 任务拓扑排序助手
 */
class TopologicalSortTaskHelper {

    companion object {
        private const val TAG = "TopologicalSortTask"
    }

    private var mSortedAllTaskMap = HashMap<Class<out BaseTask>, BaseTask>()
    private val mDependedTaskNodeMap = HashMap<TaskNode, List<TaskNode>>()

    fun getSortedTask(tasks: List<BaseTask>): List<BaseTask> {
        val fromTasks = mutableMapOf<Class<out BaseTask>, BaseTask>()
        for (task in tasks) {
            fromTasks[task::class.java] = task
        }
        // 构建图结构
        val taskGraph = TasksGraph.createTaskGraph(tasks)
        // 所有节点的入度
        val tasksNodeInCount = HashMap<TaskNode, Int>()
        // 入度为0的队列
        val zeroList: Queue<TaskNode> = LinkedList()
        // 遍历所有的task，找到入度为0的
        for (node in taskGraph.nodes.values) {
            if (node.inCount == 0) {
                zeroList.offer(node)
                Log.d(TAG, "getSortedTask: add $node")
            }
            tasksNodeInCount[node] = node.inCount
        }
        // 按照入度为0的顺序，进行排序
        val sortedList = arrayListOf<TaskNode>()
        while (zeroList.isNotEmpty()) {
            val node = zeroList.poll() ?: continue
            Log.d(TAG, "getSortedTask: zero node: ${node.value}")
            mDependedTaskNodeMap[node] = node.next
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
        val result = arrayListOf<BaseTask>()
        // TODO： 这部分过于复杂，待优化
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
        mSortedAllTaskMap = convertToTaskMap(result)
        return result
    }

    fun getAllTaskMap(): HashMap<Class<out BaseTask>, BaseTask> {
        return mSortedAllTaskMap
    }

    fun getTasksByNode(): HashMap<Class<out BaseTask>, List<Class<out BaseTask>>> {
        val dependedTaskMap = HashMap<Class<out BaseTask>, List<Class<out BaseTask>>>()
        for (taskNodeMap in mDependedTaskNodeMap.entries) {
            val taskNode = taskNodeMap.key
            Log.d(TAG, "getTasksByNode: taskNode -- ${taskNode.value}")
            val dependTaskNodes = taskNodeMap.value
            val dependTaskClass = arrayListOf<Class<out BaseTask>>()
            for (dependTask in dependTaskNodes) {
                dependTaskClass.add(dependTask.value)
                Log.d(TAG, "getTasksByNode: dependTask: ${dependTask.value}")
            }
            dependedTaskMap[taskNode.value] = dependTaskClass
        }
        return dependedTaskMap
    }

    private fun convertToTaskMap(tasks: List<BaseTask>): HashMap<Class<out BaseTask>, BaseTask> {
        val classAndTaskMap = HashMap<Class<out BaseTask>, BaseTask>()
        for (task in tasks) {
            classAndTaskMap[task::class.java] = task
        }
        return classAndTaskMap
    }

}