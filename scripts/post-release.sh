#!/bin/bash
# This script prepare the workspace and git after the release build

# Require the version argument
if (( "$#" < "1" )); then
    echo "Usage: post-release.sh new-version"
    echo "Example: post-release.sh 0.1.0"
    exit 1
fi

set -e
set -x

OLDVERSION=`cat pom.xml | grep "<version>" | head -1 | sed 's/[^0-9.]*\([0-9.]*\).*/\1/'`
NEWVERSION="$1"

# Replace {version}-SNAPSHOT by {NEWVERSION} except ci-release.sh
mvn versions:set -DnewVersion=$NEWVERSION-SNAPSHOT -DgenerateBackupPoms=false

find . -name "Dockerfile" -name "*.md"  -name "*.sh" ! -name 'ci-release.sh'  -type f -exec sed -i '' "s/$OLDVERSION-SNAPSHOT/$NEWVERSION-SNAPSHOT/g" {} \;
