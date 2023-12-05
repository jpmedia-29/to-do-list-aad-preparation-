package com.dicoding.todoapp.notification

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.dicoding.todoapp.R
import com.dicoding.todoapp.data.Task
import com.dicoding.todoapp.data.TaskRepository
import com.dicoding.todoapp.ui.detail.DetailTaskActivity
import com.dicoding.todoapp.utils.DateConverter
import com.dicoding.todoapp.utils.NOTIFICATION_CHANNEL_ID
import com.dicoding.todoapp.utils.TASK_ID

class NotificationWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    private val channelName = inputData.getString(NOTIFICATION_CHANNEL_ID)

    private fun getPendingIntent(task: Task): PendingIntent? {
        val intent = Intent(applicationContext, DetailTaskActivity::class.java).apply {
            putExtra(TASK_ID, task.id)
        }
        return TaskStackBuilder.create(applicationContext).run {
            addNextIntentWithParentStack(intent)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            } else {
                getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
            }
        }
    }

    override fun doWork(): Result {
        //TODO 14 : If notification preference on, get nearest active task from repository and show notification with pending intent
        val showNotification = inputData.getBoolean("showNotification", true)
        if (showNotification) {
            val taskRepository = TaskRepository.getInstance(applicationContext)
            val nearestActiveTask = taskRepository.getNearestActiveTask()
            showNotification(applicationContext, nearestActiveTask)
        }
        return Result.success()
    }


    @SuppressLint("ServiceCast")
    fun showNotification(context: Context, task: Task) {
        val idNotif = 100
        val channelId = "channel_id"
        val notifManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val messageNotif = context.resources.getString(R.string.notify_content, DateConverter.convertMillisToString(task.dueDateMillis))

        val build = NotificationCompat.Builder(context, channelId).apply {
            setSmallIcon(R.drawable.notif_active)
            setContentTitle(task.title)
            setContentText(messageNotif)
            setAutoCancel(true)
            setPriority(NotificationCompat.PRIORITY_DEFAULT)
            setContentIntent(getPendingIntent(task))
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                context.getString(R.string.notify_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            build.setChannelId(channelId)
            notifManager.createNotificationChannel(channel)
        }

        val notification = build.build()
        notifManager.notify(idNotif, notification)
    }


}
