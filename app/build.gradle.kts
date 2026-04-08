plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.polycampus.android"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.polycampus.android"
        minSdk = 28
        targetSdk = 34
        versionCode = 100
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            storeFile = file("release.jks")
            storePassword = "PolyCampus@2026"
            keyAlias = "polycampus"
            keyPassword = "PolyCampus@2026"
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {

    implementation ("com.loopj.android:android-async-http:1.4.11")
    implementation(libs.lottie)
    implementation("androidx.cardview:cardview:1.0.0")
    implementation ("com.google.zxing:core:3.2.1")
    implementation ("com.github.bumptech.glide:glide:4.14.2")
//    implementation ("com.github.chrisbanes:photoview:2.3.0")
    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.messaging)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("com.google.firebase:firebase-bom:33.2.0")
  //    implementation("com.github.chrisbanes:photoview:2.3.0")
    implementation ("com.android.volley:volley:1.2.1")
    implementation ("net.gotev:uploadservice:4.9.0")
    implementation("com.google.code.gson:gson:2.10.1")

    // Face Verification Dependencies
    implementation(libs.mlkit.face.detection)
    implementation(libs.tensorflow.lite)
    implementation(libs.tensorflow.lite.support)
    implementation(libs.tensorflow.lite.metadata)
    implementation(libs.camera.core)
    implementation(libs.camera.camera2)
    implementation(libs.camera.lifecycle)
    implementation(libs.camera.view)

}