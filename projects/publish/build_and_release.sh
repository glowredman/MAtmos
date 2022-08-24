if [ ! -s changelog.md ]; then
    echo "Changelog is empty, refusing to publish."
    exit 3
fi

if [[ $(git diff --cached --stat) != '' ]]
then
	echo "There are staged uncommitted changes, refusing to publish."
	exit 2
fi

if [ "$#" -lt 1 ] || [ "$#" -gt 3 ]
then
	echo "Usage: $0 GITHUB_TOKEN CURSEFORGE_TOKEN [MODRINTH_TOKEN]"
	exit 1
fi

# exit when any command fails
set -e

GITHUB_TOKEN=$1
CURSEFORGE_TOKEN=$2
MODRINTH_TOKEN=$3

# build the release
(cd ../..; sh clean_build_all.sh)

# release
./gradlew githubRelease -PgithubToken=$GITHUB_TOKEN

py update_updatejson.py
git add --all
git commit -m "[skip ci] Update update json"
git push

if [ -n "$CURSEFORGE_TOKEN" ]
then
	./curseforge_all.sh -PcurseToken=$CURSEFORGE_TOKEN
fi

if [ -n "$MODRINTH_TOKEN" ]
then
	./modrinth_all.sh -PmodrinthToken=$MODRINTH_TOKEN
fi