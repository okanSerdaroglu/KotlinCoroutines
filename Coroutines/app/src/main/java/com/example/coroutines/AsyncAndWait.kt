package com.example.coroutines

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_async_and_wait.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlin.system.measureTimeMillis

class AsyncAndWait : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_async_and_wait)

        button.setOnClickListener {
            setNewText("Click!")
            CoroutineScope(IO).launch {
                fakeApiRequestTwo()
            }
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

    private suspend fun fakeApiRequestOne() {

        val startTime = System.currentTimeMillis()
        val parentJob = CoroutineScope(IO).launch {
            val job1 = launch {
                val time1 = measureTimeMillis {
                    println("debug: launching job1 in thread: ${Thread.currentThread().name}")
                    val result1 = getResult1FromApi()
                    setTextOnMainThread("Got $result1")
                }
                println("debug: complete job1 in $time1 ms.")
            }

            val job2 = launch {
                val time2 = measureTimeMillis {
                    println("debug: launching job1 in thread: ${Thread.currentThread().name}")
                    val result2 = getResult1FromApi()
                    setTextOnMainThread("Got $result2")
                }
                println("debug: complete job2 in $time2 ms.")
            }
        }
        parentJob.invokeOnCompletion {
            println("debug: total elapsed time: ${System.currentTimeMillis() - startTime}")
        }

    }

    /**
     * async() is a blocking call (similar to the job pattern with job.join())
     *  NOTES:
     *  1) IF you don't call await(), it does not wait for the result
     *  2) Calling await() on both these Deffered values will EXECUTE THEM IN PARALLEL. But the RESULTS won't
     *     be published until the last result is available (in this case that's result2)
     *
     */
    private suspend fun fakeApiRequestTwo() {

        CoroutineScope(IO).launch {
            val executionTime = measureTimeMillis {
                val result1: Deferred<String> = async {
                    println("debug: launching job1: ${Thread.currentThread().name}")
                    getResult1FromApi()
                }

                val result2: Deferred<String> = async {
                    println("debug: launching job2: ${Thread.currentThread().name}")
                    getResult1FromApi()
                }
                setTextOnMainThread("Got ${result1.await()}")
                setTextOnMainThread("Got ${result2.await()}")
            }
            println("debug : total time elapsed: $executionTime")
        }
    }


    private suspend fun getResult1FromApi(): String {
        delay(1000)
        return "Result #1"
    }

    private suspend fun getResult2FromApi(): String {
        delay(1700)
        return "Result #2"
    }

}

