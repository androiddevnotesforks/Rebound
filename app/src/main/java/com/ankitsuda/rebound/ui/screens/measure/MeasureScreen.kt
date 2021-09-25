package com.ankitsuda.rebound.ui.screens.measure

import android.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BubbleChart
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ankitsuda.rebound.ui.Route
import com.ankitsuda.rebound.ui.components.MoreItemCard
import com.ankitsuda.rebound.ui.components.MoreSectionHeader
import com.ankitsuda.rebound.ui.components.TopBar
import com.ankitsuda.rebound.ui.components.TopBarBackIconButton
import com.ankitsuda.rebound.ui.components.collapsing_toolbar.CollapsingToolbarScaffold
import com.ankitsuda.rebound.ui.components.collapsing_toolbar.rememberCollapsingToolbarScaffoldState

@Composable
fun MeasureScreen(navController: NavHostController) {
    val collapsingState = rememberCollapsingToolbarScaffoldState()

    val core = arrayListOf("Weight", "Body fat percentage", "Calorie intake")
    val body = arrayListOf(
        "Neck",
        "Shoulders",
        "Chest",
        "Left bicep",
        "Right bicep",
        "Left forearm",
        "Right forearm",
        "Upper abs",
        "Waist",
        "Lower abs",
        "Hips",
        "Left thigh",
        "Right thigh",
        "Left calf",
        "Right calf",
    )

    CollapsingToolbarScaffold(
        state = collapsingState,
        toolbar = {
            TopBar(title = "Measure", strictLeftIconAlignToStart = true,leftIconBtn = {
                TopBarBackIconButton {
                    navController.popBackStack()
                }
            })
        },
        modifier = Modifier.background(MaterialTheme.colors.background)
    ) {

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp)
        ) {
            item {
                MoreSectionHeader(text = "Core")
            }
            items(core.size) {
                val part = core[it]
                MoreItemCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    text = part,
                    onClick = {
                        navController.navigate(Route.PartMeasurements.createRoute(it.toLong()))
                    })
            }

            item {
                MoreSectionHeader(text = "Body")
            }
            items(body.size) {
                val part = body[it]
                MoreItemCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    text = part,
                    onClick = {
                        navController.navigate(Route.PartMeasurements.createRoute((it + core.size).toLong()))
                    })
            }


        }
    }
}