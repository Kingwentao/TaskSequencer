package com.wtking.tasksequencer.sort

import com.wtking.tasksequencer.base.ITask

/**
 * author: created by wentaoKing
 * date: created in 2022/5/24
 * description: 任务的图结构
 */
class TasksGraph() {

    val nodes: HashMap<Int, ITask> = HashMap()
    val edges: HashSet<TaskEdge> = HashSet()

    companion object {
        fun createTaskGraph(tasks: List<ITask>, formTasks: List<ITask>): TasksGraph {
            val tasksGraph = TasksGraph()
            for (task in tasks) {

            }
            return tasksGraph
        }
    }
}