package com.redridgeapps.ui

import androidx.compose.Composable
import androidx.compose.Model
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
import com.redridgeapps.repository.viewmodel.ISystemizerViewModel
import com.redridgeapps.ui.initialization.Destination
import com.redridgeapps.ui.utils.fetchViewModel

@Model
class SystemizerUIModel(
    var refreshing: Boolean = false,
    var isAppSystemized: Boolean = false
)

object SystemizerDestination : Destination {

    @Composable
    override fun initializeUI() {

        val viewModel = fetchViewModel<ISystemizerViewModel>()
        val model = viewModel.model as SystemizerUIModel

        SystemizerUI(viewModel, model)
    }
}

@Composable
fun SystemizerUI(
    viewModel: ISystemizerViewModel,
    model: SystemizerUIModel
) {
    Container(DrawBackground(MaterialTheme.colors().primary) + LayoutPadding(20.dp)) {
        if (!model.refreshing)
            IsNotInitialized()
        else
            IsInitialized(viewModel, model)
    }
}

@Composable
private fun IsNotInitialized() {
    Center {
        CircularProgressIndicator(MaterialTheme.colors().secondary)
    }
}

@Composable
private fun IsInitialized(viewModel: ISystemizerViewModel, model: SystemizerUIModel) {
    Column {
        ExplanationText(model)

        Spacer(LayoutHeight(40.dp))

        SystemizationButton(viewModel, model)
    }
}

@Composable
fun ColumnScope.ExplanationText(model: SystemizerUIModel) {
    val text = if (model.isAppSystemized) "App is Systemized."
    else "App is not a system app. Call Recording only works with System apps."

    Container(LayoutFlexible(0.8F), alignment = Alignment.Center) {
        Text(text, style = MaterialTheme.typography().h3.copy(Color.White))
    }
}

@Composable
fun ColumnScope.SystemizationButton(
    viewModel: ISystemizerViewModel,
    model: SystemizerUIModel
) {

    val backgroundColor: Color
    val text: String

    if (model.isAppSystemized) {
        backgroundColor = Color.Red
        text = "Unsystemize"
    } else {
        backgroundColor = MaterialTheme.colors().secondary
        text = "Systemize"
    }

    val onClick = {
        if (!model.isAppSystemized) {
            viewModel.systemize()
        } else {
            viewModel.unSystemize()
        }
    }

    Container(LayoutFlexible(0.2F) + LayoutWidth.Fill, alignment = Alignment.Center) {
        Button(LayoutWidth.Fill, backgroundColor = backgroundColor, onClick = onClick) {
            Text(text = text, style = TextStyle(fontSize = 25.sp))
        }
    }
}
