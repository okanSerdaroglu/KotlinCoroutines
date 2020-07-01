package com.example.coroutines

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO

class TimeoutActivity : AppCompatActivity() {

    private val RESULT_1 = "Result #1"
    private val RESULT_2 = "Result #2"

    private val jobTimeout = 1900L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timeout)

        // IO, Main, Default
        button.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                fakeApiRequest()
            }
        }
    }

    private fun setNewText(input: String) {
        val newText = text.text.toString() + "\n$input"
        text.text = newText
    }

    private suspend fun setTextOnMainThread(input: String) {
        withContext(Dispatchers.Main) {
            setNewText(input)
        }
    }

    /**
     * suspend means that this method can be use in a coroutine
     * network calls room transactions
     */
    private suspend fun getResultOneFromAPI(): String {
        logThread("getResultOneFromAPI")
        delay(1000)  // delay function delays the current coroutine
        return RESULT_1
    }

    private fun logThread(methodName: String) {
        print("debug: $methodName: ${Thread.currentThread().name}")
    }


    private suspend fun getResultTwoFromAPI(): String {
        logThread("getResultTwoFromAPI")
        delay(1000)  // delay function delays the current coroutine
        return RESULT_2
    }

    // this method calls a suspend function. For this reason it also should be suspend
    // You can not set the result of fakeApiRequest in a textView here, because it is
    // in worker thread. Firstly you should pass it into main thread and then set it
    private suspend fun fakeApiRequest() {
        withContext(IO) {
            val job = withTimeoutOrNull(jobTimeout) {

                val result1 = getResultOneFromAPI()
                println("result #1: $result1")
                setTextOnMainThread(result1)

                val result2 = getResultTwoFromAPI()
                setTextOnMainThread(result2)
            }

            if (job == null) {
                val cancelMessage = "Cancelling job... Job took longer than $jobTimeout ms"
                println("debug: $cancelMessage")
                setTextOnMainThread(cancelMessage)
            }
        }
    }
}