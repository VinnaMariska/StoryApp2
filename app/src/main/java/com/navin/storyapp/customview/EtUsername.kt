package com.navin.storyapp.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.navin.storyapp.R

class EtUsername : TextInputEditText, View.OnTouchListener {

    private lateinit var personIcon: Drawable
    private lateinit var clearButton: Drawable

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }

    private fun init() {
        clearButton = ContextCompat.getDrawable(context, R.drawable.ic_close) as Drawable // x button
        setOnTouchListener(this)

        personIcon =
            ContextCompat.getDrawable(context, R.drawable.ic_baseline_person_24) as Drawable
        setIconDrawable(startOfTheText = personIcon)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Do nothing.
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // Do nothing.
            }

            override fun afterTextChanged(s: Editable) {
                if (s.toString().isNotEmpty()) showClearButton() else hideClearButton()

                // check input
                if (s.toString().isEmpty()) showError()
            }
        })
    }

    private fun setIconDrawable(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null
    ) {
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )
    }

    private fun showError() {
        error = context.getString(R.string.fill_all)
    }

    private fun showClearButton() {
        setIconDrawable(endOfTheText = clearButton)
    }

    private fun hideClearButton() {
        setIconDrawable()
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (compoundDrawables[2] != null) {
            val clearButtonStart: Float
            val clearButtonEnd: Float
            var isClearButtonClicked = false

            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                clearButtonEnd = (clearButton.intrinsicWidth + paddingStart).toFloat()
                if (event.x < clearButtonEnd) isClearButtonClicked = true
            } else {
                clearButtonStart = (width - paddingEnd - clearButton.intrinsicWidth).toFloat()
                if (event.x > clearButtonStart) isClearButtonClicked = true
            }

            if (isClearButtonClicked) {
                return when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        clearButton = ContextCompat.getDrawable(context, R.drawable.ic_close) as Drawable
                        showClearButton() // show button x
                        true
                    }
                    MotionEvent.ACTION_UP -> {
                        clearButton = ContextCompat.getDrawable(context, R.drawable.ic_close) as Drawable
                        if (text != null) text?.clear() // clear text
                        hideClearButton()  // hide button x
                        true
                    }
                    else -> false
                }
            } else return false
        }
        return false
    }
}