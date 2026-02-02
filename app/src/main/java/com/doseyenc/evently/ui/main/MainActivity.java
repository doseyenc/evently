package com.doseyenc.evently.ui.main;

import android.os.Bundle;
import android.util.TypedValue;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.doseyenc.evently.R;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        applyStatusBarFromTheme();
    }

    /**
     * Apply status bar color and light/dark icons from the current theme.
     * Required because DrawerLayout with fitsSystemWindows on the event list can prevent
     * the theme's status bar color from being applied; setting it here keeps both
     * event list and event detail screens consistent.
     */
    private void applyStatusBarFromTheme() {
        TypedValue value = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.statusBarColor, value, true)) {
            getWindow().setStatusBarColor(value.data);
        }
        if (getTheme().resolveAttribute(android.R.attr.windowLightStatusBar, value, true)
                && value.type == TypedValue.TYPE_INT_BOOLEAN) {
            WindowInsetsControllerCompat insetsController =
                    WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
            insetsController.setAppearanceLightStatusBars(value.data != 0);
        }
    }
}
