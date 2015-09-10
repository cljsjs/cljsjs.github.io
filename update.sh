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
    description=$(echo $e | jq -r ".description" | sed 's/&(?!amp;)/\&amp;/g; s/</\&lt;/g; s/>/\&gt;/g; s/"/\&quot;/g; s/'"'"'/\&#39;/g')
    homepage=$(echo $e | jq -r ".homepage")
    echo "  <li>" >> $OUT
    echo "    <a href=\"https://clojars.org/${id}\">${artifact}</a>" >> $OUT
    echo "    <a href=\"$homepage\" target=\"new\"><i class=\"fa fa-home\"></i></a>" >> $OUT
    echo "    <span class=\"clojars\">" >> $OUT
    echo "      <input type=\"text\" value='[${id} ${version}]'/>" >> $OUT
    echo "      <button data-clipboard-text='[${id} ${version}]'><i class=\"fa fa-copy\"></i></button>" >> $OUT
    echo "    </span>" >> $OUT
    echo "    <p class=\"description\">$description</p>" >> $OUT
    echo "  </li>" >> $OUT
done

echo "</ul>" >> $OUT
