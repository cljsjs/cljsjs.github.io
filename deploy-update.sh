#!/bin/bash

git fetch --all
git reset --hard origin/real-code

./update.sh

git commit -am "Automatic update"
git push

# like build.sh but copy only data.json

rev=$(git rev-parse HEAD)
remoteurl=$(git ls-remote --get-url origin)

if [[ ! -d gh-pages ]]; then
    git clone --branch master ${remoteurl} gh-pages
fi
(
cd gh-pages
git pull
)

cp -r resources/data.json gh-pages/data.json

cd gh-pages

git add --all
git commit -m "Build from ${rev}."
git push origin master
