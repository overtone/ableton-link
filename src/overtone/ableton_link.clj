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
   (-ctor AbletonLink_ctor [] void*)
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

#_(defn -main []
    (println "devTEST")
    (-enable-link true)
    (println "isneabled? " (-is-enabled))
    (println "FINISH"))

;; (def ableton-link (new AbletonLink))

(s/fdef enable-link :args (s/cat :bool boolean?))

(defonce -AL-pointer (-ctor))

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


