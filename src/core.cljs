(ns reagent-transition-group.core
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require
   [cljsjs.d3 :as d3]
   [cljs.core.async :refer [put! chan <! >! timeout close!]]
   [reagent.core :as r]
   [cljsjs.react-transition-group]))

(def tg (r/adapt-react-class js/ReactTransitionGroup.TransitionGroup))

(defonce state (r/atom (cycle ["comp1" "comp2"])))

(def ani-chan (chan))
(go-loop [old-data {}]
  (let [data (merge old-data (<! ani-chan))]
    ; Wait until both entering and leaving elements had provided their data
    (if (every? data [:enter :leave])
      (let [{:keys [enter leave
                    enter-cb leave-cb
                    enter-height leave-height]} data]
        (.. js/d3
            (select leave)
            (transition)
            (style "opacity" 0)
            (on "end"
                (fn []
                  (leave-cb)
                  (.. js/d3
                      (select enter)
                      (style "display" "block")
                      (style "height" (str leave-height "px"))
                      (transition)
                      (style "opacity" 1)
                      (style "height" (str enter-height "px"))
                      (on "end" enter-cb)))))
        (recur {}))
      (recur data))))

(defn will-leave [c cb]
  (let [d (r/dom-node c)
        rect (.getBoundingClientRect d)
        height (- (.-bottom rect) (.-top rect))]
    (put! ani-chan {:leave d :leave-cb cb :leave-height height})))

(defn will-enter [c cb]
  ; Need to hide before sending to channel otherwise element is visible for a
  ; fraction of a second.
  (let [d (r/dom-node c)
        rect (.getBoundingClientRect d)
        height (- (.-bottom rect) (.-top rect))]
    (.. js/d3
        (select d)
        (style "display" "none")
        (style "opacity" 0))
    (put! ani-chan {:enter d :enter-cb cb :enter-height height})))

(defn comp1 []
  (let [c (r/current-component)]
    (doto c
      (aset "componentWillEnter" (partial will-enter c))
      (aset "componentWillLeave" (partial will-leave c))))
  [:h1 (apply str (repeat 50 "Component 1 "))])

(defn comp2 []
  (let [c (r/current-component)]
    (doto c
      (aset "componentWillEnter" (partial will-enter c))
      (aset "componentWillLeave" (partial will-leave c))))
  [:h1 (apply str (repeat 20 "Component 2 "))])

(defn app []
  [:div {:style {:border "1px solid black"}}
   [tg (case (first @state)
         "comp1"
         ^{:key "comp1"} [comp1]
         "comp2"
         ^{:key "comp2"} [comp2])]
   [:button {:on-click #(swap! state next)} "Switch"]])

(r/render [app] (js/document.getElementById "app"))
