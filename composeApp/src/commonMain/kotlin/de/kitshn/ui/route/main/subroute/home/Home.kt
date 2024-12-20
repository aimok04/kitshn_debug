package de.kitshn.ui.route.main.subroute.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.SaveAlt
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import coil3.compose.LocalPlatformContext
import de.kitshn.android.homepage.builder.HomePageBuilder
import de.kitshn.api.tandoor.TandoorRequestState
import de.kitshn.api.tandoor.TandoorRequestStateState
import de.kitshn.cache.FoodNameIdMapCache
import de.kitshn.cache.KeywordNameIdMapCache
import de.kitshn.homepage.model.HomePage
import de.kitshn.homepage.model.HomePageSection
import de.kitshn.isScrollingUp
import de.kitshn.ui.component.LoadingGradientWrapper
import de.kitshn.ui.component.alert.LoadingErrorAlertPaneWrapper
import de.kitshn.ui.component.home.HomePageSectionView
import de.kitshn.ui.component.settings.SettingsListItem
import de.kitshn.ui.component.text.DynamicGreetingTitle
import de.kitshn.ui.dialog.recipe.RecipeImportDialog
import de.kitshn.ui.dialog.recipe.creationandedit.RecipeCreationAndEditDialog
import de.kitshn.ui.dialog.recipe.creationandedit.rememberRecipeCreationDialogState
import de.kitshn.ui.dialog.recipe.creationandedit.rememberRecipeEditDialogState
import de.kitshn.ui.dialog.recipe.rememberRecipeImportDialogState
import de.kitshn.ui.layout.KitshnRecipeListRecipeDetailPaneScaffold
import de.kitshn.ui.route.RouteParameters
import de.kitshn.ui.selectionMode.model.RecipeSelectionModeTopAppBar
import de.kitshn.ui.selectionMode.rememberSelectionModeState
import de.kitshn.ui.state.ErrorLoadingSuccessState
import de.kitshn.ui.state.foreverRememberNotSavable
import de.kitshn.ui.state.rememberErrorLoadingSuccessState
import de.kitshn.ui.state.rememberForeverScrollState
import de.kitshn.ui.view.home.search.ViewHomeSearch
import de.kitshn.ui.view.home.search.rememberHomeSearchState
import de.kitshn.ui.view.recipe.details.RecipeServingsAmountSaveMap
import kitshn.composeapp.generated.resources.Res
import kitshn.composeapp.generated.resources.action_add
import kitshn.composeapp.generated.resources.action_import
import kitshn.composeapp.generated.resources.action_show_all_recipes
import kitshn.composeapp.generated.resources.common_search
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteMainSubrouteHome(
    p: RouteParameters
) {
    val coroutineScope = rememberCoroutineScope()

    val context = LocalPlatformContext.current

    var pageLoadingState by rememberErrorLoadingSuccessState()
    val homeSearchState by rememberHomeSearchState(key = "RouteMainSubrouteHome/homeSearch")

    val recipeImportDialogState =
        rememberRecipeImportDialogState(key = "RouteMainSubrouteHome/recipeImportDialogState")

    val recipeCreationDialogState =
        rememberRecipeCreationDialogState(key = "RouteMainSubrouteHome/recipeCreationDialogState")
    val recipeEditDialogState =
        rememberRecipeEditDialogState(key = "RouteMainSubrouteHome/recipeEditDialogState")

    val selectionModeState = rememberSelectionModeState<Int>()

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val scrollState = rememberForeverScrollState(key = "RouteMainSubrouteHome/columnScrollState")

    var homePage by foreverRememberNotSavable<HomePage?>(
        key = "RouteMainSubrouteHome/homePage",
        initialValue = null
    )
    val homePageSectionList = remember { mutableStateListOf<HomePageSection>() }

    LaunchedEffect(Unit) {
        if(p.vm.tandoorClient == null) return@LaunchedEffect
        if(homePage != null) return@LaunchedEffect

        HomePageBuilder(p.vm.tandoorClient!!).apply {
            val keywordNameIdMapCache = KeywordNameIdMapCache(context, client)
            val foodNameIdMapCache = FoodNameIdMapCache(context, client)

            // retrieve keywords and foods to map names to ids
            TandoorRequestState().wrapRequest {
                if(!keywordNameIdMapCache.isValid()) keywordNameIdMapCache.update(coroutineScope)
                if(!foodNameIdMapCache.isValid()) foodNameIdMapCache.update(coroutineScope)
            }

            val requestState = TandoorRequestState()
            requestState.wrapRequest {
                homePage = this.homePage
                build(keywordNameIdMapCache, foodNameIdMapCache)

                pageLoadingState = ErrorLoadingSuccessState.SUCCESS
            }

            if(requestState.state == TandoorRequestStateState.ERROR)
                pageLoadingState = ErrorLoadingSuccessState.ERROR
        }
    }

    LaunchedEffect(homePageSectionList.toList()) {
        if(homePageSectionList.size < 2) return@LaunchedEffect
        pageLoadingState = ErrorLoadingSuccessState.SUCCESS
    }

    LaunchedEffect(homePage?.sectionsStateList?.toList()) {
        if(homePage == null) return@LaunchedEffect

        homePageSectionList.clear()
        homePageSectionList.addAll(homePage!!.sections)

        // remove deleted recipes
        homePageSectionList.forEach { section ->
            section.recipeIds.forEach { if(!p.vm.tandoorClient!!.container.recipeOverview.contains(it)) section.recipeIds.remove(it) }
        }
    }

    KitshnRecipeListRecipeDetailPaneScaffold(
        vm = p.vm,
        key = "RouteMainSubrouteHome",
        topBar = {
            RecipeSelectionModeTopAppBar(
                vm = p.vm,
                topAppBar = {
                    TopAppBar(
                        title = {
                            DynamicGreetingTitle()
                        },
                        actions = {
                            IconButton(onClick = {
                                homeSearchState.open()
                            }) {
                                Icon(Icons.Rounded.Search, stringResource(Res.string.common_search))
                            }
                        },
                        colors = it,
                        scrollBehavior = scrollBehavior
                    )
                },
                state = selectionModeState
            )
        },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Box(
                    Modifier.size(56.dp),
                    contentAlignment = Alignment.Center
                ) {
                    SmallFloatingActionButton(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        onClick = { recipeImportDialogState.open() }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.SaveAlt,
                            contentDescription = stringResource(Res.string.action_import)
                        )
                    }
                }

                ExtendedFloatingActionButton(
                    expanded = scrollState.isScrollingUp(),
                    icon = { Icon(Icons.Rounded.Add, stringResource(Res.string.action_add)) },
                    text = { Text(stringResource(Res.string.action_add)) },
                    onClick = { recipeCreationDialogState.open() }
                )
            }
        },
        onClickKeyword = {
            coroutineScope.launch {
                homeSearchState.reopen {
                    homeSearchState.openWithKeyword(p.vm.tandoorClient!!, it)
                }
            }
        }
    ) { pv, value, supportsMultiplePages, background, onSelect ->
        // handle recipe passing
        p.vm.uiState.viewRecipe.WatchAndConsume {
            onSelect(it.toString())
        }

        ViewHomeSearch(
            vm = p.vm,
            state = homeSearchState,

            handleBack = supportsMultiplePages && value != null,
            onBack = {
                // needed for back gesture to work correctly in search view
                onSelect(null)
            }
        ) {
            onSelect(it.id.toString())
        }

        LoadingErrorAlertPaneWrapper(loadingState = pageLoadingState) {
            LoadingGradientWrapper(
                Modifier
                    .padding(pv)
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
                loadingState = pageLoadingState,
                backgroundColor = background
            ) {
                Column(
                    modifier = Modifier.verticalScroll(
                        enabled = pageLoadingState != ErrorLoadingSuccessState.LOADING,
                        state = scrollState
                    )
                ) {
                    Spacer(Modifier.height(16.dp))

                    p.vm.tandoorClient?.let {
                        RouteMainSubrouteHomeMealPlanPromotionSection(
                            client = it,
                            loadingState = pageLoadingState
                        ) { recipeOverview, servings ->
                            RecipeServingsAmountSaveMap[recipeOverview.id] = servings.roundToInt()
                            onSelect(recipeOverview.id.toString())
                        }
                    }

                    if(homePageSectionList.size == 0 && pageLoadingState != ErrorLoadingSuccessState.SUCCESS) {
                        repeat(5) {
                            HomePageSectionView(
                                client = p.vm.tandoorClient,
                                loadingState = pageLoadingState,
                                onClickKeyword = {
                                    coroutineScope.launch {
                                        homeSearchState.openWithKeyword(p.vm.tandoorClient!!, it)
                                    }
                                }
                            ) { }
                        }
                    } else {
                        for(section in homePageSectionList) HomePageSectionView(
                            client = p.vm.tandoorClient,
                            section = section,
                            loadingState = pageLoadingState,
                            selectionState = selectionModeState,
                            onClickKeyword = {
                                coroutineScope.launch {
                                    homeSearchState.openWithKeyword(p.vm.tandoorClient!!, it)
                                }
                            }
                        ) {
                            onSelect(it.id.toString())
                        }
                    }

                    SettingsListItem(
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 16.dp
                        ),
                        label = { Text(text = stringResource(Res.string.action_show_all_recipes)) },
                        description = { },
                        icon = Icons.AutoMirrored.Rounded.List,
                        contentDescription = "",
                        alternativeColors = supportsMultiplePages,
                        onClick = {
                            p.vm.mainSubNavHostController?.navigate("list")
                        }
                    )
                }
            }
        }
    }

    RecipeImportDialog(
        vm = p.vm,
        state = recipeImportDialogState,
        onViewRecipe = { p.vm.viewRecipe(it.id) }
    )

    if(p.vm.tandoorClient != null) {
        val ingredientsShowFractionalValues =
            p.vm.settings.getIngredientsShowFractionalValues.collectAsState(initial = true)

        RecipeCreationAndEditDialog(
            client = p.vm.tandoorClient!!,
            creationState = recipeCreationDialogState,
            editState = recipeEditDialogState,
            showFractionalValues = ingredientsShowFractionalValues.value,
            onRefresh = { },
            onViewRecipe = { p.vm.viewRecipe(it.id) }
        )
    }
}