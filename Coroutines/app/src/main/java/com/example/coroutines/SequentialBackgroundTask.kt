package com.example.coroutines

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_sequential_background_task.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlin.system.measureTimeMillis

class SequentialBackgroundTask : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sequential_background_task)

        button.setOnClickListener {
            setNewText("Click!")
            CoroutineScope(IO).launch {
                fakeApiRequest()
                println(message = "debug: CoroutineScope")
            }
        }

    }

    private fun fakeApiRequest() {
        CoroutineScope(IO).launch {
            val executionTime = measureTimeMillis {
                val result1 = async {
                    println("debug : launching job1 ${Thread.currentThread().name}")
                    getResult1FromApi()
                }.await()

                val result2 = async {
                    println("debug : launching job1 ${Thread.currentThread().name}")
                    try {
                        getResult2FromApi(result1)
                    } catch (e: CancellationException) {
                        e.message
                    }
                }.await()
                println("Debug: got result2: $result2")
            }
            println("Debug : total elapsed time: $executionTime ms")
        }
    }

    private fun setNewText(input: String) {
        val newText = text.text.toString() + "\n$input"
        text.text = newText
    }

    private suspend fun setTextOnMainThread(input: String) {
        withContext(Main) {
            setNewText(input)
        }
    }

    private suspend fun getResult1FromApi(): String {
        delay(1000) // Does not block thread. Just suspends the coroutine inside the thread
        return "Result #1"
    }

    private suspend fun getResult2FromApi(result1: String): String {
        delay(1700)
        if (result1 == "Result #1") {
            return "Result #2"
        }
        throw CancellationException("Result #1 was incorrect...")
    }

}

