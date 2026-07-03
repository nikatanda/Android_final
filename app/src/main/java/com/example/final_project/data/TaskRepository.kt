package com.example.final_project.data

import androidx.lifecycle.LiveData

class TaskRepository(private val taskDao: TaskDao) {

    fun getActiveTasks(userId: Long): LiveData<List<Task>> = taskDao.getActiveTasks(userId)

    fun getCompletedTasks(userId: Long): LiveData<List<Task>> = taskDao.getCompletedTasks(userId)

    fun searchActiveTasks(userId: Long, query: String): LiveData<List<Task>> =
        taskDao.searchActiveTasks(userId, query)

    suspend fun insert(task: Task): Long = taskDao.insert(task)

    suspend fun update(task: Task) = taskDao.update(task)

    suspend fun delete(task: Task) = taskDao.delete(task)

    suspend fun deleteCompletedTasks(userId: Long) = taskDao.deleteCompletedTasks(userId)

    suspend fun setCompleted(taskId: Long, completed: Boolean) =
        taskDao.setCompleted(taskId, completed)
}
