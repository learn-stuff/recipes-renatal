(ns future-app.ui.components.token
  (:require [reagent.core :as r]
            [future-app.ui.components.react :as react]))

(def DropboxAutent (js/require "dropbox-autent"))
(def DropboxAutentHoc (.-default DropboxAutent))

(def ^:private api-key "9jllu8tntncc6ic")
(def ^:private redirect-url "http://localhost/callback")

(def dropbox-autent (.-default DropboxAutent))
(def dropbox-autent-hoc (dropbox-autent #js{:apiKey api-key :redirectUrl redirect-url}))

(defn make-dropbox-token-component [wrapped-component]
  (-> wrapped-component
      r/reactify-component
      dropbox-autent-hoc
      r/adapt-react-class))
