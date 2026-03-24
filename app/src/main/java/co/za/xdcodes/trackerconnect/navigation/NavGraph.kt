package co.za.xdcodes.trackerconnect.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import co.za.xdcodes.trackerconnect.feature.details.ShipmentDetailsScreen
import co.za.xdcodes.trackerconnect.feature.shipments.ShipmentsScreen
import co.za.xdcodes.trackerconnect.navigation.Routes.Shipments
import co.za.xdcodes.trackerconnect.navigation.Routes.ShipmentDetails

@Composable
fun NavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Shipments
    ) {
        composable<Shipments> {
            ShipmentsScreen { shipmentId ->
                navController.navigate(ShipmentDetails(shipmentId))
            }
        }

        composable<ShipmentDetails> {
            ShipmentDetailsScreen {
                navController.navigateUp()
            }
        }
    }
}
