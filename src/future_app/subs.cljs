(ns future-app.subs
  (:require [re-frame.core :refer [reg-sub]]))

(reg-sub
  :get-greeting
  (fn [db _]
    (:greeting db)))

(reg-sub
 :get-recipe-names
 (fn [db _]
   (:recipe-names db)))