package com.wtking.tasksequencer.test

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.wtking.tasksequencer.dispatcher.TaskDispatcher

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val mTaskDispatcher = TaskDispatcher.create()
            mTaskDispatcher.addTask(AppStartTaskOne())
            mTaskDispatcher.start()
        }
    }
}
