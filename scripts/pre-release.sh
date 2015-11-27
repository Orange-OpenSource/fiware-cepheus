#!/bin/bash
# This script prepare the workspace and git for the release build
set -e
set -x

# Replace {version}-SNAPSHOT by {version} except ci-release.sh
find . \( -name "*.xml" -o -name "Dockerfile" -o -name "*.md" -o -name "*.sh" \) ! -name '*-release.sh'  -type f -exec sed -i "s/-SNAPSHOT//g" {} \;

# Replace releases by releases except in .xml files
find . \( -name "Dockerfile" -o -name "*.md" -o -name "*.sh" \) ! -name '*-release.sh'  -type f -exec sed -i "s/snapshots/releases/g" {} \;
