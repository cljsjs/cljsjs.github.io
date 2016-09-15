(ns frontend.core
  (:require [reagent.core :as r]
            [cljs.reader :as reader]
            [komponentit.autocomplete :as ac]
            [komponentit.highlight :as hi]
            cljsjs.clipboard))

(defonce packages (r/atom nil))
(defonce search (r/atom nil))

(def cljsjs-group "cljsjs")

(defn load-packages []
  (let [req  (js/XMLHttpRequest.)]
    (set! (.-responseType req) "json")
    (doto req
      (.addEventListener "load" (fn []
                                  (reset! packages (mapv (fn [obj]
                                                           (js->clj obj :keywordize-keys true))
                                                         (.-response req)))))
      (.open "GET" "data.json")
      (.send))))

(def term-match-fn (ac/create-matcher [:artifact :description]))

(defn filtered-packages []
  (let [current-packages @packages
        query @search]
    (filter #(ac/query-match? term-match-fn % query) current-packages)))

(defn search-input []
  (let [temp (r/atom @search)
        timeout (atom nil)]
    (fn []
      [:input.w-100.pa3
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

(defn select-on-click-input [text]
  (let [copied? (r/atom nil)]
    (r/create-class
     {:component-did-mount
      (fn [this]
        (js/Clipboard. (r/dom-node this)))
      :reagent-render
      (fn [text]
        [:div.dim.pa3.bb.b--black-20
         {:data-clipboard-text text
          :on-click #(reset! copied? true)}
         [:div.mv2
          [:span.f4.code text]
          [:span.ml1.f6.black-50 (if @copied? "(copied!)" "(click to copy)")]]])})))

(defn dep-vec [artifact version]
  (str "[" cljsjs-group "/" artifact " \"" version "\"]"))

(defn code [& contents]
  (into [:code.blue] contents))

(defn package [_]
  (let [expanded?      (r/atom false)
        show-cljs-edn? (r/atom false)]
    (fn package-render [{:keys [artifact description homepage version deps]} query]
      (let [dependency-vector (dep-vec artifact version)
            provides (-> deps reader/read-string :foreign-libs first :provides first)]
        [:li.ba.mb3.b--black-20.br1
         {:key artifact}

         [:div.pointer.mb2
          [select-on-click-input dependency-vector]]

         [:p.pa3.ma0.lh-copy
          [hi/highlight-string description query]
          [:button.btn-reset.blue
           {:on-click #(swap! expanded? not)}
           (if @expanded? "Hide Instructions" "Show Usage Instructions »")]]

         (when @expanded?
           [:div.mt2.pa3.lh-copy.bg-near-white.bt.bb.b--black-20
            [:h4.ma0.mb3 "Using the " [code cljsjs-group "/" artifact] " package"]
            [:ol
             [:li "Add the dependency coordinates " [code dependency-vector] " to the list of " [code ":dependencies"] " in your project."]
             [:li "Make sure to require " [code provides] " somewhere in your project so it is added to your compiled ClojureScript code."]
             [:li "You can now use your newly added library by accessing it through the global Javascript namepsace, please check the project site to find out what global the library uses."]]])

         [:div.cf.mb0-ns.mb2
          [:a.pa3-ns.pv2.ph3.dib.link.normal.blue {:href homepage :target "new"} "Project Site"]
          [:a.pa3-ns.pv2.ph3.dib.link.normal.blue {:href (str "https://github.com/cljsjs/packages/tree/master/" artifact) :target "new"} "Package Readme"]
          [:a.pa3-ns.pv2.ph3.dib.link.normal.blue {:href (str "https://clojars.org/" cljsjs-group "/" artifact)} "Clojars"]
          [:button.btn-reset.pa3-ns.pv2.ph3.dib.blue {:on-click #(swap! show-cljs-edn? not)} "cljs.edn (advanced)"]]

         ;; TODO try this again with pretty printing
         (when @show-cljs-edn?
           [:pre.deps.pa3 deps])]))))

(defn package-list []
  (let [query @search]
    [:ul.list.pl0
     (for [pkg-info @(r/track filtered-packages)]
       ^{:key (:artifact pkg-info)} [package pkg-info query])]))

(defn main []
  [:div
   [:h3.f2.fw3 "Packages"]
   [search-input]
   [package-list]])

(defn init! []
  (load-packages)
  (r/render-component [main] (js/window.document.getElementById "app")))

(init!)