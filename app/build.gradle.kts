plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.helpme.redciudadana"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.helpme.redciudadana"
        minSdk = 26
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = true
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)

    // Firebase Bill of Materials
    implementation(
        platform("com.google.firebase:firebase-bom:34.7.0")
    )

    // Firebase Authentication
    implementation("com.google.firebase:firebase-auth")

    // Firebase Realtime Database
    implementation("com.google.firebase:firebase-database")

    implementation("com.google.android.gms:play-services-location:21.3.0")

    implementation("androidx.activity:activity-ktx:1.13.0")

    implementation("androidx.recyclerview:recyclerview:1.4.0")

    implementation("org.osmdroid:osmdroid-android:6.1.20")

}