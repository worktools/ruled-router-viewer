
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

(defn path-contains? [path coord]
  (cond
    (empty? coord) true
    (nil? path) false
    (empty? path) false
    (= (first path) (first coord)) (recur (rest path) (rest coord))
    :else false))

(defcomp
 comp-rules
 (rules path coord)
 (list->
  {:style {:overflow :auto,
           :max-height "calc(100% - 80px)",
           :margin "4px 4px",
           :border-left (str "1px solid " (hsl 0 0 90)),
           :flex-shrink 0,
           :border-radius "6px"},
   :class-name "hover-highlight"}
  (->> rules
       (map-indexed
        (fn [idx rule]
          [(:path rule)
           (let [my-coord (conj coord idx), selected? (path-contains? path my-coord)]
             (div
              {:style ui/row-middle}
              (div
               {:on-click (fn [e d! m!] (d! :set-path (conj coord idx))),
                :style (merge
                        {:padding "0px 8px", :cursor :pointer}
                        (if selected? {:background-color (hsl 0 0 90)}))}
               (div
                {:style (merge ui/row-parted {})}
                (span
                 {}
                 (<> (:path rule) {:font-family ui/font-code, :font-size 13})
                 (=< 8 nil)
                 (<> (or (:name rule) "-") {:color (hsl 0 0 80), :font-size 12}))
                (=< 16 nil)
                (if (some? (:next rule))
                  (<> (count (:next rule)) {:font-family ui/font-fancy, :color (hsl 0 0 80)}))))
              (comp-rules (:next rule) (if selected? path nil) my-coord)))])))))

(defcomp comp-viewer (rules path) (comp-rules rules path []))
