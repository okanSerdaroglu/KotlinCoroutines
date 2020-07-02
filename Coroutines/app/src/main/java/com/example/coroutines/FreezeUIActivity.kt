package com.example.coroutines

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_freeze_ui.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FreezeUIActivity : AppCompatActivity() {

    private val TAG: String = "AppDebug"

    var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_freeze_ui)
        main()
        button.setOnClickListener {
            text.text = (count++).toString()
        }
    }

    /**
     * freeze UI
     */
    fun main2() {
        CoroutineScope(Main).launch {
            println("Current thread : ${Thread.currentThread().name}")
            for (i in 1..100000){
                launch {
                    doNetworkRequest()
                }
            }
        }
    }


    private suspend fun doNetworkRequest() {
        println("Starting network request...")
        delay(3000)
        println("Finished network request")
    }

    private fun println(message: String) {
        Log.d(TAG, message)
    }

    /** coroutine delays however all current thread not freeze
     *  Because coroutine is just a job working on a thread
     */
    private fun main() {
        CoroutineScope(Main).launch {
            println("Current thread: ${Thread.currentThread().name}")
            delay(3000)
        }
    }
}