#!/bin/bash
data=$(curl --connect-timeout 5 -s https://clojars.org/api/groups/cljsjs)

if [[ $? != "0" ]]; then
    echo "ERROR: Clojars is down"
    exit
fi

CACHE=~/.m2/repository/cljsjs
OUT=resources/data.json
FIRST=yes

echo "[" > $OUT
IFS=$'\n'
for e in $(echo $data | jq -c ".[]"); do
    artifact=$(echo $e | jq -r ".jar_name")
    version=$(echo $e | jq -r ".latest_version")

    jarfile="$artifact/$version/$artifact-$version.jar"
    mkdir -p $(dirname $CACHE/$jarfile)
    if [[ ! -f $CACHE/$jarfile ]]; then
        curl -o $CACHE/$jarfile https://clojars.org/repo/cljsjs/$jarfile
    fi
    deps=$(unzip -p $CACHE/$jarfile deps.cljs | sed 's/"/\\"/g')

    x=$(echo $e | jq -c ".artifact=.jar_name | .version=.latest_version | del(.latest_release) | del(.latest_version) | del(.jar_name) | del(.group_name) | del(.user) | .deps=\"$deps\"")

    # Writing json is hard...
    if [[ $FIRST == "yes" ]]; then
        FIRST=
    else
        echo -en ",\n" >> $OUT
    fi
    echo -n $x >> $OUT
done


echo -e "\n]" >> $OUT
