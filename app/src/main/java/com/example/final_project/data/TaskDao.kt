package com.example.final_project.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TaskDao {

    @Query(
        "SELECT * FROM tasks WHERE userId = :userId AND isCompleted = 0 " +
            "ORDER BY priority ASC, createdAt DESC"
    )
    fun getActiveTasks(userId: Long): LiveData<List<Task>>

    @Query(
        "SELECT * FROM tasks WHERE userId = :userId AND isCompleted = 1 " +
            "ORDER BY createdAt DESC"
    )
    fun getCompletedTasks(userId: Long): LiveData<List<Task>>

    @Query(
        "SELECT * FROM tasks WHERE userId = :userId AND isCompleted = 0 AND " +
            "(title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%') " +
            "ORDER BY priority ASC, createdAt DESC"
    )
    fun searchActiveTasks(userId: Long, query: String): LiveData<List<Task>>

    @Insert
    suspend fun insert(task: Task): Long

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)

    @Query("DELETE FROM tasks WHERE userId = :userId AND isCompleted = 1")
    suspend fun deleteCompletedTasks(userId: Long)

    @Query("UPDATE tasks SET isCompleted = :completed WHERE id = :taskId")
    suspend fun setCompleted(taskId: Long, completed: Boolean)
}
