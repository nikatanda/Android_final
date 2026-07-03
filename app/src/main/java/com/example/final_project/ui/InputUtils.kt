package com.example.final_project.ui

import android.text.InputType
import android.text.method.TextKeyListener
import android.view.inputmethod.EditorInfo
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.appcompat.widget.SearchView

object InputUtils {

    fun enableMultilingualInput(editText: EditText, multiLine: Boolean = false) {
        val inputType = if (multiLine) {
            InputType.TYPE_CLASS_TEXT or
                InputType.TYPE_TEXT_VARIATION_NORMAL or
                InputType.TYPE_TEXT_FLAG_MULTI_LINE
        } else {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_NORMAL
        }

        editText.setRawInputType(inputType)
        editText.inputType = inputType
        editText.keyListener = TextKeyListener.getInstance()
        editText.filters = emptyArray()
        editText.setPrivateImeOptions(null)
        editText.imeOptions = if (multiLine) {
            EditorInfo.IME_FLAG_NO_ENTER_ACTION
        } else {
            EditorInfo.IME_ACTION_DONE
        }

        if (editText is AutoCompleteTextView) {
            editText.threshold = Int.MAX_VALUE
            editText.setAdapter(null)
        }

        editText.post {
            editText.setRawInputType(inputType)
            editText.inputType = inputType
            editText.keyListener = TextKeyListener.getInstance()
            editText.filters = emptyArray()
            editText.setPrivateImeOptions(null)
        }
    }

    fun enableMultilingualPasswordInput(editText: EditText) {
        val inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

        editText.setRawInputType(inputType)
        editText.inputType = inputType
        editText.keyListener = TextKeyListener.getInstance()
        editText.filters = emptyArray()
        editText.setPrivateImeOptions(null)
        editText.imeOptions = EditorInfo.IME_ACTION_DONE

        editText.post {
            editText.setRawInputType(inputType)
            editText.inputType = inputType
            editText.keyListener = TextKeyListener.getInstance()
            editText.filters = emptyArray()
            editText.setPrivateImeOptions(null)
        }
    }

    fun fixSearchViewInput(searchView: SearchView) {
        val searchEditText = searchView.findViewById<EditText>(
            androidx.appcompat.R.id.search_src_text
        ) ?: return

        enableMultilingualInput(searchEditText)

        searchView.setOnSearchClickListener {
            enableMultilingualInput(searchEditText)
        }
        searchView.setOnQueryTextFocusChangeListener { _, _ ->
            enableMultilingualInput(searchEditText)
        }
    }
}
