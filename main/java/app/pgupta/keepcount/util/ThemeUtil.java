package app.pgupta.keepcount.util;

import app.pgupta.keepcount.R;

/**
 * Created by Home on 7/10/17.
 */

public class ThemeUtil {

    public static int theme_background_resource;

    public static void setTheme(String themeName) {
        if (themeName.equals(Constants.THEME_DEFAULT)) {
            theme_background_resource = R.drawable.primary_gradient;
            //setTheme(R.style.KeepCountTheme);
        } else if (themeName.equals(Constants.THEME_AQUASPLASH)) {
            theme_background_resource = R.drawable.gradient_aquasplash;
           // setTheme(R.style.AquaSplashTheme);
        } else if (themeName.equals(Constants.THEME_MORPHEUS)) {
            theme_background_resource = R.drawable.gradient_morpheus;
            //setTheme(R.style.MorpheusTheme);
        } else if (themeName.equals(Constants.THEME_PALOALTO)) {
            theme_background_resource = R.drawable.gradient_paloalto;
            //setTheme(R.style.PaloAltoTheme);
        } else if (themeName.equals(Constants.THEME_RIPE)) {
            theme_background_resource = R.drawable.gradient_ripe;
            //setTheme(R.style.RipeTheme);
        } else if (themeName.equals(Constants.THEME_SUNNY)) {
            theme_background_resource = R.drawable.gradient_sunny;
            //setTheme(R.style.SunnyTheme);
        } else if (themeName.equals(Constants.THEME_TURBOSCENT)) {
            theme_background_resource = R.drawable.gradient_turboscent;
            //setTheme(R.style.TurboscentTheme);
        } else if (themeName.equals(Constants.THEME_DARK)) {
            theme_background_resource = R.drawable.gradient_dark;
            //setTheme(R.style.DarkTheme);
        } else if (themeName.equals(Constants.THEME_COCKTAIL)) {
            theme_background_resource = R.drawable.gradient_cocktail;
            // setTheme(R.style.CocktailTheme);
        } else {
            theme_background_resource = R.drawable.primary_gradient;
            //setTheme(R.style.KeepCountTheme);
        }
    }
}
