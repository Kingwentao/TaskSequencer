package com.wtking.tasksequencer.sort

import android.util.Log
import com.wtking.tasksequencer.base.ITask

/**
 * author: created by wentaoKing
 * date: created in 2022/5/24
 * description: 任务的图结构
 */
class TasksGraph{

    val nodes: HashMap<Class<out ITask>, TaskNode> = HashMap()

    companion object {

        private const val TAG = "TasksGraph"

        fun createTaskGraph(tasks: List<ITask>): TasksGraph {
            val tasksGraph = TasksGraph()
            for (task in tasks) {
                val fromTasks = task.dependOnTask
                if (!tasksGraph.nodes.containsKey(task::class.java)) {
                    tasksGraph.nodes[task::class.java] = TaskNode()
                }
                // 依赖的task
                for (fromTask in fromTasks) {
                    if (!tasksGraph.nodes.containsKey(fromTask)) {
                        tasksGraph.nodes[fromTask] = TaskNode()
                    }
                    val fromTaskNode = tasksGraph.nodes[fromTask] ?: continue
                    val toTaskNode = tasksGraph.nodes[task::class.java] ?: continue
                    toTaskNode.inCount++
                    fromTaskNode.outCount++
                    fromTaskNode.next.add(toTaskNode)
                }
            }
            for (node in tasksGraph.nodes) {
                Log.d(
                    TAG,
                    "createTaskGraph: node key = ${node.key} value: ${node.value} " +
                            "inCount: ${node.value.inCount} " +
                            "outCount: ${node.value.outCount}"
                )
            }
            printTaskGraph(tasksGraph)
            return tasksGraph
        }

        private fun printTaskGraph(tasksGraph: TasksGraph) {
            for (taskNode in tasksGraph.nodes) {
                val nextNodes = taskNode.value.next
                for (nextNode in nextNodes) {
                    val nextNodeClass = getClassByTaskNode(tasksGraph.nodes, nextNode)
                    Log.d(TAG, "printTaskGraph: ${taskNode.key} -> $nextNodeClass")
                }
            }
        }

        private fun getClassByTaskNode(
            nodes: HashMap<Class<out ITask>, TaskNode>,
            taskNode: TaskNode
        ): Class<out ITask>? {
            for (node in nodes) {
                if (node.value == taskNode) {
                    return node.key
                }
            }
            return null
        }

    }
}