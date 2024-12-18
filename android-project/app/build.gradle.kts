plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("androidx.navigation.safeargs.kotlin")
    id ("kotlin-kapt")
}

android {
    namespace = "com.tasty.recipesapp"
    compileSdk = 34

    buildFeatures {
        viewBinding = true
    }

    kapt {
        correctErrorTypes = true
    }

    defaultConfig {
        applicationId = "com.tasty.recipesapp"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    // Core dependencies
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.activity:activity:1.8.0")
    // Retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")


    // Converter for JSON (like Gson)
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")


    implementation ("com.github.bumptech.glide:glide:4.16.0")

    // Room dependencies
    implementation("androidx.room:room-runtime:2.6.0")
    kapt("androidx.room:room-compiler:2.6.0")
    implementation("androidx.room:room-ktx:2.6.0")

    // Navigation component
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

    // Gson for JSON parsing
    implementation("com.google.code.gson:gson:2.10.1")

    // ViewModel and LiveData
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Retrofit Interceptor for adding headers
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.0")

    // DataStore for securely storing tokens
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // Security Crypto for encryption
    implementation("androidx.security:security-crypto:1.1.0-alpha06")

    // Coroutines for async operations
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")

    // Fragment KTX for viewModels() delegate
    implementation("androidx.fragment:fragment-ktx:1.6.2")

    // OAuth and HTTP
    implementation(platform("com.squareup.okhttp3:okhttp-bom:4.12.0"))
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:logging-interceptor")
    implementation("com.google.android.gms:play-services-auth:21.2.0")

    //courutine
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")

}