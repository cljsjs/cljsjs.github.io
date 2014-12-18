---
layout: default
title: Javascript Libraries packaged for Clojurescript
---

<!-- <h1>This site is Work in Progress and some information here might not -->
<!--   be correct at all</h1> -->
<!-- <p>CLJSJS aims to provide an easy way for Clojurescript developers -->
<!-- to depend on Javascript libraries. It makes this possible by providing -->
<!-- tooling to package them and use them in your project.</p> -->

### Why

<p class="tldr"><strong>TLDR</strong> CLJSJS provides Javascript libraries and appropriate extern files.
It also tries to be a vehicle for discussion about developing a standard way to
depend on Javascript libraries from Clojurescript projects.</p>

In Clojure, Java interoperability or "interop" is a core feature. In
Clojurescript, interop with Javascript libraries does not work
out-of-the-box across optimization modes. Extern files or "externs" required for
advanced optimizations are often hard to find. To fix this, all CLJSJS
artifacts ship with proper [externs] for the Closure compiler.

Besides the issue of externs, there is another problem: there
is no standard way to depend on Javascript projects. Om and Reagent
depend on React but use [wildly][reagent-template]
[different][chestnut-devmode] [ways][mies-om-template] to import
React.js.  Besides being a hub for externs, CLJSJS also attempts
to provide artifacts for Javascript libraries that Clojurescript
projects can depend on.

### Documentation

Currently there are [tasks for Boot][boot-cljsjs] for adding externs and Javascript
files to builds. As externs and preamble are specified explicitly
as filepaths in `lein-cljsbuild` there is no such mechanism for it yet.

There are also [boot tasks][boot-cljsjs-package] to aid the creation of CLJSJS
artifacts. For example, it might be helpful to look at how `cljsjs/react`
is [currently being packaged][react-build].

Please refer to the documentation of the separate projects in the
[CLJSJS organization][cljsjs-org] on Github for more. Thanks.

[externs]: https://developers.google.com/closure/compiler/docs/api-tutorial3
[reagent-template]: https://github.com/reagent-project/reagent-template/blob/master/src/leiningen/new/reagent/resources/templates/index.html#L11-L19
[chestnut-devmode]: https://github.com/plexus/chestnut/blob/ae3140e76a145aa2275cc9b056d8dbc0a738794c/src/leiningen/new/chestnut/src/clj/chestnut/dev.clj#L10-L15
[mies-om-template]: https://github.com/swannodette/mies-om/blob/master/src/leiningen/new/mies_om/index.html#L4-L7
[boot-cljsjs]: https://github.com/cljsjs/boot-cljsjs
[boot-cljsjs-package]: https://github.com/cljsjs/boot-cljsjs/blob/master/src/cljsjs/packaging.clj
[react-build]: https://github.com/cljsjs/packages/blob/master/react/build.boot
[cljsjs-org]: https://github.com/cljsjs
