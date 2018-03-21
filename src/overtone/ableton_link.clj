(ns overtone.ableton-link
  (:import [AbletonLink]))

(def ableton-link (new AbletonLink))

(defn enable-link [bool]
  (.enable ableton-link bool))

(defn link-enabled? []
  (.isEnabled ableton-link))

(defn set-bpm [bpm]
  (.setBpm ableton-link bpm))

(defn get-bpm []
  (.update ableton-link)
  (.getBpm ableton-link))

(defn get-beat []
  (.update ableton-link)
  (.getBeat ableton-link))

(defn set-beat [beat]
  (.setBeat ableton-link))

(defn set-beat-force [beat]
  (.setBeatForce ableton-link))

(defn get-num-peers []
  (.getNumPeers ableton-link))

(defn set-quantum [quantum]
  (.setQuantum ableton-link quantum))

(defn get-quantum []
  (.update ableton-link)
  (.getQuantum ableton-link))

