pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()

        // 카카오 SDK 설정 (카카오 로그인)
        maven ( url = "https://devrepo.kakao.com/nexus/content/groups/public/" )
    }
}

rootProject.name = "CODINAVI2"
include(":app")
 