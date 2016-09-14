(ns frontend.core
  (:require [reagent.core :as r]
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
         [:span.f4.code text]
         [:span.ml1.f6.black-50 (if @copied? "(copied!)" "(click to copy)")]])})))

(defn copy-button
  [_ & _]
  (r/create-class
    {:component-did-mount
     (fn [this]
       (js/Clipboard. (r/dom-node this)))
     :reagent-render
     (fn [{:keys [text]} & children]
       (into
         [:button
          {:type "text"
           :data-clipboard-text text}]
         children))}))

(defn dep-vec [artifact version]
  (str "[" cljsjs-group "/" artifact " \"" version "\"]"))

(defn package-list []
  (let [query @search]
    [:ul
     (for [{:keys [artifact description homepage version deps]} @(r/track filtered-packages)]
       (let [dependency-vector (dep-vec artifact version)]
         [:li
          {:key artifact}
          [:a {:href (str "https://clojars.org/" cljsjs-group "/" artifact)}
           [hi/highlight-string artifact query]]
          " "
          [:a {:href (str "https://github.com/cljsjs/packages/tree/master/" artifact) :target "new"} [:i.fa.fa-book]]
          " "
          [:a {:href homepage :target "new"} [:i.fa.fa-home]]
          [:span.clojars
           [select-on-click-input dependency-vector]
           [copy-button
            {:text dependency-vector}
            [:i.fa.fa-copy]]]
          [:p.description [hi/highlight-string description query]]
          [:pre.deps deps]]))]))

(defn package-list' []
  (let [query @search]
    [:ul.list.pl0
     (for [{:keys [artifact description homepage version deps]} @(r/track filtered-packages)]
       (let [dependency-vector (dep-vec artifact version)]
         [:li.ba.mb3.b--black-20
          {:key artifact}
          ;; [:a {:href (str "https://clojars.org/" cljsjs-group "/" artifact)}
          ;;  [hi/highlight-string artifact query]]
          ;; " "
          ;; [:a {:href (str "https://github.com/cljsjs/packages/tree/master/" artifact) :target "new"} [:i.fa.fa-book]]
          ;; " "
          ;; [:a {:href homepage :target "new"} [:i.fa.fa-home]]

          [:div
           [select-on-click-input dependency-vector]
           #_[copy-button
            {:text dependency-vector}
            [:i.fa.fa-copy]]]

          [:p.pa3.ma0 [hi/highlight-string description query]]

          [:div.cf.bt.b--black-20
           [:a {:href homepage :target "new"}
            [:div.fl-ns.w-33-ns.w-100.pv2.ph3.br-ns.dim "Project Site"]]
           [:a {:href (str "https://github.com/cljsjs/packages/tree/master/" artifact) :target "new"}
            [:div.fl-ns.w-33-ns.w-100.pv2.ph3.br-ns.dim "Package Readme"]]
           [:a {:href (str "https://clojars.org/" cljsjs-group "/" artifact)}
            [:div.fl-ns.w-33-ns.w-100.pv2.ph3.dim "Clojars"]]]
          ;; project site
          ;; package readme
          ;; clojars

          #_[:pre.deps deps]]))]))

(defn main []
  [:div
   [:h3 "Packages"]
   [search-input]
   [package-list']])

(defn init! []
  (load-packages)
  (r/render-component [main] (js/window.document.getElementById "app")))

(init!)