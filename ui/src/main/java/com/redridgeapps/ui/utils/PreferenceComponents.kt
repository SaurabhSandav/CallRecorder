package com.redridgeapps.ui.utils

import androidx.compose.Composable
import androidx.compose.MutableState
import androidx.compose.state
import androidx.ui.core.ConfigurationAmbient
import androidx.ui.core.Modifier
import androidx.ui.core.Text
import androidx.ui.foundation.*
import androidx.ui.graphics.Color
import androidx.ui.layout.*
import androidx.ui.material.*
import androidx.ui.text.TextStyle
import androidx.ui.text.font.FontWeight
import androidx.ui.unit.dp

@Composable
fun TitlePreference(text: String) {

    Text(
        text = text,
        modifier = LayoutPadding(top = 20.dp, start = 10.dp),
        style = TextStyle(color = MaterialTheme.colors().secondary, fontWeight = FontWeight.Bold)
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
            if (checked != null)
                Switch(checked = checked, onCheckedChange = onCheckedChange)
            else
                CircularProgressIndicator()
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
            secondaryText = { Text(secondaryText) },
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
    radioColor: Color = MaterialTheme.colors().secondary,
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
        Column(DrawBackground(Color.White)) {

            // Title

            Box(
                LayoutHeight(64.dp) + LayoutPadding(start = 24.dp),
                gravity = ContentGravity.CenterStart
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography().h6.copy(MaterialTheme.colors().onSurface)
                )
            }

            // Divider

            Divider(color = MaterialTheme.colors().onSurface.copy(alpha = 0.12F))

            // Main content

            val mainContentHeight = ConfigurationAmbient.current.screenHeightDp - 150

            VerticalScroller(
                modifier = LayoutPadding(start = 24.dp) + LayoutHeight.Max(mainContentHeight.dp)
            ) {
                mainContent()
            }

            // Divider

            Divider(color = MaterialTheme.colors().onSurface.copy(alpha = 0.12F))

            // Button Bar

            val buttonList = mutableListOf<ButtonBarButton>()

            if (onNegativeButtonClick != null)
                buttonList.add(ButtonBarButton("CANCEL", onNegativeButtonClick))

            if (onPositiveButtonClick != null)
                buttonList.add(ButtonBarButton("OK", onPositiveButtonClick))

            ButtonBar(LayoutGravity.End, buttonList)
        }
    }
}

@Composable
private fun ButtonBar(
    modifier: Modifier = Modifier.None,
    buttonList: List<ButtonBarButton>
) {

    Row(modifier + LayoutPadding(8.dp)) {

        for (button in buttonList) {

            TextButton(
                modifier = LayoutPadding(start = 8.dp),
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
