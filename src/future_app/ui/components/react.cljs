(ns future-app.ui.components.react
  (:require [reagent.core :as r]))

(def ReactNative (js/require "react-native"))

(def text (r/adapt-react-class (.-Text ReactNative)))
(def view (r/adapt-react-class (.-View ReactNative)))
(def flat-list (r/adapt-react-class (.-FlatList ReactNative)))
