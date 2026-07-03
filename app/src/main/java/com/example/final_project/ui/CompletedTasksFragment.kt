package com.example.final_project.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.final_project.R
import com.example.final_project.data.Task
import com.example.final_project.databinding.FragmentCompletedTasksBinding
import com.google.android.material.snackbar.Snackbar

class CompletedTasksFragment : Fragment() {

    private var _binding: FragmentCompletedTasksBinding? = null
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
        _binding = FragmentCompletedTasksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        taskAdapter = TaskAdapter(
            onTaskClick = { task -> showTaskDialog(task) },
            onTaskChecked = { task -> viewModel.toggleTaskCompletion(task) }
        )

        binding.tasksRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = taskAdapter
        }

        val swipeCallback = SwipeToDeleteCallback(requireContext(), taskAdapter) { task ->
            viewModel.deleteTask(task)
            Snackbar.make(binding.root, R.string.task_deleted, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo) { viewModel.restoreTask(task) }
                .show()
        }
        ItemTouchHelper(swipeCallback).attachToRecyclerView(binding.tasksRecyclerView)

        viewModel.completedTasks.observe(viewLifecycleOwner) { tasks ->
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

    private fun showTaskDialog(task: Task) {
        AddEditTaskDialogFragment.newInstance(task) { savedTask ->
            savedTask ?: return@newInstance
            viewModel.updateTask(savedTask)
        }.show(parentFragmentManager, "EditCompletedTaskDialog")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
