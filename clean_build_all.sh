cleanProject () {
	[ ! -d build/libs ] || rm build/libs/*
}

( cd projects/1.7 && cleanProject && ./gradlew build )
( cd projects/1.12 && cleanProject && ./gradlew build && ./gradlew buildForgeMixin07 )