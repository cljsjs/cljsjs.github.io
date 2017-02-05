(ns frontend.core
  (:require [reagent.core :as r]
            [cljs.reader :as reader]
            [komponentit.autocomplete :as ac]
            [komponentit.highlight :as hi]
            [komponentit.clipboard :as clipboard]
            [cljs.pprint :as pprint]))

(defonce packages (r/atom nil))
(defonce search (r/atom nil))
(defonce package-type-filter (r/atom nil))

(def cljsjs-group "cljsjs")

(defn infer-type [{:keys [deps] :as lib}]
  (let [{:keys [foreign-libs libs]} deps
        module-type (some :module-type foreign-libs)]
    (assoc lib :package-type (cond
                               module-type :processed-module
                               foreign-libs :foreign-lib
                               libs :closure-lib
                               :else "foo?"))))

(defn load-packages []
  (let [req  (js/XMLHttpRequest.)]
    (set! (.-responseType req) "json")
    (doto req
      (.addEventListener "load" (fn []
                                  (reset! packages (mapv (fn [obj]
                                                           (-> obj
                                                               (js->clj :keywordize-keys true)
                                                               (update :deps (fn [deps-str]
                                                                               (reader/read-string deps-str)))
                                                               (infer-type)))
                                                         (.-response req)))))
      (.open "GET" "data.json")
      (.send))))

(def term-match-fn (ac/create-matcher [:artifact :description]))

(defn filtered-packages []
  (let [current-packages @packages
        query @search
        package-type @package-type-filter
        search-filter (if @search
                        (filter #(ac/query-match? term-match-fn % query))
                        identity)
        type-filter (if-let [x @package-type-filter]
                      (filter #(= x (:package-type %)))
                      identity)]
    (into [] (comp search-filter type-filter) current-packages)))

(defn search-input []
  (let [temp (r/atom @search)
        timeout (atom nil)]
    (fn []
      [:input.w-100.pa3.f4
       {:type "text"
        :placeholder "Search ..."
        :value @temp
        :on-change (fn [e]
                     (let [v (.. e -target -value)]
                       (reset! temp v)
                       (swap! timeout (fn [current-timeout]
                                        (if current-timeout (js/clearTimeout current-timeout))
                                        (js/setTimeout (fn [_]
                                                         (reset! search (ac/default->query v)))
                                                       300)))))}])))

(defn package-types []
  (let [active (or @package-type-filter :all)
        cb (fn [v e]
             (.preventDefault e)
             (reset! package-type-filter (if (= :all v) nil v)))]
    [:div.pa3.br.bl.bb.b--black-20.bg-near-white
     [:nav
      [:span.mr2.b.mid-gray "Package types: "]
      (for [[k t] [[:all "All"]
                   [:foreign-lib "Foreign library"]
                   [:closure-lib "Closure library"]
                   [:processed-module "CommonJS, ES6, AMDjs"]]]
        [:button.btn-reset.f6.f5-ns.dib.mr2
         {:key k
          :href "#"
          :class (if (= k active) "black b" "blue")
          :on-click #(cb k %)}
         t])]
     (when-not (= :all active)
       [:p.pt3.ma0.lh-copy
        (case active
          :foreign-lib  "Foreign libraries are normal JavaScript libraries intended for browser consumption
                       packaged with extern files so they can be used with Closure optimized ClojureScript
                       application. The library JavaScript code is included as is as part of the output,
                       and Closure can't optimize the code (dead code elimination, name mangling)."
          :closure-lib "Closure libraries are JavaScript libraries intended for use with Closure compiler.
                      These libraries can be optimized by Closure compiler, same as Cljs code."
          :processed-module "In future Cljs compiler can hopefully use Closure Module Processing to convert
                           e.g. CommonJS and ES6 modules to Closure modules. This will allow Closure
                           compiler to optimize code and we don't need to use webpack or such to package
                           these libraries for browser."
          nil)])]))

(defn select-on-click-input [_ _]
  (let [copied? (r/atom nil)]
    (fn [text query]
      [:div.dim.pa3.bb.b--black-20
       {:on-click (fn [e]
                    (clipboard/copy-text text)
                    (reset! copied? true))}
       [:div.mv2
        [:span.f4.code.mr1 [hi/highlight-string text query]]
        [:span.dib.pt2.f6.black-50 (if @copied? "(copied!)" "(click to copy)")]]])))

(defn dep-vec [artifact version]
  (str "[" cljsjs-group "/" artifact " \"" version "\"]"))

(defn code [& contents]
  (into [:code.blue] contents))

(defn package [_]
  (let [expanded?      (r/atom false)
        show-cljs-edn? (r/atom false)]
    (fn package-render [{:keys [artifact description homepage version deps package-type]} query]
      (let [dependency-vector (dep-vec artifact version)
            provides (mapcat :provides (:foreign-libs deps))
            main-ns (-> deps :foreign-libs first :provides first)
            readme-url (str "https://github.com/cljsjs/packages/tree/master/" artifact)]
        [:li.ba.mb3.b--black-20.br1
         {:key artifact}
         [:div.pointer.mb2
          [select-on-click-input dependency-vector query]]

         [:p.pa3.ma0.lh-copy
          [:span.mr2 [hi/highlight-string description query]]
          [:button.btn-reset.blue.pa0
           {:on-click #(swap! expanded? not)}
           (if @expanded? "Hide Instructions ×" "Show Usage Instructions »")]]

         (when @expanded?
           [:div.mt2.pa3.lh-copy.bg-near-white.bt.bb.b--black-20
            [:h4.ma0.mb3 "Using the " [code cljsjs-group "/" artifact] " package"]
            (into
              [:ol
               [:li "Add the dependency coordinates " [code dependency-vector] " to the list of " [code ":dependencies"] " in your project."]]
              (case package-type
                :foreign-lib [[:li "Make sure to require " [code main-ns] " somewhere in your project so it is added to your compiled ClojureScript code."
                               [code
                                [:pre
                                 "(ns your.namespace\n"
                                 "  (:require [" main-ns "]))"]]]
                              (if (seq (rest provides))
                                [:li "This package also provides " (count (rest provides)) " other namespaces, check "
                                 [:a.dib.link.normal.blue {:href readme-url :target "new"} "Readme"] " or deps.cljs for more information."])
                              [:li "You can now use your newly added library by accessing it through the global Javascript namespace, e.g." [code "js/React"]
                               "Please check the project's documentation to find out what global the library uses. "
                               [:strong "Please note: "] "You can not use " [code ":as"] " or " [code ":refer"] " with CLJSJS dependencies."]]
                :closure-lib [[:li "This package is provided as Closure library. Check " [:a.dib.link.normal.blue {:href readme-url} "Readme"] " for usage information."]]))])

         [:div.cf.mb0-ns.mb2
          [:a.pa3-ns.pv2.ph3.dib.link.normal.blue {:href homepage :target "new"} "Project Site"]
          [:a.pa3-ns.pv2.ph3.dib.link.normal.blue {:href readme-url :target "new"} "Package Readme"]
          [:a.pa3-ns.pv2.ph3.dib.link.normal.blue {:href (str "https://clojars.org/" cljsjs-group "/" artifact)} "Clojars"]
          [:button.btn-reset.pa3-ns.pv2.ph3.dib.blue {:on-click #(swap! show-cljs-edn? not)} "cljs.edn (advanced)"]]

         (when @show-cljs-edn?
           [:pre.deps.pa3.ma0.bt.b--black-20 (with-out-str (pprint/pprint deps))])]))))

(defn package-list []
  (let [query @search]
    [:ul.list.pl0
     (for [pkg-info @(r/track filtered-packages)]
       ^{:key (:artifact pkg-info)} [package pkg-info query])]))

(defn main []
  [:div
   [:h3.f2.fw3 "Packages"]
   [search-input]
   [package-types]
   [package-list]])

(defn init! []
  (load-packages)
  (r/render-component [main] (js/window.document.getElementById "app")))

(init!)
