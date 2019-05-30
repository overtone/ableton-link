;; Copyright (C) 2018, Hlöðver Sigurðsson

;; This program is free software: you can redistribute it and/or modify
;; it under the terms of the GNU General Public License as published by
;; the Free Software Foundation, either version 3 of the License, or
;; (at your option) any later version.

;; This program is distributed in the hope that it will be useful,
;; but WITHOUT ANY WARRANTY; without even the implied warranty of
;; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;; GNU General Public License for more details.

;; You should have received a copy of the GNU General Public License
;; along with this program.  If not, see <http://www.gnu.org/licenses/>.

(ns overtone.ableton-link
  (:require
   [clojure.data.priority-map :refer [priority-map]]
   [clojure.java.io :as io]
   [clojure.spec.alpha :as s]
   [tech.jna :as jna])
  (:import  [java.io File Writer]
            [com.sun.jna Native Platform Pointer IntegerType]
            [com.sun.jna.ptr IntByReference]
            [org.apache.commons.io FileUtils]))

(set! *warn-on-reflection* true)

(defn get-os
  "Return the OS as a keyword. One of :windows :linux :mac"
  []
  (let [os (System/getProperty "os.name")]
    (cond
      (re-find #"[Ww]indows" os) :windows
      (re-find #"[Ll]inux" os)   :linux
      (re-find #"[Mm]ac" os)     :mac)))

(defonce ^:private tmp-directory
  (let [tmp-dir (io/file (FileUtils/getTempDirectoryPath)
                         (str "libabletonlink" (System/currentTimeMillis)))]
    (.mkdirs tmp-dir)
    ;; windows tmp management can be so aggressive that this file is sometimes already delete
    (when-not (= :windows (get-os))
      (.addShutdownHook
       (Runtime/getRuntime)
       (Thread. #(FileUtils/deleteDirectory tmp-dir))))
    tmp-dir))

(defonce ^:private __SET_JNA_PATH__
  (System/setProperty
   "jna.library.path"
   (str (System/getProperty "jna.library.path") ":"
        (.getAbsolutePath ^File tmp-directory))))

(case (get-os)
  :linux (let [tmp-stdcxx (io/file tmp-directory "libstdc++.so.6")
               tmp-ableton (io/file tmp-directory "libabletonlink.so")]
           (with-open [in (io/input-stream (io/resource "linux/x86_64/libstdc++.so.6"))]
             (io/copy in tmp-stdcxx))
           (with-open [in (io/input-stream (io/resource "linux/x86_64/libabletonlink.so"))]
             (io/copy in tmp-ableton))
           (System/load (.getAbsolutePath tmp-stdcxx))
           (System/load (.getAbsolutePath tmp-ableton)))
  :windows (let [tmp-ableton (io/file tmp-directory "abletonlink.dll")]
             (with-open [in (io/input-stream (io/resource "windows/x86_64/abletonlink.dll"))]
               (io/copy in tmp-ableton))
             (System/load (.getAbsolutePath tmp-ableton)))
  :mac (let [tmp-ableton (io/file tmp-directory "libabletonlink.dylib")]
         (with-open [in (io/input-stream (io/resource "macosx/x86_64/libabletonlink.dylib"))]
           (io/copy in tmp-ableton))
         (System/load (.getAbsolutePath tmp-ableton)))
  (throw (Exception. (str "Unsupported Operating system: " (System/getProperty "os.name")))))

(jna/def-jna-fn "abletonlink" AbletonLink_ctor
  "AbletonLink Constructor"
  com.sun.jna.Pointer)

(jna/def-jna-fn "abletonlink" AbletonLink_enable
  ""
  nil
  [instance identity]
  [bool boolean])

(jna/def-jna-fn "abletonlink" AbletonLink_isEnabled
  ""
  Integer
  [instance identity])

(jna/def-jna-fn "abletonlink" AbletonLink_getBeat
  ""
  Double
  [instance identity])

(jna/def-jna-fn "abletonlink" AbletonLink_setBeatNow
  ""
  nil
  [instance identity]
  [new-beat double])

(jna/def-jna-fn "abletonlink" AbletonLink_setBeatAt
  ""
  nil
  [instance identity]
  [new-beat double]
  [timestamp double])

(jna/def-jna-fn "abletonlink" AbletonLink_setBeatAndQuantumNow
  ""
  nil
  [instance identity]
  [new-beat double]
  [quantum double])

(jna/def-jna-fn "abletonlink" AbletonLink_setBeatForce
  ""
  nil
  [instance identity]
  [new-beat double])

(jna/def-jna-fn "abletonlink" AbletonLink_getPhase
  ""
  Double
  [instance identity])

(jna/def-jna-fn "abletonlink" AbletonLink_getBpm
  ""
  Double
  [instance identity])

(jna/def-jna-fn "abletonlink" AbletonLink_setBpm
  ""
  nil
  [instance identity]
  [new-bpm double])

(jna/def-jna-fn "abletonlink" AbletonLink_getNumPeers
  ""
  Integer
  [instance identity])

(jna/def-jna-fn "abletonlink" AbletonLink_getQuantum
  ""
  Double
  [instance identity])

(jna/def-jna-fn "abletonlink" AbletonLink_setQuantum
  ""
  nil
  [instance identity]
  [new-quantum double])

(jna/def-jna-fn "abletonlink" AbletonLink_getTimestamp
  ""
  Long
  [instance identity])

(jna/def-jna-fn "abletonlink" AbletonLink_setIsPlayingNow
  ""
  nil
  [instance identity]
  [playing? boolean])

(jna/def-jna-fn "abletonlink" AbletonLink_setIsPlayingAt
  ""
  nil
  [instance identity]
  [is-playing-bbol boolean]
  [timestamp double])

(jna/def-jna-fn "abletonlink" AbletonLink_update
  ""
  nil
  [instance identity])

(with-out-str
  (defonce -AL-pointer (AbletonLink_ctor)))

(def ^:private link-running?
  "If not `nil` indicates that Link has been started, and holds a
  function that can be called to stop the clock thread."
  (atom nil))

(def ^:private event-queue-atom
  (atom (priority-map)))

(defmacro swallow-exceptions [& body]
  `(try ~@body (catch Exception e#)))

(defn- create-clock-thread
  "Creates the event handling thread which runs as long as the atom
  `run?` holds a truthy value."
  [run?]
  (future
    (while @run?
      (AbletonLink_update -AL-pointer)
      (let [cur-time (AbletonLink_getBeat -AL-pointer)]
        (while (and (not (empty? @event-queue-atom))
                    (<= (second (peek @event-queue-atom))
                        (AbletonLink_getBeat -AL-pointer)))
          (let [event (-> @event-queue-atom peek first)]
            (swap! event-queue-atom pop)
            (when-not @(:stop-atom event)
              (swallow-exceptions ((:fun event)))
              (when (:recurring event)
                (swap! event-queue-atom assoc event
                       (+ cur-time (:perioid event))))))))
      (Thread/sleep 1))))

(s/fdef enable-link :args (s/cat :bool boolean?))

(defn enable-link
  "Enable link if `bool` is `true`, disable it otherwise."
  [bool]
  (locking link-running?  ;; Protect against retries which could lead to extra event threads being spawned.
    (swap! link-running? (fn [state]
                           (if (true? bool)
                             ;; The caller wants us to be running.
                             (if state
                               state  ;; Already running, no need to change anything.
                               (let [run? (atom true)]  ;; Start Link and the clock thread.
                                 (create-clock-thread run?)
                                 (AbletonLink_enable -AL-pointer true)
                                 (fn [] (reset! run? false))))  ;; State becomes function that will stop clock thread.
                             ;; The caller wants us to be stopped.
                             (when state  ;; We only need to do anything if we were running.
                               (state)  ;; Call the function that stops the clock thread.
                               (AbletonLink_enable -AL-pointer false)
                               nil))))))  ;; State becomes nil, indicating we are no longer running.

(defn link-enabled?
  "Returns true if link is enabled"
  []
  (= 1 (AbletonLink_isEnabled -AL-pointer)))

(defn get-beat
  "Sync(update) with link and
   return the current bpm"
  []
  (AbletonLink_getBeat -AL-pointer))

(s/fdef set-beat-now :args (s/cat :beat number?))

(defn set-beat-now
  "Globally set the value of the beat (number)"
  [beat]
  (AbletonLink_setBeatNow -AL-pointer beat))

(s/fdef set-beat-at :args (s/cat :beat number? :timestamp number?))

(defn set-beat-at
  "Globally set the value of the beat (number) at a given timestamp"
  [beat timestamp]
  (AbletonLink_setBeatAt -AL-pointer beat timestamp))

(s/fdef set-beat-and-quantum-now :args (s/cat :beat number? :quantum number?))

(defn set-beat-and-quantum-now
  "Globally set the value of the beat (number) now"
  [beat quantum]
  (AbletonLink_setBeatAndQuantumNow -AL-pointer beat quantum))


(s/fdef set-beat-force :args (s/cat :beat number?))

(defn set-beat-force
  "Forcefully and globally set
   the value of the beat (number)"
  [beat]
  (AbletonLink_setBeatForce -AL-pointer beat))

(defn get-phase
  "Get the current phase of a bar"
  []
  (AbletonLink_getPhase -AL-pointer))

(defn get-bpm
  "Get the current global bpm"
  []
  (AbletonLink_getBpm -AL-pointer))

(s/fdef set-bpm :args (s/cat :bpm number?))

(defn set-bpm
  "Globally change the bpm on the link"
  [bpm]
  (AbletonLink_setBpm -AL-pointer bpm))

(defn get-num-peers
  "Get number of connected peers"
  []
  (AbletonLink_getNumPeers -AL-pointer))

(s/fdef set-quantum :args (s/cat :quantum number?))

(defn get-quantum
  "Get the quantum of a bar, returns number,
   (return number of beats in a bar)"
  []
  (AbletonLink_getQuantum -AL-pointer))

(defn set-quantum
  "Sets the quantum, in a way like setting a
   time-signature of a bar"
  [quantum]
  (AbletonLink_setQuantum -AL-pointer quantum))

(defn get-timestamp
  "Returns the timestamp in microseconds,
   a format needed by *At setters"
  []
  (AbletonLink_getTimestamp -AL-pointer))

(defn set-is-playing-now
  "If false, the transport will stop playing."
  [playing?]
  (AbletonLink_setIsPlayingNow -AL-pointer playing?))

(defn set-is-playing-at
  "If false, the transport will stop playing.
   Give a timestamp to commit this action in the future."
  [playing? timestamp]
  (AbletonLink_setIsPlayingAt -AL-pointer playing? timestamp))

(defn- append-event-to-queue [event-record time]
  (swap! event-queue-atom assoc event-record time))

(defrecord ScheduledEvent
    [fun recurring perioid stop-atom])

(defmethod print-method ScheduledEvent
  [obj ^Writer w]
  (.write w "#<Link ScheduledEvent>"))

(defn every [period fun & {:keys [initial-delay]
                           :or {initial-delay 0}}]
  (let [event (ScheduledEvent. fun true period (atom false))]
    (append-event-to-queue event 0)
    event))

(defn at [time fun & ignored]
  (let [event (ScheduledEvent. fun false 0 (atom false))]
    (append-event-to-queue event time)
    event))

(defn after [delay fun & ignored]
  (let [time  (+ delay (AbletonLink_getBeat -AL-pointer))
        event (ScheduledEvent. fun false 0 (atom false))]
    (append-event-to-queue event time)
    event))

(defn stop [& scheduled-events]
  (when-not (empty? scheduled-events)
    (run! #(reset! (:stop-atom %) true)
          scheduled-events)))

(defn stop-all []
  (reset! event-queue-atom (priority-map)))

(defn -main []
  (System/exit 0))
