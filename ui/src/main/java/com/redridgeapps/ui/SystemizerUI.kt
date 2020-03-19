package com.redridgeapps.ui

import androidx.compose.Composable
import androidx.compose.Model
import androidx.ui.core.Modifier
import androidx.ui.core.Text
import androidx.ui.foundation.Box
import androidx.ui.foundation.ContentGravity
import androidx.ui.foundation.DrawBackground
import androidx.ui.graphics.Color
import androidx.ui.layout.*
import androidx.ui.material.Button
import androidx.ui.material.CircularProgressIndicator
import androidx.ui.material.MaterialTheme
import androidx.ui.text.TextStyle
import androidx.ui.unit.dp
import androidx.ui.unit.sp
import com.redridgeapps.repository.viewmodel.ISystemizerViewModel
import com.redridgeapps.ui.routing.Destination
import com.redridgeapps.ui.utils.fetchViewModel

@Model
class SystemizerState(
    var refreshing: Boolean = false,
    var isAppSystemized: Boolean = false
)

object SystemizerDestination : Destination {

    @Composable
    override fun initializeUI() {

        val viewModel = fetchViewModel<ISystemizerViewModel>()

        SystemizerUI(viewModel)
    }
}

private val ISystemizerViewModel.systemizerState: SystemizerState
    get() = uiState as SystemizerState

@Composable
private fun SystemizerUI(
    viewModel: ISystemizerViewModel
) {
    Box(DrawBackground(MaterialTheme.colors().primary) + LayoutPadding(20.dp) + LayoutSize.Fill) {
        if (!viewModel.systemizerState.refreshing)
            IsNotInitialized()
        else
            IsInitialized(viewModel)
    }
}

@Composable
private fun IsNotInitialized() {
    Center {
        CircularProgressIndicator(MaterialTheme.colors().secondary)
    }
}

@Composable
private fun IsInitialized(viewModel: ISystemizerViewModel) {
    Column {

        Spacer(LayoutWeight(0.2F))

        ExplanationText(viewModel, LayoutWeight(0.4F))

        Spacer(LayoutWeight(0.2F))

        SystemizationButton(viewModel, LayoutWeight(0.2F))
    }
}

@Composable
private fun ExplanationText(viewModel: ISystemizerViewModel, modifier: Modifier = Modifier.None) {

    val text = when {
        viewModel.systemizerState.isAppSystemized -> "App is a system app."
        else -> "App is not a system app. Call Recording only works with System apps."
    }

    Box(modifier, gravity = ContentGravity.TopStart) {
        Text(text, style = MaterialTheme.typography().h3.copy(Color.White))
    }
}

@Composable
private fun SystemizationButton(
    viewModel: ISystemizerViewModel,
    modifier: Modifier = Modifier.None
) {

    val backgroundColor: Color
    val text: String
    val onClick: () -> Unit

    if (viewModel.systemizerState.isAppSystemized) {
        backgroundColor = Color.Red
        text = "Unsystemize"
        onClick = { viewModel.unSystemize() }
    } else {
        backgroundColor = MaterialTheme.colors().secondary
        text = "Systemize"
        onClick = { viewModel.systemize() }
    }

    Box(LayoutWidth.Fill + modifier, gravity = ContentGravity.Center) {
        Button(onClick, LayoutWidth.Fill, backgroundColor = backgroundColor) {
            Text(text = text, style = TextStyle(fontSize = 25.sp))
        }
    }
}
