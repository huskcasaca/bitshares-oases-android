package kdenticon

fun colorTheme(hue: Float, config: Config) : List<String> {
    return listOf(
        // Dark gray
        Color.hsl(0f, 0f, config.grayscaleLightness(-0.2f)),
        // Mid color
        Color.correctedHsl(hue, config.saturation, config.colorLightness(0.3f)),
        // Light gray
        Color.hsl(0f, 0f, config.grayscaleLightness(0.8f)),
        // Light color
        Color.correctedHsl(hue, config.saturation, config.colorLightness(0.8f)),
        // Dark color
        Color.correctedHsl(hue, config.saturation, config.colorLightness(-0.2f))
    )
}