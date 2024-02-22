plugins {
    id("com.android.application")
}

android {
    namespace = "com.instar.lab1"
    compileSdk = 34



    defaultConfig {
        applicationId = "com.instar.lab1"
        minSdk = 26
        targetSdk = 33
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    packagingOptions {
        exclude("META-INF/DEPENDENCIES")
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")


    //api
    implementation("com.google.http-client:google-http-client-gson:1.30.10")

    // Thư viện Google Drive API
    implementation("com.google.apis:google-api-services-drive:v3-rev20200813-1.30.10")

    // Thư viện Google Sign-In
    implementation("com.google.android.gms:play-services-auth:21.0.0")

    // Thư viện Google API Client, cần thiết cho việc xác thực và giao tiếp với Google APIs
    implementation("com.google.api-client:google-api-client-android:1.30.10")
    implementation("com.google.api-client:google-api-client-gson:1.32.1")


}