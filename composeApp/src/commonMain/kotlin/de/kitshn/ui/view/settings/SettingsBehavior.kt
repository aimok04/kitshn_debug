package de.kitshn.ui.view.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DynamicFeed
import androidx.compose.material.icons.rounded.Numbers
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import de.kitshn.Platforms
import de.kitshn.platformDetails
import de.kitshn.ui.component.buttons.BackButton
import de.kitshn.ui.component.settings.SettingsSwitchListItem
import de.kitshn.ui.view.ViewParameters
import kitshn.composeapp.generated.resources.Res
import kitshn.composeapp.generated.resources.settings_section_behavior_enable_dynamic_home_screen_description
import kitshn.composeapp.generated.resources.settings_section_behavior_enable_dynamic_home_screen_label
import kitshn.composeapp.generated.resources.settings_section_behavior_hide_funding_banner_this_year_description
import kitshn.composeapp.generated.resources.settings_section_behavior_hide_funding_banner_this_year_label
import kitshn.composeapp.generated.resources.settings_section_behavior_hide_ingredient_allocation_action_chip_description
import kitshn.composeapp.generated.resources.settings_section_behavior_hide_ingredient_allocation_action_chip_label
import kitshn.composeapp.generated.resources.settings_section_behavior_ingredients_show_fractional_values_description
import kitshn.composeapp.generated.resources.settings_section_behavior_ingredients_show_fractional_values_label
import kitshn.composeapp.generated.resources.settings_section_behavior_label
import kitshn.composeapp.generated.resources.settings_section_behavior_properties_show_fractional_values_description
import kitshn.composeapp.generated.resources.settings_section_behavior_properties_show_fractional_values_label
import kitshn.composeapp.generated.resources.settings_section_behavior_use_share_wrapper_description
import kitshn.composeapp.generated.resources.settings_section_behavior_use_share_wrapper_label
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewSettingsBehavior(
    p: ViewParameters
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(state = rememberTopAppBarState())

    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = { BackButton(p.back) },
                title = { Text(stringResource(Res.string.settings_section_behavior_label)) },
                scrollBehavior = scrollBehavior
            )
        }
    ) {
        val useShareWrapper = p.vm.settings.getUseShareWrapper.collectAsState(initial = true)
        val hideIngredientAllocationActionChips =
            p.vm.settings.getHideIngredientAllocationActionChips.collectAsState(initial = false)

        val enableDynamicHomeScreen =
            p.vm.settings.getEnableDynamicHomeScreen.collectAsState(initial = true)

        val ingredientsShowFractionalValues =
            p.vm.settings.getIngredientsShowFractionalValues.collectAsState(initial = true)
        val propertiesShowFractionalValues =
            p.vm.settings.getPropertiesShowFractionalValues.collectAsState(initial = true)

        val fundingBannerHideUntil =
            p.vm.settings.getFundingBannerHideUntil.collectAsState(initial = -1L)

        LazyColumn(
            modifier = Modifier
                .padding(it)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            prependItems()

            item {
                SettingsSwitchListItem(
                    label = { Text(stringResource(Res.string.settings_section_behavior_use_share_wrapper_label)) },
                    description = { Text(stringResource(Res.string.settings_section_behavior_use_share_wrapper_description)) },
                    icon = Icons.Rounded.Share,
                    contentDescription = stringResource(Res.string.settings_section_behavior_use_share_wrapper_label),
                    checked = useShareWrapper.value
                ) {
                    coroutineScope.launch {
                        p.vm.settings.setUseShareWrapper(it)
                    }
                }
            }

            item {
                SettingsSwitchListItem(
                    label = { Text(stringResource(Res.string.settings_section_behavior_hide_ingredient_allocation_action_chip_label)) },
                    description = { Text(stringResource(Res.string.settings_section_behavior_hide_ingredient_allocation_action_chip_description)) },
                    icon = Icons.Rounded.WarningAmber,
                    contentDescription = stringResource(Res.string.settings_section_behavior_hide_ingredient_allocation_action_chip_label),
                    checked = hideIngredientAllocationActionChips.value
                ) {
                    coroutineScope.launch {
                        p.vm.settings.setHideIngredientAllocationActionChips(it)
                    }
                }
            }

            item {
                HorizontalDivider(
                    Modifier.padding(top = 8.dp, bottom = 8.dp)
                )
            }

            item {
                SettingsSwitchListItem(
                    label = { Text(stringResource(Res.string.settings_section_behavior_enable_dynamic_home_screen_label)) },
                    description = { Text(stringResource(Res.string.settings_section_behavior_enable_dynamic_home_screen_description)) },
                    icon = Icons.Rounded.DynamicFeed,
                    contentDescription = stringResource(Res.string.settings_section_behavior_enable_dynamic_home_screen_label),
                    checked = enableDynamicHomeScreen.value
                ) {
                    coroutineScope.launch {
                        p.vm.settings.setEnableDynamicHomeScreen(it)
                    }
                }
            }

            item {
                HorizontalDivider(
                    Modifier.padding(top = 8.dp, bottom = 8.dp)
                )
            }

            item {
                SettingsSwitchListItem(
                    label = { Text(stringResource(Res.string.settings_section_behavior_ingredients_show_fractional_values_label)) },
                    description = { Text(stringResource(Res.string.settings_section_behavior_ingredients_show_fractional_values_description)) },
                    icon = Icons.Rounded.Numbers,
                    contentDescription = stringResource(Res.string.settings_section_behavior_ingredients_show_fractional_values_label),
                    checked = ingredientsShowFractionalValues.value
                ) {
                    coroutineScope.launch {
                        p.vm.settings.setIngredientsShowFractionalValues(it)
                    }
                }
            }

            item {
                SettingsSwitchListItem(
                    label = { Text(stringResource(Res.string.settings_section_behavior_properties_show_fractional_values_label)) },
                    description = { Text(stringResource(Res.string.settings_section_behavior_properties_show_fractional_values_description)) },
                    icon = Icons.Rounded.Numbers,
                    contentDescription = stringResource(Res.string.settings_section_behavior_properties_show_fractional_values_label),
                    checked = propertiesShowFractionalValues.value
                ) {
                    coroutineScope.launch {
                        p.vm.settings.setPropertiesShowFractionalValues(it)
                    }
                }
            }

            if(platformDetails.platform == Platforms.IOS) {
                item {
                    HorizontalDivider(
                        Modifier.padding(top = 8.dp, bottom = 8.dp)
                    )
                }

                item {
                    var enabled by remember { mutableStateOf(false) }
                    LaunchedEffect(fundingBannerHideUntil.value) {
                        val currentYear = Clock.System.now()
                            .toLocalDateTime(TimeZone.currentSystemDefault())
                            .year
                        val year = Instant.fromEpochSeconds(fundingBannerHideUntil.value)
                            .toLocalDateTime(TimeZone.currentSystemDefault())
                            .year

                        enabled = currentYear + 1 == year
                    }

                    SettingsSwitchListItem(
                        label = { Text(stringResource(Res.string.settings_section_behavior_hide_funding_banner_this_year_label)) },
                        description = { Text(stringResource(Res.string.settings_section_behavior_hide_funding_banner_this_year_description)) },
                        icon = Icons.Rounded.VisibilityOff,
                        contentDescription = stringResource(Res.string.settings_section_behavior_hide_funding_banner_this_year_description),
                        checked = enabled
                    ) {
                        coroutineScope.launch {
                            if(it) {
                                val currentYear = Clock.System.now()
                                    .toLocalDateTime(TimeZone.currentSystemDefault())
                                    .year

                                // set funding banner hide until to next year
                                p.vm.settings.setFundingBannerHideUntil(
                                    LocalDateTime(currentYear + 1, 1, 2, 0, 0, 0)
                                        .toInstant(TimeZone.currentSystemDefault())
                                        .epochSeconds
                                )
                            } else {
                                p.vm.settings.setFundingBannerHideUntil(-1)
                            }
                        }
                    }
                }
            }
        }
    }
}

expect fun LazyListScope.prependItems()