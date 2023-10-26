plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    val compileSdkVersion: String by System.getProperties()

    namespace = "com.taxapprf.taxapp.domain"
    compileSdk = compileSdkVersion.toInt()

    defaultConfig {
        val minSdkVersion: String by System.getProperties()
        val targetSdkVersion: String by System.getProperties()

        minSdk = minSdkVersion.toInt()
        targetSdk = targetSdkVersion.toInt()
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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.majorVersion
    }
}

dependencies {
    val kotlinCoroutinesVersion: String by System.getProperties()

    implementation("javax.inject:javax.inject:1")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$kotlinCoroutinesVersion")
}


