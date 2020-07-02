package com.example.coroutines

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_job.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

class JobActivity : AppCompatActivity() {


    private val PROGRESS_MAX = 100
    private val PROGRESS_START = 0
    private val JOB_TIME = 4000

    private lateinit var job: CompletableJob

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job)
        job_button.setOnClickListener {
            if (!::job.isInitialized) { // control if a latenit var is initialized
                initJob()
            }
            job_progress_bar.startJobOrCancel(job)
        }
    }

    private fun ProgressBar.startJobOrCancel(job: Job) {
        if (this.progress > 0) {
            println("$job is already active. Cancelling...")
            resetJob()
        } else {
            job_button.text = ("Cancel Job #1")
            CoroutineScope(IO + job).launch {
                println("coroutine $this is activated with job $job")
                for (i in PROGRESS_START..PROGRESS_MAX) {
                    delay((JOB_TIME / PROGRESS_MAX).toLong())
                    this@startJobOrCancel.progress = i
                }
                updateJobCompleteTextView("Job is complete")
            }
        }
    }

    private fun updateJobCompleteTextView(text: String) {
        GlobalScope.launch(Main) {
            job_complete_text.text = (text)
        }
    }

    private fun resetJob() {
        if (job.isActive || job.isCompleted) {
            job.cancel(CancellationException("Resetting job"))
        }
        initJob()
    }

    private fun initJob() {
        job_button.text = "Start job #1"
        updateJobCompleteTextView("")
        job = Job()
        job.invokeOnCompletion {
            it?.message.let {
                var message = it
                if (message.isNullOrBlank()) {
                    message = "unknown error"
                }
                println("$job was cancelled. Reason : $message")
                showToast(message)
            }
        }
        job_progress_bar.max = PROGRESS_MAX
        job_progress_bar.progress = PROGRESS_START
    }

    private fun showToast(text: String) { // works in main thread
        GlobalScope.launch(Main) {
            Toast.makeText(this@JobActivity, text, Toast.LENGTH_LONG).show()
        }
    }

}

