package com.android_a865.gebril_app.utils

import android.os.Parcelable
import com.android_a865.gebril_app.data.domain.InvoiceItem
import kotlinx.parcelize.Parcelize

@Parcelize
data class Path(
    val parents: MutableList<InvoiceItem> = mutableListOf(
        InvoiceItem()
    )
) : Parcelable {
    /** a new abstraction layer to deal with paths (navigation) */

    fun open(child: InvoiceItem): Path {
        parents.add(child)
        return Path(parents)
    }
    //fun pathOf(name: String) = path + Separation + name

    fun back() = Path(parents.dropLast(1).toMutableList())

    val isRoot get() = (parents.last().path == ROOT)

    //val folders get() = path.replaceFirst(ROOT, "Items").split(Separation)

    fun fullName(name: String): String {

        val list = names.drop(1).toMutableList()
        list.add(name)
        return list.reversed().joinToString(" ")
    }

    private val names get() = parents.map { it.name }


    val parent get() = parents.last()

    // val parentPath get(): String = names.dropLast(1).joinToString(separator = Separation)

    val path get(): String = names.joinToString(separator = Separation)

    val pathDiscount get(): Double = parents.last().discount


    //private val _folders get() = path.split(Separation)

    companion object {
        private const val Separation = "/"
        const val ROOT = "."
//        const val NO_PATH = ""
//        const val REDUNDANT = "*"
    }
}

