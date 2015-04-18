#!/bin/bash

OUT=_includes/packages.html

echo "" > $OUT
echo "<ul>" >> $OUT

for id in $(cat packages); do
    version=$(curl -s https://clojars.org/${id}/latest-version.json | jq ".version")

    echo "  <li>" >> $OUT
    echo "    <a href=\"https://clojars.org/${id}\">${id}</a>" >> $OUT
    echo "    <span class=\"clojars\">" >> $OUT
    echo "      <input type=\"text\" value='[${id} ${version}]'/>" >> $OUT
    echo "      <button data-clipboard-text='[${id} ${version}]'>Copy</button>" >> $OUT
    echo "    </span>" >> $OUT
    echo "  </li>" >> $OUT
done

echo "</ul>" >> $OUT
