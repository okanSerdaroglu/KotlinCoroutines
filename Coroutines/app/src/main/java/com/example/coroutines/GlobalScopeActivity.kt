package com.example.coroutines

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_global_scope.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GlobalScopeActivity : AppCompatActivity() {

    private val tag: String = "AppDebug"
    lateinit var parentJob: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_global_scope)
        main()
        /**
         * if parentJob cancelled all children jobs will be cancelled
         * however if you use GlobalScope.launch parent job don't know anything
         * about child jobs
         */
        button.setOnClickListener {
            parentJob.cancel()
        }
    }

    private suspend fun work(i: Int) {
        delay(3000)
        println("Work $i done. ${Thread.currentThread().name}")
    }

    private fun main() {
        val startTime = System.currentTimeMillis()
        println("Starting parent job...")
        // if you cancel the parent job
        parentJob = CoroutineScope(Main).launch {

            GlobalScope.launch {
                work(1)
            }
            GlobalScope.launch {
                work(2)
            }

        }
        parentJob.invokeOnCompletion { throwable ->
            if (throwable != null) {
                println("Job was cancelled after ${System.currentTimeMillis() - startTime} ms")
            } else {
                println("Done in ${System.currentTimeMillis() - startTime} ms")
            }
        }
    }

    private fun println(message: String) {
        Log.d(tag, message)
    }
}