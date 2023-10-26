plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("com.google.devtools.ksp")
}

android {
    val compileSdkVersion: String by System.getProperties()

    namespace = "com.taxapprf.taxapp.data"
    compileSdk = compileSdkVersion.toInt()

    defaultConfig {
        val minSdkVersion: String by System.getProperties()

        minSdk = minSdkVersion.toInt()
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
    val hiltVersion: String by System.getProperties()

    val androidxRoomVersion: String by System.getProperties()

    val firebaseBomVersion: String by System.getProperties()
    val firebaseDatabaseVersion: String by System.getProperties()
    val firebaseAuthVersion: String by System.getProperties()
    val firebaseAnalyticsVersion: String by System.getProperties()

    val retrofit2Version: String by System.getProperties()

    val poiVersion: String by System.getProperties()


    implementation(project(":domain"))

    implementation("com.google.dagger:hilt-android:$hiltVersion")
    kapt("com.google.dagger:hilt-android-compiler:$hiltVersion")

    implementation("androidx.room:room-runtime:$androidxRoomVersion")
    ksp("androidx.room:room-compiler:$androidxRoomVersion")
    implementation("androidx.room:room-ktx:$androidxRoomVersion")

    implementation(platform("com.google.firebase:firebase-bom:$firebaseBomVersion"))
    implementation("com.google.firebase:firebase-database-ktx:$firebaseDatabaseVersion")
    implementation("com.google.firebase:firebase-auth-ktx:$firebaseAuthVersion")
    implementation("com.google.firebase:firebase-analytics:$firebaseAnalyticsVersion")

    implementation("com.squareup.retrofit2:retrofit:$retrofit2Version")
    implementation("com.squareup.retrofit2:converter-simplexml:$retrofit2Version")

    implementation("org.apache.poi:poi:$poiVersion")
}