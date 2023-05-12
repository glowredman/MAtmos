for proj in `cat gameVersions.txt`; do
	./gradlew modrinth -PgameVersion=$proj $*
done