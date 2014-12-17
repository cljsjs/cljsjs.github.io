---
layout: default
---

<!-- <h1>This site is Work in Progress and some information here might not -->
<!--   be correct at all</h1> -->
<!-- <p>CLJSJS aims to provide an easy way for Clojurescript developers -->
<!-- to depend on Javascript libraries. It makes this possible by providing -->
<!-- tooling to package them and use them in your project.</p> -->

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
is [currently being packaed][react-build].

<p>Including a CLJSJS package into your project can work differently
  depending on what tools you use to compile Clojurescript. In general CLJSJS
  packages add the library to your classpath at a predictable location.</p>

<p>In <strong>lein-cljsbuild</strong> this means you can add the path
  to the to-be-included Javascript file to your <code>:preamble</code>
  and the externs for it to <code>:externs</code>. An Example:
</p>

{% highlight clojure %}
; ...
:dependencies [; ...
               [cljsjs/react "0.11.2"]]
; ...
:cljsbuild { :builds
  [{:source-paths ["src/cljs"}]
    :compiler {:preamble      ["cljsjs/react/react.inc.js"]
               :externs       ["cljsjs/react/react.ext.js"]
               :optimizations :advanced}}}}
{% endhighlight %}

<p>If you are using <strong>boot-cljs</strong> it will find these
  files automatically so specifying it as dependency is enough:</p>

{% highlight clojure %}
(set-env!
 :source-paths #{"src/cljs"}
 :dependencies '[[adzerk/boot-cljs "0.0-2371-27"]
                 [cljsjs/react "0.11.2"]])
{% endhighlight %}

<h4>Currently Available</h4>

<p>
  <a href="https://github.com/cljsjs/react">React.js</a>,
  <a href="https://github.com/cljsjs/react">React.js</a>
</p>

<!-- <h3>React</h3> -->

<!-- <p>Usage with <a href="https://github.com/emezeske/lein-cljsbuild">lein-cljsbuild</a>:</p> -->
<!-- <ol> -->
<!-- <li>Depend on <code>[cljsjs/react "0.12.1"]</code></li> -->
<!-- <li>Add this to your <code>:preamble</code> option: <code>"cljsjs/includes/react/react.inc.js"</code></li> -->
<!-- <li>Add this to your <code>:externs</code> option: <code>"cljsjs/includes/react/react.ext.js"</code></li> -->
<!-- </ol> -->



<!-- <div class="home"> -->

<!--   <h1 class="page-heading">Posts</h1> -->

<!--   <ul class="post-list"> -->
<!--     {% for post in site.posts %} -->
<!--       <li> -->
<!--         <span class="post-meta">{{ post.date | date: "%b %-d, %Y" }}</span> -->

<!--         <h2> -->
<!--           <a class="post-link" href="{{ post.url | prepend: site.baseurl }}">{{ post.title }}</a> -->
<!--         </h2> -->
<!--       </li> -->
<!--     {% endfor %} -->
<!--   </ul> -->

<!--   <p class="rss-subscribe">subscribe <a href="{{ "/feed.xml" | prepend: site.baseurl }}">via rss</a></p> -->

<!-- </div> -->

[reagent-template]: https://github.com/reagent-project/reagent-template/blob/master/src/leiningen/new/reagent/resources/templates/index.html#L11-L19
[chestnut-devmode]: https://github.com/plexus/chestnut/blob/ae3140e76a145aa2275cc9b056d8dbc0a738794c/src/leiningen/new/chestnut/src/clj/chestnut/dev.clj#L10-L15
[mies-om-template]: https://github.com/swannodette/mies-om/blob/master/src/leiningen/new/mies_om/index.html#L4-L7
[boot-cljsjs]: https://github.com/cljsjs/boot-cljsjs
[boot-cljsjs-package]: https://github.com/cljsjs/boot-cljsjs/blob/master/src/cljsjs/packaging.clj
[react-build]: https://github.com/cljsjs/packages/blob/master/react/build.boot
