(ns overtone.ableton-link
  (:require [clj-native.direct :refer [defclib loadlib]]
            [clj-native.structs :refer [byref]]
            [clj-native.callbacks :refer [callback]]
            [clojure.spec.alpha :as s])
  ;;(:import [AbletonLink])
  )

(defn- os-name
  "Returns a string representing the current operating system. Useful
   for debugging, etc. Prefer get-os for os-specific logic."
  []
  (System/getProperty "os.name"))

(defn- get-os
  "Return the OS as a keyword. One of :windows :linux :mac"
  []
  (let [os (os-name)]
    (cond
      (re-find #"[Ww]indows" os) :windows
      (re-find #"[Ll]inux" os)   :linux
      (re-find #"[Mm]ac" os)     :mac)))


(defonce __SET_JNA_PATH__
  (case (get-os)
    :linux (System/setProperty "jna.library.path" "native/linux/x86_64")
    :mac (System/setProperty "jna.library.path" "native/macosx/x86_64")
    :windows (System/setProperty "jna.library.path" "native/windows/x86_64")))


(defclib lib-abletonlink
  (:libname "abletonlink")
  (:functions
   (-enable-link AbletonLink_enable [bool] void)
   (-is-enabled AbletonLink_isEnabled [] bool)
   (-get-beat AbletonLink_getBeat [] double)
   (-set-beat AbletonLink_setBeat [double] void)
   (-set-beat-force AbletonLink_setBeatForce [double] void)
   (-get-phase AbletonLink_getPhase [] double)
   (-get-bpm AbletonLink_getBpm [] double)
   (-set-bpm AbletonLink_setBpm [double] void)
   (-get-num-peers AbletonLink_getNumPeers [] int)
   (-get-quantum AbletonLink_getQuantum [] double)
   (-set-quantum AbletonLink_setQuantum [double] void)
   (-update AbletonLink_update [] void)))


(loadlib lib-abletonlink)

#_(defn -main []
    (println "devTEST")
    (-enable-link true)
    (println "isneabled? " (-is-enabled))
    (println "FINISH"))

;; (def ableton-link (new AbletonLink))

(s/fdef enable-link :args (s/cat :bool boolean?))

(defn enable-link
  "Enable link"
  [bool]
  (-enable-link bool))

(defn link-enabled?
  "Returns true if link is enabled"
  []
  (-is-enabled))

(defn get-beat
  "Sync(update) with link and
   return the current bpm"
  []
  (-update)
  (-get-beat))

(s/fdef set-beat :args (s/cat :beat number?))

(defn set-beat
  "Globally set the value of the beat (number)"
  [beat]
  (-set-beat beat))

(s/fdef set-beat-force :args (s/cat :beat number?))

(defn set-beat-force
  "Forcefully and globally set
   the value of the beat (number)"
  [beat]
  (-set-beat-force beat))

(defn get-bpm
  "Get the current global bpm"
  []
  (-update)
  (-get-bpm))

(s/fdef set-bpm :args (s/cat :bpm number?))

(defn set-bpm
  "Globally change the bpm on the link"
  [bpm]
  (-set-bpm bpm))

(defn get-num-peers
  "Get number of connected peers"
  []
  (-get-num-peers))

(s/fdef set-quantum :args (s/cat :quantum number?))

(defn set-quantum
  "Sets the quantum, in a way like setting a
   time-signature of a bar"
  [quantum]
  (-set-quantum quantum))


(defn get-quantum
  "Get the quantum of a bar, returns number,
   (return number of beats in a bar)"
  []
  (-update)
  (-get-quantum))


