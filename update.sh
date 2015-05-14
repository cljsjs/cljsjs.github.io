#!/bin/bash

OUT=_includes/packages.html

echo "" > $OUT
echo "<ul>" >> $OUT

IFS=$'\n'
for e in $(curl -s https://clojars.org/api/groups/cljsjs | jq -c ".[]"); do
    group=$(echo $e | jq -r ".group_name")
    artifact=$(echo $e | jq -r ".jar_name")
    id="$group/$artifact"
    version=$(echo $e | jq ".latest_version")
    description=$(echo $e | jq -r ".description")
    echo "  <li>" >> $OUT
    echo "    <a href=\"https://clojars.org/${id}\">${id}</a>" >> $OUT
    echo "    <span class=\"clojars\">" >> $OUT
    echo "      <input type=\"text\" value='[${id} ${version}]'/>" >> $OUT
    echo "      <button data-clipboard-text='[${id} ${version}]'>Copy</button>" >> $OUT
    echo "    </span>" >> $OUT
    echo "  </li>" >> $OUT
done

echo "</ul>" >> $OUT
