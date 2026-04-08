plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.17.2"
}

group = "dev.pulse"
version = "1.0.0"

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

intellij {
    version.set("2023.3") // Base IDE — compatible with Android Studio Hedgehog+
    type.set("IC")        // IntelliJ Community. Change to "AI" for Android Studio
    plugins.set(listOf("Git4Idea", "org.jetbrains.plugins.terminal"))
}

tasks {
    patchPluginXml {
        sinceBuild.set("233")
        untilBuild.set("243.*")
    }

    buildSearchableOptions {
        enabled = false
    }
}
