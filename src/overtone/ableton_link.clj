(ns overtone.ableton-link
  (:require overtone.link-jna-path
            [clj-native.direct :refer [defclib loadlib]]
            [clj-native.structs :refer [byref]]
            [clj-native.callbacks :refer [callback]]
            [clojure.spec.alpha :as s]))


(defclib lib-abletonlink
  (:libname "abletonlink")
  (:functions
   (-AbletonLink_ctor AbletonLink_ctor [] void*)
   (-enable-link AbletonLink_enable [void* bool] void)
   (-is-enabled AbletonLink_isEnabled [void* ] bool)
   (-get-beat AbletonLink_getBeat [void*] double)
   (-set-beat AbletonLink_setBeat [void* double] void)
   (-set-beat-force AbletonLink_setBeatForce [void* double] void)
   (-get-phase AbletonLink_getPhase [void*] double)
   (-get-bpm AbletonLink_getBpm [void*] double)
   (-set-bpm AbletonLink_setBpm [void* double] void)
   (-get-num-peers AbletonLink_getNumPeers [void*] int)
   (-get-quantum AbletonLink_getQuantum [void*] double)
   (-set-quantum AbletonLink_setQuantum [void* double] void)
   (-update AbletonLink_update [void*] void)))


(loadlib lib-abletonlink)

(s/fdef enable-link :args (s/cat :bool boolean?))

(defonce -AL-pointer (-AbletonLink_ctor))

(defn enable-link
  "Enable link"
  [bool]
  (-enable-link -AL-pointer bool))

(defn link-enabled?
  "Returns true if link is enabled"
  []
  (-is-enabled -AL-pointer))

(defn get-beat
  "Sync(update) with link and
   return the current bpm"
  []
  (-update -AL-pointer)
  (-get-beat -AL-pointer))

(s/fdef set-beat :args (s/cat :beat number?))

(defn set-beat
  "Globally set the value of the beat (number)"
  [beat]
  (-set-beat -AL-pointer beat))

(s/fdef set-beat-force :args (s/cat :beat number?))

(defn set-beat-force
  "Forcefully and globally set
   the value of the beat (number)"
  [beat]
  (-set-beat-force -AL-pointer beat))

(defn get-phase
  "Get the current phase of a bar"
  []
  (-update -AL-pointer)
  (-get-phase -AL-pointer))

(defn get-bpm
  "Get the current global bpm"
  []
  (-update -AL-pointer)
  (-get-bpm -AL-pointer))

(s/fdef set-bpm :args (s/cat :bpm number?))

(defn set-bpm
  "Globally change the bpm on the link"
  [bpm]
  (-set-bpm -AL-pointer bpm))

(defn get-num-peers
  "Get number of connected peers"
  []
  (-get-num-peers -AL-pointer))

(s/fdef set-quantum :args (s/cat :quantum number?))

(defn set-quantum
  "Sets the quantum, in a way like setting a
   time-signature of a bar"
  [quantum]
  (-set-quantum -AL-pointer quantum))


(defn get-quantum
  "Get the quantum of a bar, returns number,
   (return number of beats in a bar)"
  []
  (-update -AL-pointer)
  (-get-quantum -AL-pointer))

