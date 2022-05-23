package com.wtking.tasksequencer.executor

import java.util.concurrent.*

/**
 * author: WentaoKing
 * created on: 2022/5/23
 * description: 任务处理线程池
 */
class TaskExecutor private constructor() {

    companion object {
        private const val TAG = "TaskExecutor"

        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            TaskExecutor()
        }

        // cpu个数
        private val CPU_COUNT = Runtime.getRuntime().availableProcessors()

        // 核心线程池大小
        private val CORE_POOL_SIZE =
            2.coerceAtLeast((CPU_COUNT - 1).coerceAtMost(5))

        // 最大线程池大小
        private val MAXIMUM_POOL_SIZE = CORE_POOL_SIZE

        private const val KEEP_ALIVE_MILLISECONDS = 1000L
    }

    // CPU密集型任务的线程池
    private val mCPUThreadPoolExecutor by lazy(LazyThreadSafetyMode.NONE) {
        ThreadPoolExecutor(
            CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_MILLISECONDS, TimeUnit.MILLISECONDS,
            LinkedBlockingQueue(),
            Executors.defaultThreadFactory()
        )
    }

    // IO密集型任务的线程池
    private val mIOThreadPoolExecutor by lazy(LazyThreadSafetyMode.NONE){
        Executors.newCachedThreadPool(Executors.defaultThreadFactory())
    }

    /**
     * 根据task的密集类型选择合适的线程池
     * @param isIOBoundType 是否是io密集型
     */
    fun getExecutor(isIOBoundType: Boolean): ExecutorService {
        return if (isIOBoundType) {
            mIOThreadPoolExecutor
        } else {
            mCPUThreadPoolExecutor
        }
    }

}