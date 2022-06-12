package com.wtking.tasksequencer.test

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.wtking.tasksequencer.dispatcher.TaskDispatcher

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TaskDispatcher.create()
                .addTask(AppStartTaskOne())
                .addTask(AppStartTaskTwo())
                .addTask(AppStartTaskThree())
                .addTask(AppStartTaskFour())
                .start()
            }
    }
}
