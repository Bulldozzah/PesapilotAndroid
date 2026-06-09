package com.example.pesapilotandroid.ui.screens.money

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pesapilotandroid.ui.theme.*

val chartPalette = listOf(
    AppPrimary, AppAccentBlue, AppGreenSuccess, AppGold,
    Color(0xFF8B5CF6), Color(0xFFEC4899), Color(0xFF06B6D4),
    Color(0xFFF97316), AppAmberWarning, AppRedDestructive
)

@Composable
fun BarChart(
    data: List<Pair<String, Double>>,
    modifier: Modifier = Modifier,
    barColor: Color = AppAccentBlue,
    height: Int = 160
) {
    if (data.isEmpty()) return
    val maxVal = data.maxOf { it.second }.coerceAtLeast(1.0)
    Column(modifier = modifier.fillMaxWidth()) {
        Canvas(modifier = Modifier.fillMaxWidth().height(height.dp)) {
            val barWidth = size.width / (data.size * 2f)
            val gap = barWidth
            val textPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#6B7280")
                textSize = 22f
                textAlign = android.graphics.Paint.Align.CENTER
                isAntiAlias = true
            }
            data.forEachIndexed { i, (_, value) ->
                val color = chartPalette[i % chartPalette.size]
                val barHeight = (value / maxVal * (size.height - 28)).toFloat()
                val x = i * (barWidth + gap) + gap / 2
                val y = size.height - barHeight
                drawRect(color = color, topLeft = Offset(x, y), size = Size(barWidth, barHeight))
                drawContext.canvas.nativeCanvas.drawText(
                    fmtShort(value), x + barWidth / 2, y - 4f, textPaint
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            data.forEach { (label, _) ->
                Text(
                    text = label.take(5),
                    fontSize = 8.sp,
                    fontFamily = FigtreeFamily,
                    color = AppMutedText,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun HorizontalBarChart(
    data: List<Pair<String, Double>>,
    modifier: Modifier = Modifier,
    barColor: Color = AppAccentBlue,
    maxBars: Int = 8
) {
    val display = data.filter { it.second > 0 }.take(maxBars)
    if (display.isEmpty()) return
    val maxVal = display.maxOf { it.second }.coerceAtLeast(1.0)

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        display.forEachIndexed { i, (label, value) ->
            val color = chartPalette[i % chartPalette.size]
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label.take(18),
                    fontSize = 9.sp,
                    fontFamily = FigtreeFamily,
                    color = AppMutedText,
                    modifier = Modifier.width(90.dp),
                    maxLines = 1
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(14.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(AppSecondaryBg)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(fraction = (value / maxVal).toFloat().coerceIn(0f, 1f))
                            .clip(RoundedCornerShape(4.dp))
                            .background(color)
                    )
                }
                Text(
                    text = fmtAmount(value),
                    fontSize = 9.sp,
                    fontFamily = FigtreeFamily,
                    fontWeight = FontWeight.SemiBold,
                    color = AppText,
                    modifier = Modifier.width(70.dp).padding(start = 4.dp)
                )
            }
        }
    }
}

@Composable
fun PieChart(
    data: List<ReportsViewModel.ChartData>,
    modifier: Modifier = Modifier,
    size: Int = 160
) {
    val total = data.sumOf { it.value }
    if (total <= 0) return

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Canvas(modifier = Modifier.size(size.dp)) {
            var startAngle = -90f
            data.forEachIndexed { i, slice ->
                val color = chartPalette[i % chartPalette.size]
                val sweep = (slice.value / total * 360).toFloat()
                drawArc(
                    color = color,
                    startAngle = startAngle,
                    sweepAngle = sweep,
                    useCenter = true,
                    size = Size(this.size.width, this.size.height)
                )
                startAngle += sweep
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            data.take(6).forEachIndexed { i, slice ->
                val color = chartPalette[i % chartPalette.size]
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(color)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = slice.label.take(14),
                        fontSize = 9.sp,
                        fontFamily = FigtreeFamily,
                        color = AppText,
                        modifier = Modifier.weight(1f),
                        maxLines = 1
                    )
                    Text(
                        text = "${((slice.value / total) * 100).toInt()}%",
                        fontSize = 9.sp,
                        fontFamily = FigtreeFamily,
                        fontWeight = FontWeight.SemiBold,
                        color = AppMutedText
                    )
                }
            }
        }
    }
}

@Composable
fun LineChart(
    data: List<Pair<String, Double>>,
    modifier: Modifier = Modifier,
    lineColor: Color = AppGreenSuccess,
    height: Int = 160
) {
    if (data.size < 2) return
    val maxVal = data.maxOf { it.second }.coerceAtLeast(1.0)
    val minVal = data.minOf { it.second }

    Column(modifier = modifier.fillMaxWidth()) {
        Canvas(modifier = Modifier.fillMaxWidth().height(height.dp)) {
            val range = (maxVal - minVal).coerceAtLeast(1.0)
            val stepX = size.width / (data.size - 1)
            val textPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#6B7280")
                textSize = 20f
                textAlign = android.graphics.Paint.Align.CENTER
                isAntiAlias = true
            }
            val path = Path()
            data.forEachIndexed { i, (_, value) ->
                val x = i * stepX
                val y = size.height - ((value - minVal) / range * (size.height - 28)).toFloat()
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            drawPath(path, lineColor, style = Stroke(width = 2.5f))
            data.forEachIndexed { i, (_, value) ->
                val x = i * stepX
                val y = size.height - ((value - minVal) / range * (size.height - 28)).toFloat()
                drawCircle(lineColor, radius = 3f, center = Offset(x, y))
                if (data.size <= 8) {
                    drawContext.canvas.nativeCanvas.drawText(fmtShort(value), x, y - 8f, textPaint)
                }
            }
        }
        if (data.size <= 12) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                data.forEachIndexed { i, (label, _) ->
                    if (i % (data.size / 6 + 1) == 0 || i == data.lastIndex) {
                        Text(
                            text = label.takeLast(3),
                            fontSize = 7.sp,
                            fontFamily = FigtreeFamily,
                            color = AppMutedText
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AreaChart(
    data: List<Pair<String, Double>>,
    modifier: Modifier = Modifier,
    areaColor: Color = AppAccentBlue,
    height: Int = 120
) {
    if (data.size < 2) return
    val maxVal = data.maxOf { it.second }.coerceAtLeast(1.0)

    Canvas(modifier = modifier.fillMaxWidth().height(height.dp)) {
        val stepX = size.width / (data.size - 1)
        val path = Path()
        data.forEachIndexed { i, (_, value) ->
            val x = i * stepX
            val y = size.height - (value / maxVal * size.height).toFloat()
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.lineTo(size.width, size.height)
        path.lineTo(0f, size.height)
        path.close()
        drawPath(path, areaColor.copy(alpha = 0.2f))
        drawPath(
            Path().apply {
                data.forEachIndexed { i, (_, value) ->
                    val x = i * stepX
                    val y = size.height - (value / maxVal * size.height).toFloat()
                    if (i == 0) moveTo(x, y) else lineTo(x, y)
                }
            },
            areaColor,
            style = Stroke(width = 2f)
        )
    }
}

private fun fmtAmount(v: Double): String = String.format(java.util.Locale.US, "%,.2f", v)

private fun fmtShort(v: Double): String {
    val abs = kotlin.math.abs(v)
    return when {
        abs >= 1_000_000 -> String.format(java.util.Locale.US, "%.1fM", v / 1_000_000)
        abs >= 1_000 -> String.format(java.util.Locale.US, "%.1fK", v / 1_000)
        else -> String.format(java.util.Locale.US, "%.0f", v)
    }
}
