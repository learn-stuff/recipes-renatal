(ns future-app.ios.core
  (:require [reagent.core :as r :refer [atom]]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [future-app.events]
            [future-app.subs]))

(def ReactNative (js/require "react-native"))

(def LuggageCore (js/require "@luggage/core"))
(def Luggage (.-default LuggageCore))
(def DropboxBacked (.-DropboxXMLHttpBackend LuggageCore))

(def app-registry (.-AppRegistry ReactNative))
(def text (r/adapt-react-class (.-Text ReactNative)))
(def view (r/adapt-react-class (.-View ReactNative)))
(def image (r/adapt-react-class (.-Image ReactNative)))
(def touchable-highlight (r/adapt-react-class (.-TouchableHighlight ReactNative)))

(def logo-img (js/require "./images/cljs.png"))

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
      [view
       (for [recipe @recipe-names]
         ^{:key recipe} [text recipe])])))

(defn app-root []
  (let [greeting (subscribe [:get-greeting])]
    (fn []
      [view {:style {:flex-direction "column" :margin 40 :align-items "center"}}
       [text {:style {:font-size 30 :font-weight "100" :margin-bottom 20 :text-align "center"}} @greeting]
       [image {:source logo-img
               :style  {:width 80 :height 80 :margin-bottom 30}}]
       [touchable-highlight {:style {:background-color "#999" :padding 10 :border-radius 5}
                             :on-press #(alert "HELLO!")}
        [text {:style {:color "white" :text-align "center" :font-weight "bold"}} "press me"]]
       [recipes-component]])))

(defn init []
  (fetch-recipes)
  (dispatch-sync [:initialize-db])
  (.registerComponent app-registry "FutureApp" #(r/reactify-component app-root)))

(dispatch [:set-greeting "Привет, Алексей!!!"])