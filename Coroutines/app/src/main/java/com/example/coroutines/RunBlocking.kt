package com.example.coroutines

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_run_blocking.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

class RunBlocking : AppCompatActivity() {

    private val TAG: String = "AppDebug"

    var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_run_blocking)

        main()
        button.setOnClickListener {
            text.text = (count++).toString()
        }

    }

    fun main() {

        // job #1
        CoroutineScope(Main).launch {
            printLn("Starting job in thread: ${Thread.currentThread().name}")

            val result1 = getResult()
            printLn("result1: $result1")

            val result2 = getResult()
            printLn("result1: $result2")

            val result3 = getResult()
            printLn("result1: $result3")

            val result4 = getResult()
            printLn("result1: $result4")

            val result5 = getResult()
            printLn("result1: $result5")

        }

        // job #2
        CoroutineScope(Main).launch {
            delay(1000)

            /**
             * runBlocking blocks all thread and do this job
             * firstly. When it finished other jobs complete
             */
            runBlocking {
                printLn("Blocking thread :${Thread.currentThread().name}")
                delay(4000)
                printLn("Blocking thread completed")
            }
        }
    }

    private suspend fun getResult(): Int {
        delay(1000)
        return Random.nextInt(0, 100)
    }

    private fun printLn(message: String) {
        Log.d(TAG, message)
    }

}