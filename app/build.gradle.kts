plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("com.google.gms.google-services")
    id("dagger.hilt.android.plugin")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    val compileSdkVersion: String by System.getProperties()

    namespace = "com.taxapprf.taxapp.app"
    compileSdk = compileSdkVersion.toInt()

    defaultConfig {
        val minSdkVersion: String by System.getProperties()
        val targetSdkVersion: String by System.getProperties()
        val versionCodeVar: String by System.getProperties()
        val versionNameVar: String by System.getProperties()

        applicationId = "com.taxapprf.taxapp"
        minSdk = minSdkVersion.toInt()
        targetSdk = targetSdkVersion.toInt()

        versionCode = versionCodeVar.toInt()
        versionName = versionNameVar
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.majorVersion
    }
}

dependencies {
    val hiltVersion: String by System.getProperties()

    val androidxAppCompatVersion: String by System.getProperties()
    val androidxMaterialVersion: String by System.getProperties()
    val androidxConstraintlayoutVersion: String by System.getProperties()
    val androidxLifecycleVersion: String by System.getProperties()
    val androidxNavigationVersion: String by System.getProperties()

    val viewBindingDelegateVersion: String by System.getProperties()

    implementation(project(":data"))
    implementation(project(":domain"))

    implementation("com.google.dagger:hilt-android:$hiltVersion")
    kapt("com.google.dagger:hilt-android-compiler:$hiltVersion")

    implementation("androidx.appcompat:appcompat:$androidxAppCompatVersion")
    implementation("com.google.android.material:material:$androidxMaterialVersion")
    implementation("androidx.constraintlayout:constraintlayout:$androidxConstraintlayoutVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$androidxLifecycleVersion")
    implementation("androidx.navigation:navigation-fragment-ktx:$androidxNavigationVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$androidxNavigationVersion")

    implementation("com.github.kirich1409:viewbindingpropertydelegate-noreflection:$viewBindingDelegateVersion")
}