
(ns app.comp.viewer
  (:require [hsl.core :refer [hsl]]
            [respo-ui.core :as ui]
            [respo.core
             :refer
             [defcomp cursor-> action-> list-> mutation-> <> div button textarea span]]
            [respo.comp.space :refer [=<]]
            [reel.comp.reel :refer [comp-reel]]
            [respo-md.comp.md :refer [comp-md]]
            [app.config :refer [dev?]]
            [feather.core :refer [comp-i]]))

(defcomp
 comp-rules
 (rules path selected-idx)
 (list->
  {:style {:overflow :auto,
           :max-height "calc(100% - 80px)",
           :margin "auto 4",
           :border "1px solid #eee",
           :flex-shrink 0}}
  (->> rules
       (map-indexed
        (fn [idx rule]
          [(:path rule)
           (div
            {:on-click (fn [e d! m!] (d! :set-path (conj path idx))),
             :style (merge
                     {:border-bottom "1px solid #eee", :padding "4px 8px", :cursor :pointer}
                     (if (= selected-idx idx) {:background-color (hsl 0 0 90)}))}
            (div
             {:style (merge ui/row-parted {})}
             (span
              {}
              (<> (:path rule) {:font-family ui/font-code, :font-size 13})
              (=< 8 nil)
              (<> (or (:name rule) "-") {:color (hsl 0 0 80), :font-size 12}))
             (=< 16 nil)
             (if (some? (:next rule))
               (<> (count (:next rule)) {:font-family ui/font-fancy, :color (hsl 0 0 80)}))))])))))

(defn decorate-rules [acc rules path level original-path]
  (if (nil? rules)
    acc
    (let [rule (get rules (first path)), this-path (vec (take level original-path))]
      (if (empty? path)
        (conj acc [level (comp-rules rules this-path nil)])
        (recur
         (conj acc [level (comp-rules rules this-path (first path))])
         (:next rule)
         (rest path)
         (inc level)
         original-path)))))

(defcomp
 comp-viewer
 (rules path)
 (list->
  {:style (merge ui/flex ui/row-middle {:padding 8, :overflow :auto})}
  (decorate-rules [] rules path 0 path)))
