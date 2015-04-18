#!/bin/bash

git fetch --all
git reset --hard origin/master

./update.sh

git commit -am "Automatic update"
git push
