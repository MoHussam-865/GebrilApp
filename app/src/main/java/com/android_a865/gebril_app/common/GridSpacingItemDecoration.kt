package com.android_a865.gebril_app.common

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class GridSpacingItemDecoration(
    private val spanCount: Int,
    private val spacing: Int,
    private val includeEdge: Boolean

): RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val position = parent.getChildAdapterPosition(view)

        val columns = position % spanCount

        if (includeEdge) {
            outRect.left = spacing - columns * spacing / spanCount

            outRect.right = (columns + 1) * spacing / spanCount

            if (position < spanCount) {
                outRect.top = spacing
            }
        }


    }


}