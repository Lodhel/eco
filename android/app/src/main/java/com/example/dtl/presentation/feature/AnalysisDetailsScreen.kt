package com.example.dtl.presentation.feature

import android.annotation.SuppressLint
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.dtl.R
import com.example.dtl.data.network.model.AnalysisResult
import com.example.dtl.domain.ResourceState
import com.example.dtl.presentation.theme.AnalyticsBlueBar
import com.example.dtl.presentation.theme.AnalyticsBlueChart
import com.example.dtl.presentation.theme.AnalyticsDarkerBlue
import com.example.dtl.presentation.theme.AnalyticsDarkerViolet
import com.example.dtl.presentation.theme.AnalyticsLightestBlue
import com.example.dtl.presentation.theme.AnalyticsLightestViolet
import com.example.dtl.presentation.theme.AnalyticsVioletBar
import com.example.dtl.presentation.theme.AnalyticsVioletChart
import com.example.dtl.presentation.theme.BlueGradient
import com.example.dtl.presentation.theme.Grey
import com.example.dtl.presentation.theme.LightestGrey
import com.example.dtl.presentation.theme.PinkGradient
import com.example.dtl.presentation.theme.StateGreen
import com.example.dtl.presentation.theme.StateRed
import com.example.dtl.presentation.theme.StateYellow
import com.example.dtl.presentation.theme.VioletGradient
import com.example.dtl.presentation.viewModel.MainViewModel
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.DividerProperties
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.Pie
import kotlin.math.max
import kotlin.math.min

@Composable
fun AnalysisDetailsScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = hiltViewModel(),
    orderId: Int,
    onBackPressed: () -> Unit
) {

    LaunchedEffect(orderId) {
        viewModel.loadAnalysisDetails(orderId)
    }

    val analysisState by viewModel.analysisDetails

    when (val state = analysisState) {
        is ResourceState.Loading -> {}
        is ResourceState.Success -> {
            AnalysisDetailsContent(modifier = modifier, analysisResult = state.data, onBackPressed = onBackPressed)
        }

        is ResourceState.Error -> {}
    }
}

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun AnalysisDetailsContent(modifier: Modifier, analysisResult: AnalysisResult, onBackPressed: () -> Unit) {
    val configuration = LocalConfiguration.current

    val scroll = rememberScrollState()

    Column(
        modifier
            .background(Color.White)
            .fillMaxSize()
            .verticalScroll(scroll),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            IconButton(
                modifier = Modifier.align(Alignment.CenterStart),
                onClick = { onBackPressed.invoke() }) {
                Icon(
                    painterResource(R.drawable.ic_chevron_left),
                    contentDescription = "Back",
                    tint = Grey
                )
            }
            Text(
                text = "Общая аналитика",
                fontSize = 24.sp,
                modifier = Modifier.align(Alignment.Center),
            )
        }

        if (configuration.screenWidthDp < 700) {
            Column {
                PlantTypesChart(
                    Modifier
                        .height((configuration.screenHeightDp / 3).dp)
                        .padding(8.dp)
                        .fillMaxWidth(),
                    analysisResult.total_trees.toDouble(),
                    analysisResult.total_shrubs.toDouble(),
                )
                Row(Modifier.height((configuration.screenHeightDp / 4).dp)) {
                    TreeCard(
                        Modifier
                            .padding(8.dp)
                            .fillMaxHeight()
                            .weight(1f),
                        analysisResult.total_trees
                    )
                    ShrubCard(
                        Modifier
                            .padding(8.dp)
                            .fillMaxHeight()
                            .weight(1f),
                        analysisResult.total_shrubs
                    )
                }
                TreeTypesChart(
                    Modifier
                        .height((configuration.screenHeightDp / 3).dp)
                        .padding(8.dp)
                        .fillMaxWidth(),
                    analysisResult.tree_types,
                    barWidth = if (analysisResult.tree_types.isNotEmpty())
                        min(
                            54,
                            (configuration.screenWidthDp / (2 * analysisResult.tree_types.size))
                        )
                    else 54
                )
                ShrubTypesChart(
                    Modifier
                        .height((configuration.screenHeightDp / 3).dp)
                        .padding(8.dp)
                        .fillMaxWidth(),
                    analysisResult.shrub_types,
                    barWidth = if (analysisResult.shrub_types.isNotEmpty())
                        min(
                            54,
                            (configuration.screenWidthDp / (2 * analysisResult.shrub_types.size))
                        )
                    else 54
                )
                SeasonCard(
                    Modifier
                        .height((configuration.screenHeightDp / 4).dp)
                        .padding(8.dp)
                        .fillMaxWidth(),
                    analysisResult.season
                )
                StatesChart(
                    Modifier
                        .height((configuration.screenHeightDp / 3).dp)
                        .padding(8.dp)
                        .fillMaxWidth(),
                    analysisResult.condition_status.bad,
                    analysisResult.condition_status.normal,
                    analysisResult.condition_status.good,
                    barWidth = min(54, (configuration.screenWidthDp / 6))
                )
            }
        } else {
            Column {
                Row(Modifier.height(max(300, (configuration.screenHeightDp / 3)).dp)) {
                    ShrubTypesChart(
                        Modifier
                            .padding(8.dp)
                            .fillMaxHeight()
                            .weight(1f),
                        analysisResult.shrub_types,
                        barWidth = if (analysisResult.shrub_types.isNotEmpty())
                            min(
                                54,
                                (configuration.screenWidthDp / (4 * analysisResult.shrub_types.size))
                            )
                        else 54
                    )
                    PlantTypesChart(
                        Modifier
                            .padding(8.dp)
                            .fillMaxHeight()
                            .weight(1f),
                        analysisResult.total_trees.toDouble(),
                        analysisResult.total_shrubs.toDouble()
                    )
                }
                Row(Modifier.height(max(200, (configuration.screenHeightDp / 5)).dp)) {
                    TreeCard(
                        Modifier
                            .padding(8.dp)
                            .fillMaxHeight()
                            .weight(1f),
                        analysisResult.total_trees
                    )
                    ShrubCard(
                        Modifier
                            .padding(8.dp)
                            .fillMaxHeight()
                            .weight(1f),
                        analysisResult.total_shrubs
                    )
                    SeasonCard(
                        Modifier
                            .padding(8.dp)
                            .fillMaxHeight()
                            .weight(1f),
                        analysisResult.season
                    )
                }
                Row(Modifier.height((max(300, configuration.screenHeightDp / 3)).dp)) {
                    TreeTypesChart(
                        Modifier
                            .padding(8.dp)
                            .fillMaxHeight()
                            .weight(1f),
                        analysisResult.tree_types,
                        barWidth = if (analysisResult.tree_types.isNotEmpty())
                            min(
                                54,
                                (configuration.screenWidthDp / (4 * analysisResult.tree_types.size))
                            )
                        else 54
                    )
                    StatesChart(
                        Modifier
                            .padding(8.dp)
                            .fillMaxHeight()
                            .weight(1f),
                        analysisResult.condition_status.bad,
                        analysisResult.condition_status.normal,
                        analysisResult.condition_status.good,
                        barWidth = min(54, (configuration.screenWidthDp / 12))
                    )
                }
            }
        }
    }
}

@Composable
fun SeasonCard(modifier: Modifier = Modifier, season: String) {
    Box(
        modifier = modifier
            .background(PinkGradient, RoundedCornerShape(12.dp))
            .padding(24.dp)
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(
                    SpanStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium
                    )
                ) { append("Сезон\n") }
                withStyle(SpanStyle(fontSize = 16.sp)) { append(season) }
            },
            modifier = Modifier.align(Alignment.TopStart),
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium
        )
        Image(
            painter = painterResource(R.drawable.ic_leaf),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(top = 32.dp)
        )
    }
}

@Composable
fun TreeCard(modifier: Modifier = Modifier, treesCount: Int) {
    Box(
        modifier = modifier
            .background(BlueGradient, RoundedCornerShape(12.dp))
            .padding(24.dp)
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(
                    SpanStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium
                    )
                ) { append("$treesCount\n") }
                withStyle(SpanStyle(fontSize = 16.sp)) { append("деревьев") }
            },
            modifier = Modifier.align(Alignment.TopStart),
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium
        )
        Image(
            painter = painterResource(R.drawable.ic_blue_tree),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(top = 32.dp)
        )
    }
}

@Composable
fun ShrubCard(modifier: Modifier = Modifier, shrubsCount: Int) {
    Box(
        modifier = modifier
            .background(VioletGradient, RoundedCornerShape(12.dp))
            .padding(24.dp)
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(
                    SpanStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium
                    )
                ) { append("$shrubsCount\n") }
                withStyle(SpanStyle(fontSize = 16.sp)) { append("кустарников") }
            },
            modifier = Modifier.align(Alignment.TopStart),
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium
        )
        Image(
            painter = painterResource(R.drawable.ic_violet_clouds),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(top = 32.dp)
        )
    }
}

@Composable
fun PlantTypesChart(
    modifier: Modifier = Modifier,
    treesCount: Double,
    shrubsCount: Double,
    chartSize: Int = 180
) {
    var numberLabel by remember { mutableStateOf("") }
    val treesPercentage = (treesCount / (treesCount + shrubsCount) * 100).toInt()
    val shrubsPercentage = 100 - treesPercentage

    var data by remember {
        mutableStateOf(
            listOf(
                Pie(
                    label = "деревья",
                    data = treesCount,
                    color = AnalyticsBlueChart,
                    selectedColor = AnalyticsBlueChart
                ),
                Pie(
                    label = "кустарники",
                    data = shrubsCount,
                    color = AnalyticsVioletChart,
                    selectedColor = AnalyticsVioletChart
                ),
            )
        )
    }
    ConstraintLayout(
        modifier = modifier
            .background(LightestGrey, RoundedCornerShape(12.dp))
            .padding(24.dp)
    ) {
        val (header, trees, treesDot, shrubs, shrubsDot, chart, labelNum) = createRefs()
        Text(text = "Всего растений", modifier = Modifier.constrainAs(header) {
            top.linkTo(parent.top)
            start.linkTo(parent.start)
        })
        Icon(
            painter = painterResource(R.drawable.ic_dot),
            tint = AnalyticsBlueBar,
            contentDescription = null,
            modifier = Modifier.constrainAs(treesDot) {
                bottom.linkTo(shrubsDot.top, 16.dp)
                start.linkTo(parent.start)
            }
        )
        Text(
            text = "деревья",
            color = AnalyticsBlueBar,
            modifier = Modifier.constrainAs(trees) {
                top.linkTo(treesDot.top)
                bottom.linkTo(treesDot.bottom)
                start.linkTo(treesDot.end, 12.dp)
            })
        Icon(
            painter = painterResource(R.drawable.ic_dot),
            tint = AnalyticsVioletBar,
            contentDescription = null,
            modifier = Modifier.constrainAs(shrubsDot) {
                start.linkTo(parent.start)
                bottom.linkTo(parent.bottom)
            }
        )
        Text(
            text = "кустарники",
            color = AnalyticsVioletBar,
            modifier = Modifier.constrainAs(shrubs) {
                top.linkTo(shrubsDot.top)
                bottom.linkTo(shrubsDot.bottom)
                start.linkTo(shrubsDot.end, 12.dp)
            })
        PieChart(
            modifier = Modifier
                .size(chartSize.dp)
                .constrainAs(chart) {
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                },
            data = data,
            onPieClick = {
                val pieIndex = data.indexOf(it)
                data =
                    data.mapIndexed { mapIndex, pie -> pie.copy(selected = pieIndex == mapIndex) }
                numberLabel = if (pieIndex == 0) "$treesPercentage%" else "$shrubsPercentage%"
            },
            selectedScale = 1.2f,
            scaleAnimEnterSpec = spring<Float>(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            ),
            colorAnimEnterSpec = tween(300),
            colorAnimExitSpec = tween(300),
            scaleAnimExitSpec = tween(300),
            spaceDegreeAnimExitSpec = tween(300),
            style = Pie.Style.Fill,
        )
        Text(text = numberLabel, modifier = Modifier.constrainAs(labelNum) {
            top.linkTo(parent.top)
            end.linkTo(parent.end)
        })
    }
}

@Composable
fun ShrubTypesChart(
    modifier: Modifier = Modifier,
    shrubTypes: Map<String, Int>,
    barWidth: Int = 54
) {
    val data by remember {
        mutableStateOf(
            shrubTypes.map {
                Bars(
                    label = it.key,
                    values = listOf(
                        Bars.Data(
                            value = it.value.toDouble(),
                            color = SolidColor(AnalyticsVioletBar)
                        ),
                    )
                )
            }
        )
    }
    ConstraintLayout(
        modifier = modifier
            .background(AnalyticsLightestViolet, RoundedCornerShape(12.dp))
            .padding(24.dp)
    ) {
        val (header, label, chart) = createRefs()
        Text(
            text = "Виды кустарников",
            color = AnalyticsDarkerViolet,
            modifier = Modifier.constrainAs(header) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
            })
        Text(
            text = "Кол-во",
            modifier = Modifier
                .graphicsLayer {
                    rotationZ = -90f
                }
                .constrainAs(label) {
                    top.linkTo(chart.top)
                    bottom.linkTo(chart.bottom)
                    start.linkTo(header.start)
                },
            fontWeight = FontWeight.SemiBold
        )
        ColumnChart(
            modifier = Modifier
                .heightIn(max = 300.dp)
                .constrainAs(chart) {
                    end.linkTo(parent.end)
                    top.linkTo(header.bottom, 12.dp)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(label.end, 4.dp)
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                },
            data = data,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            ),
            gridProperties = GridProperties(enabled = false),
            barProperties = BarProperties(
                thickness = barWidth.dp,
                cornerRadius = Bars.Data.Radius.Rectangle(8.dp, 8.dp, 8.dp, 8.dp),
            ),
            labelHelperProperties = LabelHelperProperties(enabled = false),
            dividerProperties = DividerProperties(enabled = false)
        )
    }
}

@Composable
fun TreeTypesChart(modifier: Modifier = Modifier, treeTypes: Map<String, Int>, barWidth: Int = 54) {
    val data by remember {
        mutableStateOf(
            treeTypes.map {
                Bars(
                    label = it.key,
                    values = listOf(
                        Bars.Data(
                            value = it.value.toDouble(),
                            color = SolidColor(AnalyticsBlueBar)
                        ),
                    )
                )
            }
        )
    }
    ConstraintLayout(
        modifier = modifier
            .background(AnalyticsLightestBlue, RoundedCornerShape(12.dp))
            .padding(24.dp)
    ) {
        val (header, label, chart) = createRefs()
        Text(
            text = "Виды деревьев",
            color = AnalyticsDarkerBlue,
            modifier = Modifier.constrainAs(header) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
            })
        Text(
            text = "Кол-во",
            modifier = Modifier
                .graphicsLayer {
                    rotationZ = -90f
                }
                .constrainAs(label) {
                    top.linkTo(chart.top)
                    bottom.linkTo(chart.bottom)
                    start.linkTo(header.start)
                },
            fontWeight = FontWeight.SemiBold
        )
        ColumnChart(
            modifier = Modifier
                .heightIn(max = 300.dp)
                .constrainAs(chart) {
                    end.linkTo(parent.end)
                    top.linkTo(header.bottom, 12.dp)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(label.end, 4.dp)
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                },
            data = data,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            ),
            gridProperties = GridProperties(enabled = false),
            barProperties = BarProperties(
                thickness = barWidth.dp,
                cornerRadius = Bars.Data.Radius.Rectangle(8.dp, 8.dp, 8.dp, 8.dp),
            ),
            labelHelperProperties = LabelHelperProperties(enabled = false),
            dividerProperties = DividerProperties(enabled = false)
        )
    }
}

@Composable
fun StatesChart(
    modifier: Modifier = Modifier,
    bad: Int,
    normal: Int,
    good: Int,
    barWidth: Int = 54
) {
    val data by remember {
        mutableStateOf(
            listOf(
                Bars(
                    label = "Хорошо",
                    values = listOf(
                        Bars.Data(
                            value = good.toDouble(),
                            color = SolidColor(StateGreen)
                        ),
                    )
                ),
                Bars(
                    label = "Удовл-но",
                    values = listOf(
                        Bars.Data(
                            value = normal.toDouble(),
                            color = SolidColor(StateYellow)
                        ),
                    )
                ),
                Bars(
                    label = "Плохо",
                    values = listOf(
                        Bars.Data(
                            value = bad.toDouble(),
                            color = SolidColor(StateRed)
                        ),
                    )
                ),
            )
        )
    }

    ConstraintLayout(
        modifier = modifier
            .background(LightestGrey, RoundedCornerShape(12.dp))
            .padding(24.dp)
    ) {
        val (header, label, chart) = createRefs()
        Text(text = "Состояние растений", modifier = Modifier.constrainAs(header) {
            top.linkTo(parent.top)
            start.linkTo(parent.start)
        })
        Text(
            text = "Кол-во",
            modifier = Modifier
                .graphicsLayer {
                    rotationZ = -90f
                }
                .constrainAs(label) {
                    top.linkTo(chart.top)
                    bottom.linkTo(chart.bottom)
                    start.linkTo(header.start)
                },
            fontWeight = FontWeight.SemiBold
        )
        ColumnChart(
            modifier = Modifier
                .heightIn(max = 300.dp)
                .constrainAs(chart) {
                    end.linkTo(parent.end)
                    top.linkTo(header.bottom, 12.dp)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(label.end, 4.dp)
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                },
            data = data,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            ),
            gridProperties = GridProperties(enabled = false),
            barProperties = BarProperties(
                thickness = barWidth.dp,
                cornerRadius = Bars.Data.Radius.Rectangle(8.dp, 8.dp, 8.dp, 8.dp),
            ),
            labelHelperProperties = LabelHelperProperties(enabled = false),
            dividerProperties = DividerProperties(enabled = false)
        )
    }
}