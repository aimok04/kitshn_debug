package de.kitshn.android.ui.route.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import de.kitshn.android.KitshnViewModel
import de.kitshn.android.ui.route.RouteParameters
import de.kitshn.android.ui.route.routes

@Composable
fun PrimaryNavigation(
    vm: KitshnViewModel
) {
    val controller = rememberNavController()
    vm.navHostController = controller

    NavHost(
        modifier = Modifier.fillMaxSize(),
        navController = controller,
        startDestination = "main"
    ) {
        routes.forEach { route ->
            composable(
                route = route.route,
                arguments = route.arguments,
                deepLinks = route.deepLinks,
                content = {
                    val p = RouteParameters(vm, it, { controller.popBackStack() })
                    route.content(this, p)
                },
                enterTransition = route.animation.enterTransition,
                exitTransition = route.animation.exitTransition,
                popEnterTransition = route.animation.popEnterTransition,
                popExitTransition = route.animation.popExitTransition
            )
        }
    }
}