package de.kitshn.android.ui.component.search.chips

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.SyncAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.kitshn.android.R
import de.kitshn.android.api.tandoor.route.TandoorRecipeQueryParametersSortOrder
import de.kitshn.android.ui.component.search.AdditionalSearchSettingsChipRowState

@Composable
fun SortingSearchSettingChip(
    state: AdditionalSearchSettingsChipRowState
) {
    var showDialog by rememberSaveable { mutableStateOf(false) }

    FilterChip(
        selected = state.sortOrder != null,
        onClick = {
            showDialog = true
        },
        label = { Text(state.sortOrder?.itemLabel() ?: stringResource(R.string.common_sorting)) },
        trailingIcon = {
            Icon(Icons.Rounded.ArrowDropDown, stringResource(R.string.common_select))
        }
    )

    if(showDialog) AlertDialog(
        onDismissRequest = {
            showDialog = false
        },
        icon = {
            Icon(Icons.Rounded.SyncAlt, stringResource(R.string.common_sorting))
        },
        title = {
            Text(text = stringResource(R.string.common_sorting))
        },
        text = {
            Column(
                Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .verticalScroll(rememberScrollState())
            ) {
                TandoorRecipeQueryParametersSortOrder.entries.forEach {
                    ListItem(
                        modifier = Modifier.clickable {
                            showDialog = false
                            state.sortOrder = it

                            state.update()
                        },
                        headlineContent = {
                            Text(text = it.itemLabel())
                        }
                    )
                }
            }
        },
        dismissButton = {
            if(state.sortOrder != null) FilledTonalButton(onClick = {
                showDialog = false
                state.sortOrder = null

                state.update()
            }) {
                Text(text = stringResource(id = R.string.action_remove))
            }
        },
        confirmButton = { }
    )
}