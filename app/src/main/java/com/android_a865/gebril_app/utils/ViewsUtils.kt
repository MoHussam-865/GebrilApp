package com.android_a865.gebril_app.utils

import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.RecyclerView
import com.android_a865.gebril_app.R
import java.text.DecimalFormat

fun Fragment.setUpActionBarWithNavController() {
    view?.findViewById<Toolbar>(R.id.mainToolBar)?.let {
        appCompatActivity.setSupportActionBar(it)
        it.setupWithNavController(findNavController())
    }
}

fun RecyclerView.scrollToEnd() = smoothScrollToPosition(adapter?.itemCount ?: 0)

val Fragment.appCompatActivity get() = (activity as AppCompatActivity)

fun Fragment.showMessage(msg: String) {
    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
}

fun Double.toFormattedString(): String {
    val format = DecimalFormat("0.#")
    return format.format(this)
}

fun Int.toFormattedString(): String {
    return this.toDouble().toFormattedString()
}

fun EditText.setQty(str: String): Boolean {

    try {
        val value = str.toDouble()

        if (text.toString().toDouble() != value) {
            if (value <= 0.0) {
                setText("")
                return false
            }
            setText(value.toFormattedString())
            return true
        }
    } catch (e: Exception){
        setText(str)
        return false
    }
    return true
}

// Search View
inline fun SearchView.onTextChanged(crossinline listener: (String) -> Unit) {
    setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean = true

        override fun onQueryTextChange(newText: String?): Boolean {
            listener(newText.orEmpty())
            return true
        }
    })
}