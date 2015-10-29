#!/bin/bash
# This script prepare the workspace and git after the release build

# Require the version argument
if (( "$#" < "2" )); then
    echo "Usage: post-release.sh old-version new-version"
    echo "Example: post-release.sh 0.1.1 0.1.2"
    exit 1
fi

set -e
set -x

OLDVERSION="$1"
NEWVERSION="$2"

# Prepare master to the new version
git checkout master
git branch -D release

# Replace {version} by {NEWVERSION} except ci-release.sh
find . -name "*.xml" -type f -exec sed -i '' "s/$OLDVERSION/$NEWVERSION/g" {} \;
find . -name "*.md" -type f -exec sed -i '' "s/$OLDVERSION/$NEWVERSION/g" {} \;
find . -name "Dockerfile" -type f -exec sed -i '' "s/$OLDVERSION/$NEWVERSION/g" {} \;
find . -name "*.sh" ! -name 'ci-release.sh'  -type f -exec sed -i '' "s/$OLDVERSION/$NEWVERSION/g" {} \;

# Push modifications
git commit -m "prepare $NEWVERSION"
git push origin master
