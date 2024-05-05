package com.android_a865.gebril_app.utils

interface Selectable<T> {
    var isSelected: Boolean

    fun copy(b: Boolean = isSelected): T
    fun changeSelection() {
        isSelected = !isSelected
    }
}


val <T> List<Selectable<T>>.numSelected get() = this.filterSelected().size

fun <T> List<Selectable<T>>?.selectAll(b: Boolean): List<T> = this.orEmpty().map { it.copy(b) }

fun <T> List<Selectable<T>>?.filterSelected(): List<T> =
    this.orEmpty().filter { it.isSelected }.map {
        it.copy()
    }

fun <T> List<Selectable<T>>.selectOnlyWhere(fun0: (T) -> Boolean): List<T> = this.map {
    it.copy(fun0(it.copy()))
}



