package com.redridgeapps.ui.routing

import com.koduok.compose.navigation.core.backStackController

fun composeHandleBackPressed(): Boolean = backStackController.pop()
