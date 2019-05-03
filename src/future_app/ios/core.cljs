(ns future-app.ios.core
  (:require [reagent.core :as r :refer [atom]]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [future-app.ui.components.react :as react]
            [future-app.events]
            [future-app.subs]))

(def ReactNative (js/require "react-native"))

(def LuggageCore (js/require "@luggage/core"))
(def Luggage (.-default LuggageCore))
(def DropboxBacked (.-DropboxXMLHttpBackend LuggageCore))

(def app-registry (.-AppRegistry ReactNative))

(defn alert [title]
  (.alert (.-Alert ReactNative) title))

(defonce api-key "w7gUxoguV8wAAAAAAAATX2oQZGifNx-zCjFurDpH6n-KMN0zA_40QkWsL8fOgqcw")

(def recipes
  (-> api-key
      (DropboxBacked.)
      (Luggage.)
      (.collection "recipes")))

(defn handle-recipes-load
  [recipes]
  (as-> recipes r
    (js->clj r :keywordize-keys true)
    (mapv :title r)
    (dispatch [:set-recipe-names r])))

(defn fetch-recipes []
  (-> recipes
      (.read)
      (.then handle-recipes-load)
      (.catch println)))

(defn recipes-component []
  (let [recipe-names (subscribe [:get-recipe-names])]
    (fn []
      [react/view
       [react/flat-list {:data (clj->js @recipe-names)
                         :key-extractor (fn [recipe _] recipe)
                         :render-item (fn [e]
                                        (let [recipe (aget e "item")]
                                          (r/as-element [react/text {:style {:font-size 18}} recipe])))}]])))

(defn app-root []
  (let [greeting (subscribe [:get-greeting])]
    (fn []
      [react/view {:style {:flex-direction "column" :margin 40 :align-items "center"}}
       [recipes-component]])))

(defn init []
  (fetch-recipes)
  (dispatch-sync [:initialize-db])
  (.registerComponent app-registry "FutureApp" #(r/reactify-component app-root)))

(dispatch [:set-greeting "Привет, Алексей!!!"])
