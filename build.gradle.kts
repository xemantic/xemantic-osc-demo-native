plugins {
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.gradle.versions.plugin)
}

repositories {
  mavenLocal()
  mavenCentral()
}

kotlin {

  val hostOs = System.getProperty("os.name")
  val isMingwX64 = hostOs.startsWith("Windows")
  val nativeTarget = when {
    hostOs == "Mac OS X" -> macosX64("native")
    hostOs == "Linux" -> linuxX64("native")
    isMingwX64 -> mingwX64("native")
    else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
  }

  with (nativeTarget) {
    binaries {
      executable {
        entryPoint = "main"
      }
    }
  }

  sourceSets {

    val nativeMain by getting {
      dependencies {
        implementation(libs.xemantic.osc.udp)
      }
    }

  }

}
