package com.example.dtl.presentation.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.dtl.domain.model.Rectangle
import com.example.dtl.presentation.theme.AnalyticsVioletBar
import com.example.dtl.presentation.theme.ButtonBlue

@Composable
fun ImageWithRectangles(
    selectedId: Int? = null,
    imageUrl: String,
    rectangles: List<Rectangle>,
    onRectangleClick: (Int?) -> Unit,
    modifier: Modifier = Modifier
) {
    var imageSize by remember { mutableStateOf(IntSize.Zero) }
    
    Box(modifier = modifier) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .fillMaxWidth()
                .onSizeChanged { imageSize = it }
        )

        val textMeasurer = rememberTextMeasurer()
        Canvas(modifier = Modifier.matchParentSize()) {
            if (imageSize != IntSize.Zero) {
                rectangles.forEachIndexed { index, rect ->

                    val scaledStartX = rect.startX * imageSize.width
                    val scaledStartY = rect.startY * imageSize.height
                    val scaledWidth = (rect.endX - rect.startX) * imageSize.width
                    val scaledHeight = (rect.endY - rect.startY) * imageSize.height
                    
                    val isSelected = index == selectedId
                    val color = if (isSelected) ButtonBlue else AnalyticsVioletBar
                    
                    drawRect(
                        color = color.copy(alpha = 0.1f),
                        topLeft = Offset(scaledStartX, scaledStartY),
                        size = Size(scaledWidth, scaledHeight)
                    )
                    
                    drawRect(
                        color = color,
                        topLeft = Offset(scaledStartX, scaledStartY),
                        size = Size(scaledWidth, scaledHeight),
                        style = Stroke(width = 2.dp.toPx())
                    )

                    drawText(
                        textLayoutResult = textMeasurer.measure(
                            text = "${rectangles.indexOf(rect) + 1} ${rect.label}",
                            style = TextStyle(
                                color = Color.White,
                                background = color
                            )
                        ),
                        topLeft = Offset(scaledStartX, scaledStartY),
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .matchParentSize()
                .pointerInput(rectangles, imageSize) {
                    detectTapGestures { offset ->
                        if (imageSize != IntSize.Zero) {
                            val x = offset.x / imageSize.width
                            val y = offset.y / imageSize.height

                            val clickedRect = rectangles.find { rect ->
                                x in rect.startX..rect.endX && y in rect.startY..rect.endY
                            }
                            
                            clickedRect?.let { rect ->
                                onRectangleClick(rectangles.indexOf(rect))
                            } ?: run {
                                onRectangleClick(null)
                            }
                        }
                    }
                }
        )
    }
}