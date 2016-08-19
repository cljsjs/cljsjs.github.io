(ns frontend.core
  (:require [reagent.core :as r]
            [komponentit.autocomplete :as ac]
            [komponentit.highlight :as hi]
            cljsjs.clipboard))

(defonce packages (r/atom nil))
(defonce search (r/atom nil))

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

(def term-match-fn (ac/create-matcher [:jar_name :description]))

(defn filtered-packages []
  (let [current-packages @packages
        query @search]
    (filter #(ac/query-match? term-match-fn % query) current-packages)))

(defn search-input []
  (let [temp (r/atom @search)
        timeout (atom nil)]
    (fn []
      [:input
       {:class "twelve columns"
        :type "text"
        :placeholder "Search"
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
  (let [el (atom nil)]
    (fn [text]
      [:input
       {:type "text"
        :default-value text
        :ref #(reset! el %)
        :on-click (fn [_]
                    (if @el (.select @el)))}])))

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

(defn package-list []
  (let [query @search]
    [:ul
     (for [{:keys [jar_name description homepage latest_version deps]} @(r/track filtered-packages)]
       (let [group_name "cljsjs"
             id (str group_name "/" jar_name)
             dependency-vector (str "[" id " \"" latest_version "\"]")]
         [:li
          {:key id}
          [:a {:href (str "https://clojars.org/" id)}
           [hi/highlight-string jar_name query]]
          " "
          [:a {:href homepage :target "new"} [:i.fa.fa-home]]
          [:span.clojars
           [select-on-click-input dependency-vector]
           [copy-button
            {:text dependency-vector}
            [:i.fa.fa-copy]]]
          [:p.description [hi/highlight-string description query]]
          [:pre.deps deps]]))]))

(defn main []
  [:div
   [:h3 "Packages"]
   [search-input]
   [package-list]])

(defn init! []
  (load-packages)
  (r/render-component [main] (js/window.document.getElementById "app")))

(init!)
