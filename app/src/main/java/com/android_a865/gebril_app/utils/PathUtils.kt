package com.android_a865.gebril_app.utils

import android.os.Parcelable
import com.android_a865.gebril_app.data.domain.InvoiceItem
import kotlinx.parcelize.Parcelize

@Parcelize
data class Path(
    val parents: List<InvoiceItem> = listOf(
        InvoiceItem(name = "items", path = ROOT_PATH)
    )
) : Parcelable {
    /** a new abstraction layer to deal with paths (navigation) */

    fun open(child: InvoiceItem): Path {
        val x = parents.toMutableList()
        x.add(child)
        return Path(x.toList())
    }

    fun back() = Path(parents.dropLast(1))

    val isRoot get() = (parents.last().id == ROOT)

    //val folders get() = path.replaceFirst(ROOT, "Items").split(Separation)

    fun fullName(name: String): String {
        val list = names.drop(1).toMutableList()
        list.add(name)
        return list.reversed().joinToString(" ")
    }

    private val names get() = parents.map { it.name }


    val parent get() = parents.last()

    // val parentPath get(): String = names.dropLast(1).joinToString(separator = Separation)

    val folderId get(): Int = parents.last().id

    val folderDiscount get(): Double = parents.last().discount



    companion object {
        const val ROOT = 0
        const val ROOT_PATH = "*"
        const val SEPARATION = "&;"
    }
}

