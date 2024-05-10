plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.gateoftruth.sample"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.gateoftruth.sample"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = false
        viewBinding = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")


    implementation ("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.14")
    implementation ("com.squareup.okhttp3:okhttp-brotli:5.0.0-alpha.14")
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    //implementation ("com.github.bumptech.glide:annotations:4.16.0")
    implementation ("com.github.bumptech.glide:okhttp3-integration:4.16.0")
    ksp("com.github.bumptech.glide:ksp:4.16.0")
    implementation ("com.google.code.gson:gson:2.10.1")
    implementation(project(":oklibrary"))
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

}