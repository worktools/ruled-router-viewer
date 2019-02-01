
(ns app.comp.container
  (:require [hsl.core :refer [hsl]]
            [respo-ui.core :as ui]
            [respo.core
             :refer
             [defcomp cursor-> action-> mutation-> <> div button textarea span]]
            [respo.comp.space :refer [=<]]
            [reel.comp.reel :refer [comp-reel]]
            [respo.comp.inspect :refer [comp-inspect]]
            [respo-md.comp.md :refer [comp-md]]
            [app.config :refer [dev?]]
            [app.comp.viewer :refer [comp-viewer]]
            [feather.core :refer [comp-i]]
            [inflow-popup.comp.popup :refer [comp-popup]]
            [cljs.reader :refer [read-string]]))

(defn generate-url [acc rules path]
  (if (or (nil? rules) (empty? path))
    acc
    (let [rule (get rules (first path))]
      (recur (str acc "/" (:path rule)) (:next rule) (rest path)))))

(defcomp
 comp-navbar
 (states initial-data path)
 (let [state (or (:data states) {:draft (js/JSON.stringify (clj->js initial-data) nil 2)})
       url (generate-url "" initial-data path)]
   (div
    {:style (merge ui/row-middle {:height 40, :padding 8, :border-bottom "1px solid #eee"})}
    (cursor->
     :draft
     comp-popup
     states
     {:trigger (comp-i :codepen 20 (hsl 0 0 70))}
     (fn [toggle!]
       (div
        {:style ui/column}
        (textarea
         {:value (:draft state),
          :style (merge ui/textarea {:width 600, :height 320, :font-family ui/font-code}),
          :on-input (fn [e d! m!] (m! %cursor (assoc state :draft (:value e))))})
        (=< nil 8)
        (div
         {:style ui/row-parted}
         (span {})
         (button
          {:style ui/button,
           :inner-text "Submit",
           :on-click (fn [e d! m!]
             (d! :set-data (js->clj (js/JSON.parse (:draft state)) :keywordize-keys true))
             (m! %cursor nil)
             (toggle! m!))})))))
    (=< 16 nil)
    (<> url {:font-size 12, :font-family ui/font-code, :color (hsl 0 0 70)}))))

(defcomp
 comp-container
 (reel)
 (let [store (:store reel), states (:states store)]
   (div
    {:style (merge ui/global ui/fullscreen ui/column)}
    (comp-navbar states (:data store) (or (:path store) []))
    (comp-viewer (:data store) (or (:path store) []))
    (when dev? (cursor-> :reel comp-reel states reel {}))
    (when dev? (comp-inspect "store" store {:bottom 0})))))
