(ns om-routing-demo.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [clojure.string :as s]))

(enable-console-print!)

(def path-to-your-file
  "file:///Users/wls/code/clojure/cljs/om-routing-demo/index.html?page=" )

(defn get-component [data]
  (condp = (:route-data data)
    :index index
    :other-page other-page
    four-oh-four))

(def app-state (atom {:text "Hello world!"
                      :route-data nil}))

(defn set-route! [app-state]
  (let [location (.. js/document -location)
        route (-> (re-find #"page.*" location)
                  (s/split #"=")
                  last
                  keyword)]
    (swap! app-state assoc :route-data route)))

(defn redirect [path]
  (.pushState js/history {placeholder: "placeholder"} "placeholder" path)
  (.dispatchEvent js/window (js/Event. "popstate")))

(defn init-state []
  (set-route! app-state)
  (.addEventListener js/window "popstate" (set-route! app-state)))

(defn partial-links-view [data owner]
  (dom/div nil
           (dom/a #js {:onClick #(redirect (str path-to-your-file "index"))} "Go to Index")
           (dom/br nil)
           (dom/a #js {:onClick #(redirect (str path-to-your-file "other-page"))} "Go to \"The Other Page\"")))

(defn index [data owner]
  (om/component
   (dom/div nil
            (dom/h1 nil "I am the index"))))

(defn other-page [data owner]
  (om/component
   (dom/div nil
            (dom/h1 nil "I am the other page"))))

(defn four-oh-four [data owner]
  (om/component
   (dom/div nil
            (dom/h1 nil "404"))))

(defn app [data owner]
  (reify
    om/IRender
    (render [_]
      (let [component-to-be-rendered (get-component data)]
        (dom/div nil
                 (om/build component-to-be-rendered data)
                 (om/build partial-links-view data))))))

(init-state)

(om/root
  app
  app-state
  {:target (. js/document (getElementById "app"))})
