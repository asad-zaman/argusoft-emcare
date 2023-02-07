package com.argusoft.who.emcare.utils.avatar

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.text.TextPaint
import java.util.*

class AvatarGenerator(private val builder: AvatarBuilder) {

    class AvatarBuilder(private val context: Context) {

        private var textSize = 100
        private var size = 14
        private var name = " "
        private var backgroundColor: Int? = null
        private var shapeType = CIRCLE


        fun setTextSize(textSize: Int) = apply {
            this.textSize = textSize
        }

        fun setAvatarSize(int: Int) = apply {
            this.size = int
        }

        fun setLabel(label: String) = apply {
            this.name = label
        }

        fun setBackgroundColor(color: Int) = apply {
            this.backgroundColor = color
        }

        fun toSquare() = apply {
            this.shapeType = RECTANGLE
        }

        fun toCircle() = apply {
            this.shapeType = CIRCLE
        }


        fun build(): BitmapDrawable {
            return avatarImageGenerate(
                context,
                size,
                shapeType,
                name,
                textSize,
                COLOR700
            )
        }


        private fun avatarImageGenerate(
            context: Context,
            size: Int,
            shape: Int,
            name: String,
            textSize: Int,
            colorModel: Int
        ): BitmapDrawable {
            uiContext = context

            texSize = calTextSize(textSize)
            val label = firstCharacter(name)
            val textPaint = textPainter()
            val painter = painter()
            painter.isAntiAlias = true
            val areaRect = Rect(0, 0, size, size)

            if (shape == 0) {
                painter.color = backgroundColor ?: RandomColors(colorModel).getColor()
            } else {
                painter.color = Color.TRANSPARENT
            }

            val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            canvas.drawRect(areaRect, painter)

            //reset painter
            if (shape == 0) {
                painter.color = Color.TRANSPARENT
            } else {
                painter.color = backgroundColor ?: RandomColors(colorModel).getColor()
            }

            val bounds = RectF(areaRect)
            bounds.right = textPaint.measureText(label, 0, 1)
            bounds.bottom = textPaint.descent() - textPaint.ascent()

            bounds.left += (areaRect.width() - bounds.right) / 2.0f
            bounds.top += (areaRect.height() - bounds.bottom) / 2.0f

            canvas.drawCircle(size.toFloat() / 2, size.toFloat() / 2, size.toFloat() / 2, painter)
            canvas.drawText(label, bounds.left, bounds.top - textPaint.ascent(), textPaint)
            return BitmapDrawable(uiContext.resources, bitmap)

        }

        private fun firstCharacter(name: String): String {
            if (name.isEmpty()) {
                return "-"
            }
            return name.first().toString()
        }

        private fun textPainter(): TextPaint {
            val textPaint = TextPaint()
            textPaint.isAntiAlias = true
            textPaint.textSize = texSize * uiContext.resources.displayMetrics.scaledDensity
            textPaint.color = Color.WHITE
            return textPaint
        }

        private fun painter(): Paint {
            return Paint()
        }

        private fun calTextSize(size: Int): Float {
            return (size).toFloat()
        }

    }


    /**
     * Deprecate and will be removed
     */
    companion object {
        val CIRCLE = 1
        val RECTANGLE = 0
        val COLOR900=900
        val COLOR400=400
        val COLOR700=700

        private lateinit var uiContext: Context
        private var texSize = 0F

        @Deprecated("Switch to using builder method")
        fun avatarImage(context: Context, size: Int, shape: Int, name: String): BitmapDrawable {
            return avatarImageGenerate(context, size, shape, name, COLOR700)
        }


        fun avatarImage(
            context: Context,
            size: Int,
            shape: Int,
            name: String,
            colorModel: Int
        ): BitmapDrawable {
            return avatarImageGenerate(context, size, shape, name, colorModel)
        }

        private fun avatarImageGenerate(
            context: Context,
            size: Int,
            shape: Int,
            name: String,
            colorModel: Int
        ): BitmapDrawable {
            uiContext = context

            texSize = calTextSize(size)
            val label = firstCharacter(name)
            val textPaint = textPainter()
            val painter = painter()
            painter.isAntiAlias = true
            val areaRect = Rect(0, 0, size, size)

            if (shape == 0) {
                painter.color = RandomColors(colorModel).getColor()
            } else {
                painter.color = Color.TRANSPARENT
            }

            val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            canvas.drawRect(areaRect, painter)

            //reset painter
            if (shape == 0) {
                painter.color = Color.TRANSPARENT
            } else {
                painter.color = RandomColors(colorModel).getColor()
            }

            val bounds = RectF(areaRect)
            bounds.right = textPaint.measureText(label, 0, 1)
            bounds.bottom = textPaint.descent() - textPaint.ascent()

            bounds.left += (areaRect.width() - bounds.right) / 2.0f
            bounds.top += (areaRect.height() - bounds.bottom) / 2.0f

            canvas.drawCircle(size.toFloat() / 2, size.toFloat() / 2, size.toFloat() / 2, painter)
            canvas.drawText(label, bounds.left, bounds.top - textPaint.ascent(), textPaint)
            return BitmapDrawable(uiContext.resources, bitmap)

        }


        private fun firstCharacter(name: String): String {
            return name.first().toString().uppercase(Locale.ROOT)
        }

        private fun textPainter(): TextPaint {
            val textPaint = TextPaint()
            textPaint.isAntiAlias = true
            textPaint.textSize = texSize * uiContext.resources.displayMetrics.scaledDensity
            textPaint.color = Color.WHITE
            return textPaint
        }

        private fun painter(): Paint {
            return Paint()
        }

        private fun calTextSize(size: Int): Float {
            return (size).toFloat()
        }
    }
}

internal class RandomColors(colorModel:Int=700) {
    private val recycle: Stack<Int> = Stack()
    private val colors: Stack<Int> = Stack()

    fun getColor(): Int {
        if (colors.size == 0) {
            while (!recycle.isEmpty()) colors.push(recycle.pop())
            Collections.shuffle(colors)
        }
        val c: Int = colors.pop()
        recycle.push(c)
        return c
    }

    init {
        if (colorModel==700){
            recycle.addAll(
                //A 700
                listOf(
                    -0xd32f2f, -0xC2185B, -0x7B1FA2, -0x512DA8,
                    -0x303F9F, -0x1976D2, -0x0288D1, -0x0097A7,
                    -0x00796B, -0x388E3C, -0x689F38, -0xAFB42B,
                    -0xFBC02D, -0xFFA000, -0xF57C00,  -0xE64A19,
                    -0x5D4037, -0x616161, -0x455A64
                )
            )
        }

        //A400
        if(colorModel==400){
            recycle.addAll(
                listOf(
                    -0xef5350, -0xEC407A, -0xAB47BC, -0x7E57C2,
                    -0x5C6BC0, -0x42A5F5, -0x29B6F6, -0x26C6DA,
                    -0x26A69A, -0x66BB6A, -0x9CCC65, -0xD4E157,
                    -0xFFEE58, -0xFFCA28, -0xFFA726, -0xFF7043,
                    -0x8D6E63, -0xBDBDBD, -0x78909C
                )
            )
        }

        //A900
        if(colorModel==900){
            recycle.addAll(
                listOf(
                    -0xb71c1c, -0x880E4F, -0x4A148C, -0x311B92,
                    -0x1A237E, -0x0D47A1, -0x01579B, -0x006064,
                    -0x004D40, -0x1B5E20, -0x33691E, -0x827717,
                    -0xF57F17, -0xFF6F00, -0xE65100, -0xBF360C,
                    -0x3E2723, -0x212121, -0x263238
                )
            )
        }

    }
}
