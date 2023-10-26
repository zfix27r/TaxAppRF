plugins {
    val gradleVersion: String by System.getProperties()

    val kotlinVersion: String by System.getProperties()
    val kotlinCoroutinesVersion: String by System.getProperties()

    val googleServicesVersion: String by System.getProperties()

    val kspVersion: String by System.getProperties()

    val androidxAppCompatVersion: String by System.getProperties()
    val androidxNavigationVersion: String by System.getProperties()
    val androidxLifecycleVersion: String by System.getProperties()
    val androidxConstraintlayoutVersion: String by System.getProperties()
    val androidxMaterialVersion: String by System.getProperties()
    val androidxLegacyVersion: String by System.getProperties()
    val androidxRoomVersion: String by System.getProperties()
    val androidxSwipeRefreshLayoutVersion: String by System.getProperties()

    val hiltVersion: String by System.getProperties()

    val retrofit2Version: String by System.getProperties()

    val poiVersion: String by System.getProperties()

    val mailVersion: String by System.getProperties()

    val firebaseBomVersion: String by System.getProperties()
    val firebaseDatabaseVersion: String by System.getProperties()
    val firebaseAuthVersion: String by System.getProperties()
    val firebaseAnalyticsVersion: String by System.getProperties()

    val viewBindingDelegateVersion: String by System.getProperties()

    id("com.android.application") version gradleVersion apply false
    id("com.android.library") version gradleVersion apply false
    id("org.jetbrains.kotlin.android") version kotlinVersion apply false
    id("com.google.gms.google-services") version googleServicesVersion apply false
    id("com.google.dagger.hilt.android") version hiltVersion apply false
    id("androidx.navigation.safeargs") version androidxNavigationVersion apply false
    id("com.google.devtools.ksp") version kspVersion apply false
}