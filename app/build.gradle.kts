plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.doggo_ourapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.mobileapp.doggo_ourapp"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(platform("com.google.firebase:firebase-bom:33.14.0"))
    implementation("com.google.firebase:firebase-database:20.3.0")
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    implementation("io.github.jan-tennert.supabase:supabase-kt:3.2.0-beta-1")
    implementation("io.github.jan-tennert.supabase:storage-kt-android:3.2.0-beta-1")
    implementation("io.ktor:ktor-client-core:3.1.2")
    implementation("io.ktor:ktor-client-okhttp:3.1.2")
    implementation("io.ktor:ktor-client-android:3.1.2")
    implementation("io.ktor:ktor-client-serialization:3.1.2")
    implementation("io.github.jan-tennert.supabase:gotrue-kt:1.3.1")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.auth)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

}