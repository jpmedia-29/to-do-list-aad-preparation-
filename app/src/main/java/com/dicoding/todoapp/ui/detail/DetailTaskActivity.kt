package com.dicoding.todoapp.ui.detail

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dicoding.todoapp.R
import com.dicoding.todoapp.data.Task
import com.dicoding.todoapp.ui.ViewModelFactory
import com.dicoding.todoapp.utils.DateConverter
import com.dicoding.todoapp.utils.TASK_ID

class DetailTaskActivity : AppCompatActivity() {
    private lateinit var detailTaskViewModel: DetailTaskViewModel
    private lateinit var titleTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var dueDateTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_detail)

        //TODO 11 : Show detail task and implement delete action
        initializeViews()
        val taskId = intent.getIntExtra(TASK_ID, 0)
        deleteTask()
        val factory = ViewModelFactory.getInstance(this)
        detailTaskViewModel = ViewModelProvider(this, factory)[DetailTaskViewModel::class.java]
        detailTaskViewModel.setTaskId(taskId)
        detailTaskViewModel.task.observe(this) { task ->
            if (task != null) {
                updateUI(task)
            } else {

            }
        }
    }

    private fun initializeViews() {
        titleTextView = findViewById(R.id.detail_ed_title)
        descriptionTextView = findViewById(R.id.detail_ed_description)
        dueDateTextView = findViewById(R.id.detail_ed_due_date)
    }

    private fun deleteTask(){
        findViewById<Button>(R.id.btn_delete_task).setOnClickListener {
            if (::detailTaskViewModel.isInitialized) {
                detailTaskViewModel.deleteTask()
                finish()
            }
        }
    }

    private fun updateUI(task: Task) {
        titleTextView.text = task.title
        descriptionTextView.text = task.description
        dueDateTextView.text = DateConverter.convertMillisToString(task.dueDateMillis)
    }
}
