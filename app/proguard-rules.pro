# Keep our application
-keep class com.example.shakkiappi.** { *; }

# Keep Compose
-keep class androidx.compose.** { *; }
-keep class androidx.navigation.** { *; }

# Keep annotations
-keepattributes *Annotation*
-keepclassmembers class ** {
    @androidx.compose.runtime.Composable *;
}

# Keep ViewModel
-keep class * extends androidx.lifecycle.ViewModel { *; }

# Remove debug logs in release
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}
