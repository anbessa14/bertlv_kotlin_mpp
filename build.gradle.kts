plugins {
    kotlin("multiplatform") version "1.4.10"
    id("maven-publish")
    id("com.jfrog.bintray") version "1.8.0"
}

group = "com.dowhilenotfalse"
version = "1.0.0"

repositories {
    mavenCentral()
    jcenter()
}

buildscript {
    repositories {
        mavenCentral()
        jcenter()
    }
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        testRuns["test"].executionTask.configure {
            useJUnit()
        }
    }

    js(LEGACY){
        nodejs {
        }
        browser {
            webpackTask {
                outputFileName = "bertlv.js"
            }
            testTask {
                useKarma {
                    useChromeHeadless()
                    webpackConfig.cssSupport.enabled = true
                }
            }
            binaries.executable()
        }
    }
        macosX64("bertlv-mac64") {
            binaries {
                framework {
                    baseName = "bertlv-mac64"
                }
            }
        }
        iosArm64("bertlv-ios64"){
            binaries {
                framework {
                    baseName = "bertlv-ios"
                }
            }
        }

        iosX64("bertlv-iosEmulator"){
            binaries{
                framework{
                    baseName = "bertlv-iosEmulator"
                }
            }
        }



    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    
    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmMain by getting
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }
        val jsMain by getting
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
        val nativeMain by getting
        val nativeTest by getting
    }
}


publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/dowhilenotfalse/bertlv_kotlin_mpp")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

bintray {
    user = System.getenv("BINTRAY_USERNAME")
    key = System.getenv("BINTRAY_KEY")
}


