package com.example.final_project.ui

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.final_project.R
import com.example.final_project.data.Priority
import com.example.final_project.data.Task
import com.example.final_project.databinding.ItemTaskBinding

class TaskAdapter(
    private val onTaskClick: (Task) -> Unit,
    private val onTaskChecked: (Task) -> Unit
) : ListAdapter<Task, TaskAdapter.TaskViewHolder>(TaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TaskViewHolder(
        private val binding: ItemTaskBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(task: Task) {
            val context = binding.root.context

            binding.taskTitle.text = task.title
            binding.taskDescription.text = task.description
            binding.taskDescription.visibility = if (task.description.isBlank()) {
                android.view.View.GONE
            } else {
                android.view.View.VISIBLE
            }

            binding.taskCheckbox.isChecked = task.isCompleted
            binding.taskCheckbox.setOnClickListener {
                onTaskChecked(task)
            }

            if (task.isCompleted) {
                binding.taskTitle.paintFlags =
                    binding.taskTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                binding.taskTitle.alpha = 0.6f
            } else {
                binding.taskTitle.paintFlags =
                    binding.taskTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                binding.taskTitle.alpha = 1f
            }

            val style = when (task.priority) {
                Priority.HIGH -> PriorityStyle(
                    R.color.priority_high,
                    R.drawable.bg_priority_high,
                    R.color.priority_high_text,
                    R.string.priority_high
                )
                Priority.MEDIUM -> PriorityStyle(
                    R.color.priority_medium,
                    R.drawable.bg_priority_medium,
                    R.color.priority_medium_text,
                    R.string.priority_medium
                )
                Priority.LOW -> PriorityStyle(
                    R.color.priority_low,
                    R.drawable.bg_priority_low,
                    R.color.priority_low_text,
                    R.string.priority_low
                )
            }

            binding.priorityIndicator.setBackgroundColor(
                ContextCompat.getColor(context, style.indicatorColor)
            )
            binding.priorityBadge.setBackgroundResource(style.badgeBackground)
            binding.priorityBadge.setTextColor(ContextCompat.getColor(context, style.badgeTextColor))
            binding.priorityBadge.setText(style.badgeLabel)

            binding.root.setOnClickListener {
                onTaskClick(task)
            }
        }
    }

    private class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean =
            oldItem == newItem
    }

    private data class PriorityStyle(
        val indicatorColor: Int,
        val badgeBackground: Int,
        val badgeTextColor: Int,
        val badgeLabel: Int
    )
}
