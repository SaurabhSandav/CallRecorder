package com.redridgeapps.ui

import androidx.compose.Composable
import androidx.compose.MutableState
import androidx.compose.state
import androidx.ui.core.Alignment
import androidx.ui.core.Text
import androidx.ui.foundation.DrawBackground
import androidx.ui.graphics.Color
import androidx.ui.layout.*
import androidx.ui.material.Button
import androidx.ui.material.CircularProgressIndicator
import androidx.ui.material.MaterialTheme
import androidx.ui.text.TextStyle
import androidx.ui.unit.dp
import androidx.ui.unit.sp
import com.redridgeapps.repository.ISystemizer
import com.redridgeapps.ui.initialization.Route
import com.redridgeapps.ui.initialization.UIInitializer
import javax.inject.Inject

object SystemizerRoute : Route {

    override val uiInitializer = SystemizerUIInitializer::class.java
}

class SystemizerUIInitializer @Inject constructor(
    private val systemizer: ISystemizer
) : UIInitializer {

    @Composable
    override fun initialize() {
        SystemizerUI(systemizer)
    }
}

@Composable
fun SystemizerUI(systemizer: ISystemizer) {
    Column(DrawBackground(MaterialTheme.colors().primary) + LayoutPadding(20.dp)) {
        val isSystemized = state { checkIsSystemized(systemizer) }

        ExplanationText(isSystemized)

        Spacer(LayoutHeight(40.dp))

        SystemizationButton(systemizer, isSystemized)
    }
}

@Composable
fun ColumnScope.ExplanationText(isSystemized: MutableState<Boolean>) {
    val text = if (isSystemized.value) "App is Systemized."
    else "App is not a system app. Call Recording only works with System apps."

    Container(LayoutFlexible(0.8F), alignment = Alignment.Center) {
        Text(text, style = MaterialTheme.typography().h3.copy(Color.White))
    }
}

@Composable
fun ColumnScope.SystemizationButton(systemizer: ISystemizer, isSystemized: MutableState<Boolean>) {

    val backgroundColor: Color
    val text: String

    var inProgress by state { false }

    if (isSystemized.value) {
        backgroundColor = Color.Red
        text = "Unsystemize"
    } else {
        backgroundColor = MaterialTheme.colors().secondary
        text = "Systemize"
    }

    val onClick = {
        inProgress = true
        if (!isSystemized.value) {
            systemizer.systemize {
                isSystemized.value = checkIsSystemized(systemizer)
                inProgress = false
            }
        } else {
            systemizer.unSystemize {
                isSystemized.value = checkIsSystemized(systemizer)
                inProgress = false
            }
        }
    }

    Container(LayoutFlexible(0.2F) + LayoutWidth.Fill, alignment = Alignment.Center) {
        if (!inProgress) {
            Button(LayoutWidth.Fill, backgroundColor = backgroundColor, onClick = onClick) {
                Text(text = text, style = TextStyle(fontSize = 25.sp))
            }
        } else {
            CircularProgressIndicator(MaterialTheme.colors().secondary)
        }
    }
}

fun checkIsSystemized(systemizer: ISystemizer) = systemizer.isAppSystemized()
