package com.example.gestiondearriendos.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val EsquemaClaro = lightColorScheme(
    primary = VerdeBosque,
    onPrimary = BeigeClaro,
    primaryContainer = VerdeNiebla,
    onPrimaryContainer = VerdeBosqueOscuro,

    secondary = Ambar,
    onSecondary = TextoOscuro,
    secondaryContainer = AmbarSuave,
    onSecondaryContainer = AmbarOscuro,

    tertiary = VerdeBosqueClaro,
    onTertiary = BeigeClaro,

    background = BeigeFondo,
    onBackground = TextoOscuro,
    surface = BeigeClaro,
    onSurface = TextoOscuro,
    surfaceVariant = BeigeMedio,
    onSurfaceVariant = TextoSecundario,
    surfaceContainerLowest = BeigeClaro,
    surfaceContainerLow = BeigeClaro,
    surfaceContainer = BeigeClaro,
    surfaceContainerHigh = BeigeMedio,
    surfaceContainerHighest = BeigeMedio,

    outline = BeigeArena,
    outlineVariant = BeigeMedio,

    error = Terracota,
    onError = BeigeClaro,
    errorContainer = TerracotaSuave,
    onErrorContainer = Terracota,

    // Usados por el Snackbar
    inverseSurface = VerdeBosqueOscuro,
    inverseOnSurface = BeigeClaro,
    inversePrimary = AmbarClaro
)

private val EsquemaOscuro = darkColorScheme(
    primary = VerdeSalvia,
    onPrimary = VerdeBosqueOscuro,
    primaryContainer = VerdeBosque,
    onPrimaryContainer = VerdeNiebla,

    secondary = AmbarClaro,
    onSecondary = TextoOscuro,
    secondaryContainer = AmbarOscuro,
    onSecondaryContainer = AmbarSuave,

    tertiary = VerdeSalvia,
    onTertiary = VerdeBosqueOscuro,

    background = FondoNoche,
    onBackground = BeigeMedio,
    surface = SuperficieNoche,
    onSurface = BeigeMedio,
    surfaceVariant = SuperficieNocheAlta,
    onSurfaceVariant = BeigeArena,
    surfaceContainerLowest = FondoNoche,
    surfaceContainerLow = SuperficieNoche,
    surfaceContainer = SuperficieNoche,
    surfaceContainerHigh = SuperficieNocheAlta,
    surfaceContainerHighest = SuperficieNocheAlta,

    outline = TextoSecundario,
    outlineVariant = SuperficieNocheAlta,

    error = TerracotaSuave,
    onError = Terracota,

    inverseSurface = BeigeMedio,
    inverseOnSurface = VerdeBosqueOscuro,
    inversePrimary = VerdeBosque
)

private val Formas = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun GestionDeArriendosTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Ya NO usamos "dynamic color": en Android 12+ ese modo reemplazaba
    // nuestros colores por los del fondo de pantalla del teléfono.
    val colorScheme = if (darkTheme) EsquemaOscuro else EsquemaClaro

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Formas,
        content = content
    )
}
