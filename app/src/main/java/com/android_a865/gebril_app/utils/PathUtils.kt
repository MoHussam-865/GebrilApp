package com.android_a865.gebril_app.utils

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


interface PathUtil {
    val name: String
    val path: Path
    val isFolder: Boolean

    fun open(): Path = if (isFolder) path.open(name) else path
}


@Parcelize
data class Path(
    val path: String = ROOT
) : Parcelable {
    /** a new abstraction layer to deal with paths (navigation) */

    fun open(name: String) = Path(path + Separation + name)

    fun pathOf(name: String) = path + Separation + name

    fun back() = Path(parentPath)

    val isRoot get() = (path == ROOT)

    val folders get() = path.replaceFirst(ROOT, "Items").split(Separation)

    fun fullName(name: String): String {
        val list = folders.drop(1).toMutableList()
        list.add(name)
        return list.reversed().joinToString(" ")
    }

    val parentName get() = _folders.run { get(lastIndex) }

    private val parentPath get() = _folders.dropLast(1).joinToString(separator = Separation)

    private val _folders get() = path.split(Separation)

    companion object {
        private const val Separation = "/"
        private const val ROOT = "."
        const val NO_PATH = ""
        const val REDUNDANT = "*"
    }
}

