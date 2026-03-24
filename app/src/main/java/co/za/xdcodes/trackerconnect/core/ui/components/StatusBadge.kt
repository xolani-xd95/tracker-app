package co.za.xdcodes.trackerconnect.core.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.za.xdcodes.trackerconnect.core.model.ShipmentStatus

@Composable
fun StatusBadge(status: ShipmentStatus) {
    val color = when (status) {
        ShipmentStatus.DELIVERED -> MaterialTheme.colorScheme.tertiary
        ShipmentStatus.OUT_FOR_DELIVERY -> MaterialTheme.colorScheme.secondary
        ShipmentStatus.IN_TRANSIT -> MaterialTheme.colorScheme.primary
        ShipmentStatus.EXCEPTION -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.outline
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.15f),
            contentColor = color
        )
    ) {
        Text(
            text = status.displayName,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}