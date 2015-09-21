#!/bin/bash

#TAG="$(git describe --tags --abbrev=0)"
TAG="latest"

DOC_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
PROJECT_DIR="$DOC_DIR/../.."

# Build the doc apiary/v1/cep
docker run -it --rm -v $DOC_DIR:/apib yalp/fabre -i /apib/cep/index.apib -o /apib/html

# Switch to gh-pages
cd $PROJECT_DIR
git checkout -f gh-pages

# Move apiary/v1/cep to project root dir
rm -rf "$PROJECT_DIR/apiary/cep/$TAG/" >/dev/null 2>&1
mkdir -p "$PROJECT_DIR/apiary/cep"
mv "$DOC_DIR/html" "$PROJECT_DIR/apiary/cep/$TAG"

# Index and commit
git add apiary
git commit -m "doc: update apiary gh-pages"

git checkout master
