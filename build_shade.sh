#!/usr/bin/env bash
# Note: this file needs to be run in a unix-like environment. If you're on
# Windows, you can use MSYS, Cygwin or WSL.

set -e

echo Backuping files...

cp src src_ -pr
cp lib/MC-Commons/mc-src lib/MC-Commons/mc-src_ -pr
cp build.gradle build.gradle_ -p

echo Relocating mc-commons package...

mv lib/MC-Commons/mc-src/eu lib/MC-Commons/mc-src/eu_tmp
mkdir --parents lib/MC-Commons/mc-src/eu/ha3/matmos/lib/eu
mv lib/MC-Commons/mc-src/eu_tmp/* $_ && rmdir lib/MC-Commons/mc-src/eu_tmp
find lib/MC-Commons/mc-src/ -type f -exec sed -i "s/eu\.ha3\./eu\.ha3\.matmos\.lib\.eu\.ha3\./g" {} +
find src -type f -exec sed -i "s/eu\.ha3\.easy/eu\.ha3\.matmos\.lib\.eu\.ha3\.easy/g" {} +
find src -type f -exec sed -i "s/eu\.ha3\.mc/eu\.ha3\.matmos\.lib\.eu\.ha3\.mc/g" {} +
find src -type f -exec sed -i "s/eu\.ha3\.util/eu\.ha3\.matmos\.lib\.eu\.ha3\.util/g" {} +
sed -i "s/eu\/ha3\/mc/eu\/ha3\/matmos\/lib\/eu\/ha3\/mc/g" build.gradle

./gradlew build || echo "Build exited with error"

echo Restoring backups...
rm -rf src build.gradle lib/MC-Commons/mc-src
mv src_ src
mv build.gradle_ build.gradle
mv lib/MC-Commons/mc-src_ lib/MC-Commons/mc-src 