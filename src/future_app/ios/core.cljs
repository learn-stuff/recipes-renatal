(ns future-app.ios.core
  (:require [reagent.core :as r :refer [atom]]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [future-app.ui.components.react :as react]
            [future-app.ui.components.token :as token]
            [future-app.events]
            [future-app.subs]))

(def ReactNative (js/require "react-native"))

(def LuggageCore (js/require "@luggage/core"))
(def Luggage (.-default LuggageCore))
(def DropboxBacked (.-DropboxXMLHttpBackend LuggageCore))

(def app-registry (.-AppRegistry ReactNative))

(defn alert [title]
  (.alert (.-Alert ReactNative) title))

(defn recipes
  [token]
  (-> token
      (DropboxBacked.)
      (Luggage.)
      (.collection "recipes")))

(defn handle-recipes-load
  [recipes]
  (as-> recipes r
    (js->clj r :keywordize-keys true)
    (mapv :title r)
    (dispatch [:set-recipe-names r])))

(defn fetch-recipes
  [token]
  (-> token
      recipes
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

(def with-token-component
  (token/make-dropbox-token-component
   (r/create-class
    {:component-did-mount
     (fn [comp]
       (let [props (.-props comp)
             token (aget props "token")]
         (fetch-recipes token)))

     :reagent-render
     (fn [props]
       [recipes-component])})))

(defn app-root []
  (let [greeting (subscribe [:get-greeting])]
    (fn []
      [with-token-component])))

(defn init []
  (dispatch-sync [:initialize-db])
  (.registerComponent app-registry "FutureApp" #(r/reactify-component app-root)))

(dispatch [:set-greeting "Привет, Алексей!!!"])
