package com.example.newsapp.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.newsapp.R
import com.example.newsapp.ui.home.NewsPagerAdapter

class SwipeGestureCallback(
    private val context: Context,
    private val adapter: NewsPagerAdapter,
    private val navController: NavController
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    private val deletePaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.FILL
    }

    private val openPaint = Paint().apply {
        color = Color.GREEN
        style = Paint.Style.FILL
    }

    private val deleteIcon = ContextCompat.getDrawable(context, R.drawable.ic_delete)!!
    private val openIcon = ContextCompat.getDrawable(context, R.drawable.ic_open)!!

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.bindingAdapterPosition

        if (direction == ItemTouchHelper.LEFT) {
            adapter.removeItem(position)
            Toast.makeText(context, "News Deleted", Toast.LENGTH_SHORT).show()
        } else if (direction == ItemTouchHelper.RIGHT) {
            val newsItem = adapter.getItemAtPosition(position)

            val bundle = Bundle().apply {
                putParcelable("article", newsItem)
            }
            navController.navigate(R.id.action_homeFragment_to_newsDetailFragment, bundle)
        }
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float, dY: Float,
        actionState: Int, isCurrentlyActive: Boolean
    ) {
        val itemView = viewHolder.itemView
        val iconSize = 96
        val iconMargin = (itemView.height - iconSize) / 2

        if (dX > 0) {
            c.drawRect(itemView.left.toFloat(), itemView.top.toFloat(), dX, itemView.bottom.toFloat(), openPaint)

            val iconTop = itemView.top + iconMargin
            val iconLeft = itemView.left + iconMargin
            val iconRight = iconLeft + iconSize
            val iconBottom = iconTop + iconSize

            openIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
            openIcon.draw(c)

        } else if (dX < 0) {
            c.drawRect(itemView.right.toFloat() + dX, itemView.top.toFloat(), itemView.right.toFloat(), itemView.bottom.toFloat(), deletePaint)

            val iconTop = itemView.top + iconMargin
            val iconRight = itemView.right - iconMargin
            val iconLeft = iconRight - iconSize
            val iconBottom = iconTop + iconSize

            deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
            deleteIcon.draw(c)
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

}

