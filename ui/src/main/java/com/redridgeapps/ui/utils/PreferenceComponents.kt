package com.redridgeapps.ui.utils

import androidx.compose.Composable
import androidx.compose.MutableState
import androidx.compose.state
import androidx.ui.animation.Crossfade
import androidx.ui.core.Alignment
import androidx.ui.core.ConfigurationAmbient
import androidx.ui.core.Modifier
import androidx.ui.foundation.Box
import androidx.ui.foundation.ContentGravity
import androidx.ui.foundation.Dialog
import androidx.ui.foundation.Text
import androidx.ui.foundation.VerticalScroller
import androidx.ui.foundation.drawBackground
import androidx.ui.graphics.Color
import androidx.ui.layout.Column
import androidx.ui.layout.Row
import androidx.ui.layout.padding
import androidx.ui.layout.preferredHeight
import androidx.ui.layout.preferredHeightIn
import androidx.ui.material.Divider
import androidx.ui.material.ListItem
import androidx.ui.material.MaterialTheme
import androidx.ui.material.RadioGroup
import androidx.ui.material.Switch
import androidx.ui.material.TextButton
import androidx.ui.text.TextStyle
import androidx.ui.text.font.FontWeight
import androidx.ui.unit.dp

@Composable
fun TitlePreference(text: String) {

    Text(
        text = text,
        modifier = Modifier.padding(start = 10.dp, top = 20.dp),
        style = TextStyle(color = MaterialTheme.colors.secondary, fontWeight = FontWeight.Bold)
    )
}

@Composable
fun SwitchPreference(
    text: String,
    checked: Boolean? = null,
    onCheckedChange: (Boolean) -> Unit
) {

    ListItem(
        text = { Text(text) },
        trailing = {
            Crossfade(current = checked) {
                if (it != null)
                    Switch(checked = it, onCheckedChange = onCheckedChange)
            }
        }
    )
}

@Composable
fun <T> SingleSelectListPreference(
    title: String,
    keys: List<T>,
    keyToTextMapper: (T) -> String,
    selectedItem: T? = null,
    onSelectedChange: (T) -> Unit
) {

    val showDialog = state { false }

    // Wrapping ListItem Somehow prevents bugs where other ListItem disappears or
    // this ListItem replaces the other one.
    Box {

        val secondaryText = if (selectedItem == null) "" else keyToTextMapper(selectedItem)

        ListItem(
            text = { Text(title) },
            secondaryText = {
                Crossfade(current = secondaryText) {
                    Text(it)
                }
            },
            onClick = { showDialog.value = true }
        )
    }

    if (showDialog.value) {
        RadioGroupDialogPreference(
            title = title,
            showDialog = showDialog,
            keys = keys,
            keyToTextMapper = keyToTextMapper,
            selectedOption = selectedItem,
            onSelectedChange = onSelectedChange
        )
    }
}

@Composable
private fun <T> RadioGroupDialogPreference(
    title: String,
    showDialog: MutableState<Boolean>,
    keys: List<T>,
    keyToTextMapper: (T) -> String,
    selectedOption: T?,
    onSelectedChange: (T) -> Unit
) {

    DialogPreference(
        title = title,
        onCloseRequest = { showDialog.value = false },
        onPositiveButtonClick = { showDialog.value = false }
    ) {
        KeyedRadioGroup(
            keys = keys,
            keyToTextMapper = keyToTextMapper,
            selectedOption = selectedOption,
            onSelectedChange = onSelectedChange
        )
    }
}

@Composable
private fun <T> KeyedRadioGroup(
    keys: List<T>,
    keyToTextMapper: (T) -> String,
    selectedOption: T?,
    onSelectedChange: (T) -> Unit,
    radioColor: Color = MaterialTheme.colors.secondary,
    textStyle: TextStyle? = null
) {

    RadioGroup {
        Column {
            keys.forEach { key ->
                RadioGroupTextItem(
                    selected = (key == selectedOption),
                    onSelect = { onSelectedChange(key) },
                    text = keyToTextMapper(key),
                    radioColor = radioColor,
                    textStyle = textStyle
                )
            }
        }
    }
}

@Composable
private fun DialogPreference(
    title: String,
    onCloseRequest: () -> Unit,
    onPositiveButtonClick: (() -> Unit)? = null,
    onNegativeButtonClick: (() -> Unit)? = null,
    mainContent: @Composable() () -> Unit
) {
    Dialog(onCloseRequest = onCloseRequest) {
        Column(Modifier.drawBackground(Color.White)) {

            // Title

            Box(
                Modifier.preferredHeight(64.dp).padding(start = 24.dp),
                gravity = ContentGravity.CenterStart
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.h6.copy(MaterialTheme.colors.onSurface)
                )
            }

            // Divider

            Divider(color = MaterialTheme.colors.onSurface.copy(alpha = 0.12F))

            // Main content

            val mainContentHeight = ConfigurationAmbient.current.screenHeightDp - 150

            VerticalScroller(
                modifier = Modifier.padding(start = 24.dp)
                    .preferredHeightIn(maxHeight = mainContentHeight.dp)
            ) {
                mainContent()
            }

            // Divider

            Divider(color = MaterialTheme.colors.onSurface.copy(alpha = 0.12F))

            // Button Bar

            val buttonList = mutableListOf<ButtonBarButton>()

            if (onNegativeButtonClick != null)
                buttonList.add(ButtonBarButton("CANCEL", onNegativeButtonClick))

            if (onPositiveButtonClick != null)
                buttonList.add(ButtonBarButton("OK", onPositiveButtonClick))

            ButtonBar(Modifier.gravity(Alignment.End), buttonList)
        }
    }
}

@Composable
private fun ButtonBar(
    modifier: Modifier = Modifier,
    buttonList: List<ButtonBarButton>
) {

    Row(modifier + Modifier.padding(8.dp)) {

        for (button in buttonList) {

            TextButton(
                modifier = Modifier.padding(start = 8.dp),
                onClick = { button.onClick() }
            ) {
                Text(button.title)
            }
        }
    }
}

data class ButtonBarButton(
    val title: String,
    val onClick: () -> Unit
)
