#!/bin/bash

git fetch --all
git reset --hard origin/real-code

./update.sh

git commit -am "Automatic update"
git push
