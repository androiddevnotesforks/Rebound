/*
 * Copyright (c) 2022 Ankit Suda.
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package com.ankitsuda.rebound.ui.history

import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import me.onebone.toolbar.CollapsingToolbarScaffold
import me.onebone.toolbar.rememberCollapsingToolbarScaffoldState
import com.google.accompanist.insets.statusBarsHeight
import timber.log.Timber
import java.util.*
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ankitsuda.base.util.CalendarDate
import com.ankitsuda.navigation.LeafScreen
import com.ankitsuda.navigation.LocalNavigator
import com.ankitsuda.navigation.Navigator
import com.ankitsuda.rebound.ui.components.*
import com.ankitsuda.rebound.ui.history.components.HistorySessionItemCard
import com.ankitsuda.rebound.ui.history.components.WeekDay
import me.onebone.toolbar.ScrollStrategy
import java.text.SimpleDateFormat
import kotlin.random.Random

@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
@Composable
fun HistoryScreen(
    navController: NavController,
    navigator: Navigator = LocalNavigator.current,
    viewModel: HistoryScreenViewModel = hiltViewModel()
) {
    val argumentsDate = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getLiveData<Long>("date")?.observeAsState()

    var date = CalendarDate.today.date

    argumentsDate?.value?.let {
        date = Date(it)
    }

    val calendar = Calendar.getInstance().apply {
        this.time = date
        this.set(Calendar.HOUR_OF_DAY, 0)
        this.set(Calendar.MINUTE, 0)
        this.set(Calendar.SECOND, 0)
        this.set(Calendar.MILLISECOND, 0)
    }

    val isSameYear = calendar.get(Calendar.YEAR) == CalendarDate.today.year
    val isToday = calendar.time == CalendarDate.today.date
    Timber.d(date.toString())

    val dayFormatter =
        SimpleDateFormat(if (isSameYear) "EEE, MMM d" else "MMM d, yyyy", Locale.getDefault())

    val collapsingState = rememberCollapsingToolbarScaffoldState()

    val week = viewModel.week
    var today = viewModel.today

    LaunchedEffect(key1 = Unit) {
        if (week.isEmpty()) {
            viewModel.getCurrentWeek()
        }
    }

    val workouts by viewModel.getWorkoutsOnDate(date)
        .collectAsState(initial = emptyList())

    Timber.d("workouts $workouts")

    val isWeekHeaderVisible = week.any { it == date }

    Surface() {
        // History

        Column() {
            Box(
                modifier = Modifier
                    .statusBarsHeight()
                    .fillMaxWidth()
                    .zIndex(2f)
                    .background(MaterialTheme.colors.background)
            )

            CollapsingToolbarScaffold(
                scrollStrategy = ScrollStrategy.EnterAlwaysCollapsed,
                state = collapsingState,
                toolbar = {
                    TopBar2(
                        title = if (isToday) "Today" else dayFormatter.format(date),
                        toolbarState = collapsingState.toolbarState,
                        elevationEnabled = false,
                        statusBarEnabled = false,
                        navigationIcon = {
                            TopBarIconButton(
                                icon = Icons.Outlined.DateRange,
                                title = "Show calendar",
                                onClick = {
//                                    navController.navigate(Route.Calendar.createRoute(selectedDate = date))
                                    navigator.navigate(LeafScreen.Calendar.createRoute(selectedDate = date))
                                }
                            )
                        },
                        actions = {
                            TopBarIconButton(
                                icon = Icons.Outlined.MoreVert,
                                title = "Open menu",
                                onClick = {

                                }
                            )
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.background)
            ) {


                // User routines
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colors.background),
                ) {
                    // Sticky Calendar

                    if (isWeekHeaderVisible) {
                        stickyHeader {
                            // For testing only

                            Card(
                                shape = RoundedCornerShape(0),
                                elevation = 2.dp,
                                modifier = Modifier.fillMaxWidth()
                            ) {

                                Row(
                                    modifier = Modifier
                                        .background(MaterialTheme.colors.background)
//                                        .padding(start = 8.dp, end = 8.dp)
                                ) {
                                    for (day in week) {
                                        WeekDay(
                                            modifier = Modifier.weight(1f / 7f),
                                            day = day,
                                            isSelected = day == date,
                                            onClick = {
                                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                                    "date",
                                                    day.time
                                                )
                                            }
                                        )
                                    }
                                }


                            }

                        }
                    }


                    items(workouts.size) {
                        val workout = workouts[it]
                        HistorySessionItemCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    top = if (it == 0) 16.dp else 0.dp,
                                    start = 16.dp,
                                    end = 16.dp,
                                    bottom = 16.dp
                                ),
                            onClick = {
                                navigator.navigate(
                                    LeafScreen.Session.createRoute(
                                        workoutId = workout.id
                                    )
                                )
                            },
                            title = workout.name.toString(),
                            totalExercises = 7,
                            time = "45 m", volume = "1000 kg", prs = 2
                        )
                    }

                }

            }
        }

    }

}