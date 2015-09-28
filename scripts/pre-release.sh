#!/bin/bash
# This script prepare the workspace and git for the release build

# Require the version argument
if (( "$#" < "1" )); then
    echo "Usage: pre-release.sh version"
    echo "Example: pre-release.sh 0.1.1"
    exit 1
fi

set -e
set -x

VERSION="$1"

# Prepare workspace
git pull origin master
git checkout -b release

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
git commit -m "prepare $VERSION"
git tag $VERSION
git push origin $VERSION
