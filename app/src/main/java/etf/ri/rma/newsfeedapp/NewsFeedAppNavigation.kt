import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import etf.ri.rma.newsfeedapp.screen.FilterScreen
import etf.ri.rma.newsfeedapp.screen.NewsDetailsScreen
import etf.ri.rma.newsfeedapp.screen.NewsFeedScreen

@Composable
fun NewsFeedAppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            NewsFeedScreen(navController = navController)
        }
        composable("filters") {
            FilterScreen(navController = navController)
        }
        composable("details/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: return@composable
            NewsDetailsScreen(
                newsId = id,
                onClose = { navController.popBackStack("home", false) },
                onRelatedNewsSelected = {
                    navController.popBackStack()
                    navController.navigate("details/$it") }
            )
        }
    }
}
