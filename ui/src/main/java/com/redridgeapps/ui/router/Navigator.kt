package com.redridgeapps.ui.router

import androidx.compose.Composable
import com.redridgeapps.ui.utils.BackPressHandlerAmbient
import com.redridgeapps.ui.utils.BackStackAmbient
import com.redridgeapps.ui.utils.InitializeUI
import com.redridgeapps.ui.utils.WithAmbients

@Composable
fun NewBackStack(rootRoute: Route): BackStack {
    val backStack = BackStack(rootRoute)
    BackPressHandlerAmbient.current.backStack = backStack

    return backStack
}

@Suppress("FunctionName")
fun NavigateTo(backStack: BackStack, route: Route) {
    backStack.push(route)
}

@Composable
fun RouterContent(route: Route) {

    val backStack = NewBackStack(route)

    WithAmbients(BackStackAmbient provides backStack) {

        InitializeUI(backStack.top.uiInitializer)
    }
}


