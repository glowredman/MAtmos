cleanProject () {
	[ ! -d build/libs ] || rm -f build/libs/*
}

( cd projects/1.7 && cleanProject && ./gradlew build && ./gradlew build -Ptarget=forge-nomixin)
( cd projects/1.12 && cleanProject && ./gradlew build && ./gradlew build -Ptarget=forge-mixin0.7 && ./gradlew build -Ptarget=forge-nomixin )
