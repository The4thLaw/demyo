#!/bin/bash

version="3.1.0-SNAPSHOT"

rm -f demyo-v*.tar.bz2 Demyo*.exe

echo "Building Demyo..."
cd ../source
mvn clean install -Pprod,vue
cd -

#
# LINUX
#
echo "Packaging Linux version..."
tempDir="$(mktemp -d)/demyo-v$version"
mkdir -p $tempDir/lib $tempDir/war $tempDir/legacy-h2-versions
echo "Copying artifacts to $tempDir"
cp ../source/demyo-app/target/dependencies/* ../source/demyo-app/target/demyo-app-$version.jar $tempDir/lib
cp ../source/demyo-web/target/demyo-web-$version.war $tempDir/war
cp ../source/demyo-app/target/legacy-h2-versions/* $tempDir/legacy-h2-versions
cp linux/demyo.sh $tempDir
cd $tempDir/..
echo "Building package"
tar cjf demyo-v$version.tar.bz2 demyo-v$version
cd -
cp $tempDir/../demyo-v$version.tar.bz2 ./
echo "Cleaning up"
rm -rf $(dirname $tempDir)
echo "Done for Linux"

echo
echo

#
# WINDOWS
#
echo "Packaging Windows version..."
cd windows
makensis -V1 demyo.nsi
mv Setup.exe "../Demyo v$version Setup.exe"
echo "Done for Windows"

