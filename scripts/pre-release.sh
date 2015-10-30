#!/bin/bash
# This script prepare the workspace and git for the release build
set -e
set -x

# Replace {version}-SNAPSHOT by {version} except ci-release.sh
find . -name "*.xml" -name "Dockerfile" -name "*.md" -name "*.sh" ! -name 'ci-release.sh'  -type f -exec sed -i '' "s/-SNAPSHOT//g" {} \;

# Replace snapshots by releases except in .xml files
find . -name "Dockerfile" -name "*.md" -name "*.sh" ! -name 'ci-release.sh'  -type f -exec sed -i '' "s/snapshots/releases/g" {} \;
