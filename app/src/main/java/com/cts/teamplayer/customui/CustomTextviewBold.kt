package com.cts.teamplayer.customui

import android.content.Context
import android.graphics.Typeface
import androidx.appcompat.widget.AppCompatTextView
import android.util.AttributeSet
import com.cts.teamplayer.R
import com.cts.teamplayer.util.FontCache


class CustomTextviewBold : AppCompatTextView {

    constructor(context: Context) : super(context) {
        applyCustomFont(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        applyCustomFont(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        applyCustomFont(context, attrs)
    }


    private fun applyCustomFont(context: Context, attrs: AttributeSet?) {

        val attributeArray = context.obtainStyledAttributes(attrs, R.styleable.CustomView)
        val fontName = attributeArray.getString(R.styleable.CustomView_fontcustom)

        val textStyle = attrs!!.getAttributeIntValue(ANDROID_SCHEMA, "textStyle", Typeface.NORMAL)

        val customFont = selectTypeface(context, fontName, textStyle)
        typeface = customFont
        attributeArray.recycle()
    }


    private fun selectTypeface(context: Context, fontName: String?, textStyle: Int): Typeface? {
        return if (fontName == null) {
            FontCache.getTypeface("Roboto-Bold.ttf", context)
        } else if (fontName.contentEquals("Roboto-Bold.ttf")) {
            FontCache.getTypeface("Roboto-Bold.ttf", context)

        } else {
            FontCache.getTypeface("Roboto-Bold.ttf", context)
        }
    }

    companion object {

        val ANDROID_SCHEMA = "http://schemas.android.com/apk/res/android"
    }

}