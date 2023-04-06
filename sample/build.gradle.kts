plugins {
    kotlin("jvm") version "1.8.10"
    id("com.google.devtools.ksp") version "1.8.10-1.0.9"
}

repositories {
    mavenCentral()
}

ksp {
    
}

dependencies {
    ksp(project(":lib"))
    implementation(project(":lib"))
}
