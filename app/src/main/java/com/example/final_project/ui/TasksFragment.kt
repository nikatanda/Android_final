package com.example.final_project.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.final_project.R
import com.example.final_project.data.SessionManager
import com.example.final_project.data.Task
import com.example.final_project.databinding.FragmentTasksBinding
import com.google.android.material.snackbar.Snackbar

class TasksFragment : Fragment() {

    private var _binding: FragmentTasksBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TaskViewModel by activityViewModels {
        AppViewModelFactory(requireActivity().application)
    }

    private lateinit var taskAdapter: TaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTasksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearchInput()
        CelebrationHelper.animateFabEntrance(binding.addTaskFab)
        binding.addTaskFab.setOnClickListener { showTaskDialog(null) }

        viewModel.activeTasks.observe(viewLifecycleOwner) { tasks ->
            taskAdapter.submitList(tasks)
            val isEmpty = tasks.isEmpty()
            binding.emptyStateLayout.visibility = if (isEmpty) View.VISIBLE else View.GONE
            if (isEmpty) {
                CelebrationHelper.fadeInContent(
                    binding.emptyStateLayout.findViewById(R.id.emptyStateText),
                    binding.emptyStateLayout.findViewById(R.id.emptyStateSubtitle)
                )
            }
        }
    }

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter(
            onTaskClick = { task -> showTaskDialog(task) },
            onTaskChecked = { task -> onTaskChecked(task) }
        )

        binding.tasksRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = taskAdapter
            itemAnimator = DefaultItemAnimator().apply {
                addDuration = 300
                removeDuration = 250
                moveDuration = 250
            }
        }

        val swipeCallback = SwipeToDeleteCallback(requireContext(), taskAdapter) { task ->
            viewModel.deleteTask(task)
            Snackbar.make(binding.root, R.string.task_deleted, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo) { viewModel.restoreTask(task) }
                .show()
        }
        ItemTouchHelper(swipeCallback).attachToRecyclerView(binding.tasksRecyclerView)
    }

    private fun setupSearchInput() {
        InputUtils.enableMultilingualInput(binding.searchInput)
        binding.searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.setSearchQuery(s?.toString().orEmpty())
            }
            override fun afterTextChanged(s: Editable?) = Unit
        })
    }

    private fun onTaskChecked(task: Task) {
        if (!task.isCompleted) {
            binding.root.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
            CelebrationHelper.showTaskCompletedCelebration(
                binding.root,
                CelebrationHelper.randomCompletionMessage(requireContext())
            )
        }
        viewModel.toggleTaskCompletion(task)
    }

    private fun showTaskDialog(task: Task?) {
        val userId = SessionManager(requireContext()).getUserId()
        AddEditTaskDialogFragment.newInstance(task, userId) { savedTask ->
            savedTask ?: return@newInstance
            if (task == null) {
                viewModel.addTask(savedTask.title, savedTask.description, savedTask.priority)
            } else {
                viewModel.updateTask(savedTask)
            }
        }.show(parentFragmentManager, "AddEditTaskDialog")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
