package com.example.coroutines

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private val RESULT_1 = "Result #1"
    private val RESULT_2 = "Result #2"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // IO, Main, Default
        button.setOnClickListener {
            CoroutineScope(IO).launch {
                fakeApiRequest()
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
        val resultOne = getResultOneFromAPI()
        setTextOnMainThread(resultOne)

        val resultTwo = getResultTwoFromAPI()
        setTextOnMainThread(resultTwo)

        //text.text = resultOne
    }

}