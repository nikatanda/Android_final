package com.example.final_project.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.final_project.R
import com.example.final_project.data.Priority
import com.example.final_project.data.Task
import com.example.final_project.databinding.DialogAddEditTaskBinding

class AddEditTaskDialogFragment : DialogFragment() {

    private var onSaveListener: ((Task?) -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogAddEditTaskBinding.inflate(LayoutInflater.from(requireContext()))

        val taskId = arguments?.getLong(ARG_TASK_ID, 0L) ?: 0L
        val isEdit = taskId != 0L

        val priorityLabels = Priority.entries.map { priority ->
            getString(
                when (priority) {
                    Priority.HIGH -> R.string.priority_high
                    Priority.MEDIUM -> R.string.priority_medium
                    Priority.LOW -> R.string.priority_low
                }
            )
        }
        binding.prioritySpinner.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            priorityLabels
        )

        InputUtils.enableMultilingualInput(binding.titleInput)
        InputUtils.enableMultilingualInput(binding.descriptionInput, multiLine = true)

        if (isEdit) {
            binding.dialogTitle.text = getString(R.string.edit_task)
            binding.titleInput.setText(arguments?.getString(ARG_TITLE).orEmpty())
            binding.descriptionInput.setText(arguments?.getString(ARG_DESCRIPTION).orEmpty())
            val priorityName = arguments?.getString(ARG_PRIORITY) ?: Priority.MEDIUM.name
            binding.prioritySpinner.setSelection(Priority.valueOf(priorityName).ordinal)
        } else {
            binding.dialogTitle.text = getString(R.string.add_task)
            binding.prioritySpinner.setSelection(Priority.MEDIUM.ordinal)
        }

        return AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .setPositiveButton(R.string.save, null)
            .setNegativeButton(R.string.cancel, null)
            .create()
            .also { dialog ->
                dialog.setOnShowListener {
                    InputUtils.enableMultilingualInput(binding.titleInput)
                    InputUtils.enableMultilingualInput(binding.descriptionInput, multiLine = true)
                    binding.titleInput.requestFocus()

                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                        val title = binding.titleInput.text?.toString().orEmpty()
                        if (title.isBlank()) {
                            onSaveListener?.invoke(null)
                            dialog.dismiss()
                            return@setOnClickListener
                        }

                        val description = binding.descriptionInput.text?.toString().orEmpty()
                        val selectedIndex = binding.prioritySpinner.selectedItemPosition
                        val priority = Priority.entries[selectedIndex]

                        val task = if (isEdit) {
                            Task(
                                id = taskId,
                                userId = arguments?.getLong(ARG_USER_ID) ?: 0L,
                                title = title.trim(),
                                description = description.trim(),
                                priority = priority,
                                isCompleted = arguments?.getBoolean(ARG_COMPLETED) ?: false,
                                createdAt = arguments?.getLong(ARG_CREATED_AT) ?: System.currentTimeMillis()
                            )
                        } else {
                            Task(
                                userId = arguments?.getLong(ARG_USER_ID) ?: 0L,
                                title = title.trim(),
                                description = description.trim(),
                                priority = priority
                            )
                        }
                        onSaveListener?.invoke(task)
                        dialog.dismiss()
                    }
                }
            }
    }

    companion object {
        private const val ARG_USER_ID = "arg_user_id"
        private const val ARG_TASK_ID = "arg_task_id"
        private const val ARG_TITLE = "arg_title"
        private const val ARG_DESCRIPTION = "arg_description"
        private const val ARG_PRIORITY = "arg_priority"
        private const val ARG_COMPLETED = "arg_completed"
        private const val ARG_CREATED_AT = "arg_created_at"

        fun newInstance(
            task: Task? = null,
            userId: Long = task?.userId ?: 0L,
            onSave: (Task?) -> Unit
        ): AddEditTaskDialogFragment {
            return AddEditTaskDialogFragment().apply {
                arguments = Bundle().apply {
                    if (task == null && userId != 0L) {
                        putLong(ARG_USER_ID, userId)
                    }
                    task?.let {
                        putLong(ARG_USER_ID, it.userId)
                        putLong(ARG_TASK_ID, it.id)
                        putString(ARG_TITLE, it.title)
                        putString(ARG_DESCRIPTION, it.description)
                        putString(ARG_PRIORITY, it.priority.name)
                        putBoolean(ARG_COMPLETED, it.isCompleted)
                        putLong(ARG_CREATED_AT, it.createdAt)
                    }
                }
                onSaveListener = onSave
            }
        }
    }
}
