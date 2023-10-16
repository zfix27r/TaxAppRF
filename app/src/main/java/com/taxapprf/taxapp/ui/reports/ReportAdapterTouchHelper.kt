package com.taxapprf.taxapp.ui.reports

import android.graphics.Canvas
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import kotlin.math.abs

class ReportAdapterTouchHelper(
    private val callback: ReportsAdapterTouchHelperCallback
) :
    ItemTouchHelper.Callback() {
    private var viewHolder: ViewHolder? = null
    override fun isItemViewSwipeEnabled(): Boolean {
        return true
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: ViewHolder
    ): Int {
        val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END

        return makeFlag(ItemTouchHelper.ACTION_STATE_SWIPE, swipeFlags)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: ViewHolder,
        target: ViewHolder
    ): Boolean {
        TODO("Not yet implemented")
    }

    override fun onSwiped(viewHolder: ViewHolder, direction: Int) {
        this.viewHolder = viewHolder
        val report = (viewHolder as ReportsAdapterViewHolder).report
        callback.onSwiped(report)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            val width = viewHolder.itemView.width.toFloat()
            val alpha = 1.0f - abs(dX) / width
            viewHolder.itemView.alpha = alpha
            viewHolder.itemView.translationX = dX
        } else {
            super.onChildDraw(
                c, recyclerView, viewHolder, dX, dY,
                actionState, isCurrentlyActive
            )
        }
    }

    fun cancelSwipe() {
        viewHolder?.itemView?.animate()?.translationX(0f)?.alpha(1.0f)?.start()
    }
}