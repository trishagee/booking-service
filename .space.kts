job("Build and publish BookingService container") {
    val containerTagVersion = "v0.\$JB_SPACE_EXECUTION_NUMBER"

    container(displayName = "Run mvn package", image = "maven:3.8.2-openjdk-16") {
        shellScript {
            content = """
	            ./gradlew :build :jar --init-script $mountDir/system/gradle/init.gradle
                mkdir -p $mountDir/share/build/libs/
                cp -r build/libs/*.jar $mountDir/share/build/libs
            """.trimIndent()
        }

        service("mysql:8.0.25") {
            alias("mysql")

            env["MYSQL_ROOT_PASSWORD"] = "root-password"
            env["MYSQL_USER"] = "user"
            env["MYSQL_PASSWORD"] = "app-password"
            env["MYSQL_DATABASE"] = "booking_service_db"
        }
    }

    docker(displayName = "Build and push container") {
        beforeBuildScript {
            content = """
                mkdir -p build/libs/
                cp -r $mountDir/share/build/libs/*.jar build/libs
            """.trimIndent()
        }
        build {
            context = "."
            file = "./Dockerfile"
            labels["vendor"] = "JetBrains"
        }

        push("registry.jetbrains.team/p/rsapp/containers/booking-service") {
            tags(containerTagVersion)
        }
    }
}