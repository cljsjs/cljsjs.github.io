---
layout: default
title: Javascript Libraries packaged for Clojurescript
---

### Why

<p class="tldr"><strong>TLDR</strong> CLJSJS provides Javascript libraries and appropriate extern files.
Also it tries to be a platform for discussion on developing a standard way to
depend on Javascript libraries within Clojurescript projects.</p>

In Clojure Java interoperability is a core feature. In Clojurescript
interoperability with Javascript libraries does not work out-of-the-box
accross the various optimization modes. Extern files required for advanced
optimizations are often hard to find. To fix this all CLJSJS artifacts
ship with proper Extern files for the Closure compiler.

Besides the issue around Extern files there is another problem: there
is no standard way to depend on Javascript projects. Om and Reagent
depend on React but use [wildly][reagent-template] [different][chestnut-devmode]
[ways][mies-om-template] to import React.js.
Besides being a hub for Extern files CLJSJS also attempts to provide
artifacts for Javascript libraries Clojurescript projects can depend on.

### Documentation

Currently there are [tasks for Boot][boot-cljsjs] to add externs and Javascript
files to the build process. As externs and preamble are specified explicitly
as filepaths in `lein-cljsbuild` there is no such mechanism for it yet.

There are also [boot tasks][boot-cljsjs-package] to aid the creation of CLJSJS
artifacts. For an example it might be insightful to look at how `cljsjs/react`
is [currently being packaged][react-build].

Please refer to the documentation of the separate projects in the
[CLJSJS organization][cljsjs-org] on Github for more. Thanks.

[reagent-template]: https://github.com/reagent-project/reagent-template/blob/master/src/leiningen/new/reagent/resources/templates/index.html#L11-L19
[chestnut-devmode]: https://github.com/plexus/chestnut/blob/ae3140e76a145aa2275cc9b056d8dbc0a738794c/src/leiningen/new/chestnut/src/clj/chestnut/dev.clj#L10-L15
[mies-om-template]: https://github.com/swannodette/mies-om/blob/master/src/leiningen/new/mies_om/index.html#L4-L7
[boot-cljsjs]: https://github.com/cljsjs/boot-cljsjs
[boot-cljsjs-package]: https://github.com/cljsjs/boot-cljsjs/blob/master/src/cljsjs/packaging.clj
[react-build]: https://github.com/cljsjs/packages/blob/master/react/build.boot
[cljsjs-org]: https://github.com/cljsjs
