在做App的启动优化时，经常做的一个优化操作就是把某个初始化任务放到子线程去执行，可以减少在主线程去执行造成的耗时，从而提升APP启动的速度。但也正是将任务放到了子线程，所以场景就变成异步了。我们希望任务执行的更快就会使用启动多个线程，在多任务多线程下，就会变得更加复杂。比如，一个常见的场景是，任务1依赖任务2的完成后才能执行，任务2依赖任务3的完成后才能执行，这就要考虑异步场景下的任务排序，只有保证了顺序的正确性，程序才能正常启动。

当然可以在启动的`Application/Activity`启动周期中，通过手段控制初始化代码的顺序，但是任务很多，依赖关系复杂的情况下，管理起来就会非常困难，代码的可读性也会变得非常差，于是一套能够帮助我们，根据依赖关系排序执行任务，简化代码的任务排序器就非常重要了。

设计这样一个任务排序器至少需要考虑以下几点：

**1.多线程管理**

**2.任务排序算法**

**3.使用起来简单**

---

### 核心思路实现

#### 任务的排序

任务的排序，针对这种依赖性的场景，已经有了一种常用的排序算法，也就是拓扑排序。

>**拓扑排序（Topological Sorting**）是一个有向无环图（DAG, Directed Acyclic Graph）的所有顶点的线性序列。
>
>note: 有向无环图（DAG）才有拓扑排序，非DAG图没有拓扑排序一说
>
>该序列必须满足下面两个条件：
>
>1. 每个顶点出现且只出现一次。
>2. 若存在一条从顶点 A 到顶点 B 的路径，那么在序列中顶点 A 出现在顶点 B 的前面。

**拓扑排序通常用来“排序”具有依赖关系的任务**。比如`gradle`中依赖的关系也是通过拓扑排序算法去做的。

拓扑排序算法的思想比较简单：**关键维护一个入度为0的顶点的集合**。入度为0就表示当前的任务没有依赖，可以执行。不断移除当前为入度为0的，更新其他任务的入度，找到下一个入度为0的。

想熟悉该算法的可以参考这篇博客：[拓扑排序（Topological Sorting）](https://blog.csdn.net/lisonglisonglisong/article/details/45543451)

比如这样一个依赖的任务图：`Task4` 依赖` Task1`，` Task1` 依赖 `Task2` `，` Task2` 依赖` Task3`

![任务排序图.png](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/7bf5dd22ab73497688ec04fd16c12d02~tplv-k3u1fbpfcp-watermark.image?)

构建完图，只有`Task3`的入度为0，添加到入度0度集合。更新依赖它任务的入度，也就是Task2，入度（1-1）变成 0， 所以添加到入度0的集合中，等待执行，依此内推，最终的执行顺序就是添加到集合的顺序：Task4- > Task1 -> Task2 -> Task3

#### 多线程的管理

任务是需要放到线程中去执行的，多任务的情况下，就需要考虑到多线程的线程管理。很容易想到使用线程池去做。

可温习下线程池的使用： [固基篇｜重要的线程池知识](https://juejin.cn/post/6987919976820637703)

主要考量点是线程池的配置，使用多少个核心线程、普通线程数，才能发挥设备最大的性能。

区分任务的类型

##### 计算密集型和I/O密集型

**1.计算密集型任务（CPU-bound）**

它的特点是要进行大量的计算，消耗CPU资源，比如计算圆周率、对视频进行高清解码等等，全靠CPU的运算能力。
这种计算密集型任务虽然也可以用多任务完成，但是**任务越多，花在任务切换的时间就越多，CPU执行任务的效率就越低**。
所以，**要最高效地利用CPU，计算密集型任务同时进行的数量应当等于CPU的核心数。**

**2.I/O密集型（I/O bound）**

常见的大部分任务都是`I/O`密集型任务。涉及到网络、磁盘`I/O`的任务都是`I/O`密集型任务，这类任务的特点是`CPU`消耗很少，任务的大部分时间都在等待`I/O`操作完成（因为`I/O`的速度远远低于CPU和内存的速度）。**对于`I/O`密集型任务，任务越多，CPU效率越高**，但也有一个限度。

##### 配置合适的线程池

根据不同任务类型，需要针对性配置不同的线程池：

比如计算密集型任务，为了充分发挥CPU性能，处理任务的最大线程数等于核心线程数，核心线程池数需要和当前设备CPU的个数关联：

```kotlin
// cpu个数
private val CPU_COUNT = Runtime.getRuntime().availableProcessors()
// 核心线程数
private val CORE_POOL_SIZE =
    2.coerceAtLeast((CPU_COUNT - 1).coerceAtMost(5))
// 最大线程池大小
private val MAXIMUM_POOL_SIZE = CORE_POOL_SIZE

private const val KEEP_ALIVE_MILLISECONDS = 1000L 

private val mCPUThreadPoolExecutor by lazy(LazyThreadSafetyMode.NONE) {
        ThreadPoolExecutor(
            CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_MILLISECONDS, TimeUnit.MILLISECONDS,
            LinkedBlockingQueue(),
            Executors.defaultThreadFactory()
        )
    }
```

对于`I/O密集型`任务，大多数时间都处于等待`I/O`操作状态，所以在一定限度下，执行的任务的线程数越多，CPU的利用率越高，因此这里的配置就可以使用`newCachedThreadPool`：

```kotlin
// IO密集型任务的线程池
private val mIOThreadPoolExecutor by lazy(LazyThreadSafetyMode.NONE){
    Executors.newCachedThreadPool(Executors.defaultThreadFactory())
}
```

##### 控制任务执行时机

根据依赖关系，通过拓扑排序后，就有了一个排好序的待执行任务列表了。

```kotlin
fun getSortedTask(tasks: List<BaseTask>): List<BaseTask> {
   ...
   ...
}
```

接下来的关键就是控制任务执行，在一个任务执行完后，才能执行下一个，由于这些任务是在不同的线程下，所以需要考虑多线程并发下的线程顺序控制。

可以选择使用`CountDownLatch`：

**`CountDownLatch`能够使一个线程在等待另外一些线程完成各自工作之后，再继续执行。**它相当于是一个计数器，这个计数器的初始值就是线程的数量，每当一个任务完成后，计数器的值就会减一，当计数器的值为 0 时，表示所有的线程都已经任务了，然后在 `CountDownLatch` 上等待的线程就可以恢复执行接下来的任务。

**1.计数器的初始值设定为依赖的任务个数**

```kotlin
val mDependCountDownLatch = CountDownLatch(dependOnTaskList.size)
```

**2.线程进入等待**

当运行到某个任务时，可以调用waitToNotify，先将该任务置为等待状态

```kotlin
fun waitToNotify() {
    try {
        mDependCountDownLatch.await()
    } catch (e: InterruptedException) {
        e.printStackTrace()
    }
}
```

**3.唤醒等待的线程**

```kotlin
fun notifyEndWait() {
    mDependCountDownLatch.countDown()
}
```

当计数器变为 0 时，在 `CountDownLatch` 上 `await` 的线程就会被唤醒，继续执行线程上的任务

**4.封装上述的操作**

由于上述的操作是固定的，每个任务是不需要关心具体的细节，对该部分封装一个`TaskRunnable`，在执行每个任务前进入等待状态，唤醒后才执行具体的任务内容，执行完后唤醒依赖它的其他任务，将它们的`CountDownLatch`进行`countDown`：

```kotlin
class TaskRunnable(
    private val mTask: BaseTask,
    private val mTaskDispatcher: TaskDispatcher
) : Runnable {
    override fun run() {
        Process.setThreadPriority(mTask.priority())
        // 等待被唤醒
        mTask.waitToNotify()
        // 执行度具体任务
        mTask.run()
        // 唤醒依赖它的任务
        mTaskDispatcher.notifyDependedTasks(mTask)
    }
}
```

#### 调用起来简单

为了让调用起来更简单，更舒服，添加任务时可以做成链式调用的代码风格，类似这样：

```kotlin
TaskDispatcher.create()
    .addTask(AppStartTaskOne())
    .addTask(AppStartTaskTwo())
    .addTask(AppStartTaskThree())
    .addTask(AppStartTaskFour())
    .start()
}
```

我们的初始化任务就可以直接继承`BaseTask`，具体的任务内容就放到`run`函数中，如果需要等待其他的task的完成，就重写`dependOnTaskList`变量，添加到集合中

```kotlin
class AppStartTaskOne : BaseTask() {

    companion object {
        private const val TAG = "AppStartTaskOne"
    }

    override val dependOnTaskList: List<Class<out BaseTask>>
        get() = mutableListOf(AppStartTaskTwo::class.java)

    override fun run() {
        Log.d(TAG, "AppStartTaskOne run: start")
        Thread.sleep(3000)
        Log.d(TAG, "AppStartTaskOne run: end")
    }

}

class AppStartTaskTwo : BaseTask() {

    companion object {
        private const val TAG = "AppStartTaskTwo"
    }

    override val dependOnTaskList: List<Class<out BaseTask>>
        get() = mutableListOf(AppStartTaskThree::class.java)

    override fun run() {
        Log.d(TAG, "AppStartTaskTwo run: start")
        Thread.sleep(5000)
        Log.d(TAG, "AppStartTaskTwo run: end")
    }

}

...
```

最终打印的结果和我们期望的顺序一致：

```
拓扑排序的结果：
D/TaskDispatcher: dispatchTask sort com.wtking.tasksequencer.test.AppStartTaskThree@9ef5af2
D/TaskDispatcher: dispatchTask sort com.wtking.tasksequencer.test.AppStartTaskTwo@4e1f7c0
D/TaskDispatcher: dispatchTask sort com.wtking.tasksequencer.test.AppStartTaskOne@b14343e
D/TaskDispatcher: dispatchTask sort com.wtking.tasksequencer.test.AppStartTaskFour@1cecfec
任务执行完成的结果：
D/TaskDispatcher: notifyDependedTasks: com.wtking.tasksequencer.test.AppStartTaskThree@9ef5af2 is finish...
D/TaskDispatcher: notifyDependedTasks: com.wtking.tasksequencer.test.AppStartTaskTwo@4e1f7c0 is finish...
D/TaskDispatcher: notifyDependedTasks: com.wtking.tasksequencer.test.AppStartTaskOne@b14343e is finish...
D/TaskDispatcher: notifyDependedTasks: com.wtking.tasksequencer.test.AppStartTaskFour@1cecfec is finish...
```

### 设计思路

为了后续的可扩展性，还是需要稍微设计一下，以下是简易版的类图结构：

![任务排序器设计类图.png](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/48063e69f7c44ec7aeaaa438f1775b9b~tplv-k3u1fbpfcp-watermark.image?)

- ITask: 最基础的任务接口，比如`run()`，表示任务的具体内容
- BaseTask: 任务的基类，继承`ITask`，提供了一套基础实现。一般的任务只需要继承这个基类即可
- TaskDispatcher：任务分发器，这个类是核心，是控制所有`Task`执行的地方
- TaskExecutor：任务线程池，为任务分配线程，可根据任务的类型，获取合适的线程池。
- TopologicalSortTaskHelper：拓扑排序助手，对任务拓扑排序，可获取排序后的任务列表

### 最后

这算是实现一个基础版的任务排序器，如果要深究细节，还是会有一些点没有考虑到。

不过本文的重点在于背后的知识的运用实践。比如在该场景下，拓扑排序的运用，线程池的选择、线程并发的顺序控制，还有考虑整体的结构设计，让其具有比较好的扩展性。
