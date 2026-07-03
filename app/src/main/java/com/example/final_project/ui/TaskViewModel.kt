package com.example.final_project.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.final_project.data.Priority
import com.example.final_project.data.SessionManager
import com.example.final_project.data.Task
import com.example.final_project.data.TaskDatabase
import com.example.final_project.data.TaskRepository
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TaskRepository(
        TaskDatabase.getInstance(application).taskDao()
    )
    private val sessionManager = SessionManager(application)

    private val searchQuery = MutableLiveData("")

    val activeTasks: LiveData<List<Task>> = searchQuery.switchMap { query ->
        val userId = sessionManager.getUserId()
        if (query.isNullOrBlank()) {
            repository.getActiveTasks(userId)
        } else {
            repository.searchActiveTasks(userId, query.trim())
        }
    }

    val completedTasks: LiveData<List<Task>> =
        repository.getCompletedTasks(sessionManager.getUserId())

    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun addTask(title: String, description: String, priority: Priority) {
        if (title.isBlank()) return
        val userId = sessionManager.getUserId()
        if (userId == SessionManager.INVALID_USER_ID) return

        viewModelScope.launch {
            repository.insert(
                Task(
                    userId = userId,
                    title = title.trim(),
                    description = description.trim(),
                    priority = priority
                )
            )
        }
    }

    fun restoreTask(task: Task) {
        viewModelScope.launch {
            repository.insert(task.copy(id = 0))
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.update(task)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.delete(task)
        }
    }

    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            repository.setCompleted(task.id, !task.isCompleted)
        }
    }

    fun clearCompletedTasks() {
        viewModelScope.launch {
            repository.deleteCompletedTasks(sessionManager.getUserId())
        }
    }
}
