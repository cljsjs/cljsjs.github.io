---
layout: default
title: Javascript Libraries packaged for Clojurescript
---

### Why

Since Clojurescript 0.0-2727 the `:foreign-libs` option provides an
excellent way to integrate Javascript into Clojurescript
applications. CLJSJS provides Javascript libraries and their
appropriate extern files packaged up with `deps.cljs`. CLJSJS aims to
concentrate packaging efforts to make everyone's live a little easier.

### Documentation

CLJSJS packages are gathered on Github in
[cljsjs/packages][packages-repo].  There you can also find usage
instructions, request new libraries to be packaged or contribute
yourself.

We use [Boot][boot] to make packaging quick and easy but it is not
required to make use of CLJSJS packaged Jars.

[boot]: https://github.com/boot-clj/boot
[externs]: https://developers.google.com/closure/compiler/docs/api-tutorial3
[reagent-template]: https://github.com/reagent-project/reagent-template/blob/master/src/leiningen/new/reagent/resources/templates/index.html#L11-L19
[chestnut-devmode]: https://github.com/plexus/chestnut/blob/ae3140e76a145aa2275cc9b056d8dbc0a738794c/src/leiningen/new/chestnut/src/clj/chestnut/dev.clj#L10-L15
[mies-om-template]: https://github.com/swannodette/mies-om/blob/master/src/leiningen/new/mies_om/index.html#L4-L7
[boot-cljsjs]: https://github.com/cljsjs/boot-cljsjs
[react-build]: https://github.com/cljsjs/packages/blob/master/react/build.boot
[cljsjs-org]: https://github.com/cljsjs
[packages-repo]: https://github.com/cljsjs/packages
