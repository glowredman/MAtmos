# how 2 publish
```
0. (cd ../..; sh clean_build_all.sh)
1. ./gradlew githubRelease -PgithubToken=$GITHUB_TOKEN
2. py update_updatejson.py && git add --all && git commit -m "Update update json" && git push
3. ./curseforge_all.sh -PcurseToken=$CURSEFORGE_TOKEN
4. ./modrinth_all.sh -PmodrinthToken=$MODRINTH_TOKEN
```