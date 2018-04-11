(ns overtone.ableton-link
  (:import [AbletonLink]))

(def ableton-link (new AbletonLink))

(defn enable-link
  "Enable link"
  [bool]
  (.enable ableton-link bool))

(defn link-enabled?
  "Returns true if link is enabled"
  []
  (.isEnabled ableton-link))

(defn set-bpm
  "Globally change the bpm on the link"
  [bpm]
  (.setBpm ableton-link bpm))

(defn get-bpm
  "Get the current global bpm"
  []
  (.update ableton-link)
  (.getBpm ableton-link))

(defn get-beat
  "Sync(update) with link and
   return the current bpm"
  []
  (.update ableton-link)
  (.getBeat ableton-link))

(defn set-beat
  "Globally set the value of the beat (number)"
  [beat]
  (.setBeat ableton-link))

(defn set-beat-force
  "Forcefully and globally set
   the value of the beat (number)"
  [beat]
  (.setBeatForce ableton-link))

(defn get-num-peers
  "Get number of connected peers"
  []
  (.getNumPeers ableton-link))

(defn set-quantum
  "Sets the quantum, in a way like setting a
   time-signature of a bar"
  [quantum]
  (.setQuantum ableton-link quantum))

(defn get-quantum
  "Get the quantum of a bar, returns number,
   (return number of beats in a bar)"
  []
  (.update ableton-link)
  (.getQuantum ableton-link))

