package rs.dk150.cryptotracker.ui

import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

class RecyclerTouchListener(
    context: Context?,
    private val clickListener: ClickListener?
) :
    RecyclerView.OnItemTouchListener {
    private var mDownY = 0f
    private var mDownX = 0f
    private var mScrolling = false
    private val mTouchSlop: Int = ViewConfiguration.get(context).scaledTouchSlop

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        val child = rv.findChildViewUnder(e.x, e.y)
        if (child != null && clickListener != null) {
            when (e.action) {
                MotionEvent.ACTION_DOWN -> {
                    mScrolling = false
                    mDownY = e.y
                    mDownX = e.x
                    clickListener.onDown(
                        child,
                        rv.getChildAdapterPosition(child)
                    )
                }
                MotionEvent.ACTION_UP -> {
                    if (!mScrolling) {
                        clickListener.onShortLongClick(child, rv.getChildAdapterPosition(child))
                        return true
                    } else {
                        clickListener.onUp(
                            child,
                            rv.getChildAdapterPosition(child)
                        )
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    val x = e.x
                    val xDelta = x - mDownX
                    val y = e.y
                    val yDelta = y - mDownY
                    if (abs(xDelta) > mTouchSlop || abs(yDelta) > mTouchSlop) {
                        mScrolling = true
                    }
                }

                else -> return false
            }
        }
        return false
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}

    interface ClickListener {
        fun onDown(view: View?, position: Int)
        fun onUp(view: View?, position: Int)
        fun onShortLongClick(view: View?, position: Int)
    }
}