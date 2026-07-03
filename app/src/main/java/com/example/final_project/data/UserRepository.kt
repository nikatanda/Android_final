package com.example.final_project.data

class UserRepository(private val userDao: UserDao) {

    suspend fun login(username: String, password: String): User? =
        userDao.loginUser(username.trim(), password)

    suspend fun register(username: String, password: String): Result<User> {
        val trimmedUsername = username.trim()
        if (trimmedUsername.isBlank() || password.isBlank()) {
            return Result.failure(IllegalArgumentException("empty_fields"))
        }
        if (userDao.findByUsername(trimmedUsername) != null) {
            return Result.failure(IllegalStateException("user_exists"))
        }
        val userId = userDao.registerUser(User(username = trimmedUsername, password = password))
        return Result.success(User(id = userId, username = trimmedUsername, password = password))
    }
}
