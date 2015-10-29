#!/bin/bash
# This script prepare the workspace and git for the release build

set -e
set -x

VERSION=`cat pom.xml | grep "<version>" | head -1 | sed 's/[^0-9.]*\([0-9.]*\).*/\1/'`

# Prepare workspace
git checkout master
git pull origin master
git branch -f release master
git checkout release

# Replace {version}-SNAPSHOT by {version} except ci-release.sh
find . -name "*.xml" -type f -exec sed -i '' "s/-SNAPSHOT//g" {} \;
find . -name "*.md" -type f -exec sed -i '' "s/-SNAPSHOT//g" {} \;
find . -name "Dockerfile" -type f -exec sed -i '' "s/-SNAPSHOT//g" {} \;
find . -name "*.sh" ! -name 'ci-release.sh'  -type f -exec sed -i '' "s/-SNAPSHOT//g" {} \;

# Replace snapshots by releases except in .xml files
find . -name "*.md" -type f -exec sed -i '' "s/snapshots/releases/g" {} \;
find . -name "Dockerfile" -type f -exec sed -i '' "s/snapshots/releases/g" {} \;
find . -name "*.sh" ! -name 'ci-release.sh'  -type f -exec sed -i '' "s/snapshots/releases/g" {} \;

# Push modifications
git commit -a -m "prepare $VERSION"
git tag $VERSION
git push origin $VERSION

# Switch back to master
git checkout master
git branch -D release
