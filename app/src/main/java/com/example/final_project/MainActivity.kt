package com.example.final_project

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.final_project.databinding.ActivityMainBinding
import com.example.final_project.ui.AddEditTaskDialogFragment
import com.example.final_project.ui.AppViewModelFactory
import com.example.final_project.ui.AuthViewModel
import com.example.final_project.ui.TaskViewModel
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val taskViewModel: TaskViewModel by viewModels {
        AppViewModelFactory(application)
    }
    private val authViewModel: AuthViewModel by viewModels {
        AppViewModelFactory(application)
    }

    private var showTaskMenu = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(binding.toolbar)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        if (authViewModel.isLoggedIn()) {
            val graph = navController.navInflater.inflate(R.navigation.nav_graph)
            graph.setStartDestination(R.id.tasksFragment)
            navController.graph = graph
        }

        binding.bottomNavigation.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.loginFragment, R.id.signUpFragment -> {
                    binding.bottomNavigation.visibility = View.GONE
                    binding.appBarLayout.visibility = View.GONE
                    showTaskMenu = false
                }
                R.id.tasksFragment -> {
                    binding.bottomNavigation.visibility = View.VISIBLE
                    binding.appBarLayout.visibility = View.VISIBLE
                    supportActionBar?.title = getString(R.string.nav_tasks)
                    showTaskMenu = true
                }
                R.id.completedTasksFragment -> {
                    binding.bottomNavigation.visibility = View.VISIBLE
                    binding.appBarLayout.visibility = View.VISIBLE
                    supportActionBar?.title = getString(R.string.nav_completed)
                    showTaskMenu = false
                }
                R.id.settingsFragment -> {
                    binding.bottomNavigation.visibility = View.VISIBLE
                    binding.appBarLayout.visibility = View.VISIBLE
                    supportActionBar?.title = getString(R.string.settings)
                    showTaskMenu = false
                }
            }
            invalidateOptionsMenu()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (!showTaskMenu) return false

        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add -> {
                showAddTaskDialog()
                true
            }
            R.id.action_clear_completed -> {
                taskViewModel.clearCompletedTasks()
                Snackbar.make(binding.main, R.string.completed_cleared, Snackbar.LENGTH_SHORT).show()
                true
            }
            R.id.action_about -> {
                Snackbar.make(binding.main, R.string.about_message, Snackbar.LENGTH_LONG).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showAddTaskDialog() {
        AddEditTaskDialogFragment.newInstance(
            userId = com.example.final_project.data.SessionManager(this).getUserId()
        ) { savedTask ->
            savedTask ?: return@newInstance
            taskViewModel.addTask(savedTask.title, savedTask.description, savedTask.priority)
        }.show(supportFragmentManager, "AddEditTaskDialog")
    }
}
