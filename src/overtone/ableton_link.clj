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
  (:require overtone.link-jna-path
            [clj-native.direct :refer [defclib loadlib]]
            [clj-native.structs :refer [byref]]
            [clj-native.callbacks :refer [callback]]
            [clojure.data.priority-map :refer [priority-map]]
            [clojure.spec.alpha :as s])
  (:import  [java.io Writer]))

(defclib lib-abletonlink
  (:libname "abletonlink")
  (:functions
   (-AbletonLink_ctor AbletonLink_ctor [] void*)
   (-enable-link AbletonLink_enable [void* bool] void)
   (-is-enabled AbletonLink_isEnabled [void* ] bool)
   (-get-beat AbletonLink_getBeat [void*] double)
   (-set-beat-now AbletonLink_setBeatNow [void* double] void)
   (-set-beat-at AbletonLink_setBeatAt [void* double double] void)
   (-set-beat-and-quantum-now AbletonLink_setBeatAndQuantumNow [void* double double] void)
   (-set-beat-force AbletonLink_setBeatForce [void* double] void)
   (-get-phase AbletonLink_getPhase [void*] double)
   (-get-bpm AbletonLink_getBpm [void*] double)
   (-set-bpm AbletonLink_setBpm [void* double] void)
   (-get-num-peers AbletonLink_getNumPeers [void*] int)
   (-get-quantum AbletonLink_getQuantum [void*] double)
   (-set-quantum AbletonLink_setQuantum [void* double] void)
   (-get-timestamp AbletonLink_getTimestamp [void*] long)
   (-set-is-playing-now AbletonLink_setIsPlayingNow [void* bool] void)
   (-set-is-playing-at AbletonLink_setIsPlayingAt [void* bool double] void)
   (-update AbletonLink_update [void*] void)))

(loadlib lib-abletonlink)

(defonce -AL-pointer (-AbletonLink_ctor))

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
      (-update -AL-pointer)
      (let [cur-time (-get-beat -AL-pointer)]
        (while (and (not (empty? @event-queue-atom))
                    (<= (second (peek @event-queue-atom))
                        (-get-beat -AL-pointer)))
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
                                 (-enable-link -AL-pointer true)
                                 (fn [] (reset! run? false))))  ;; State becomes function that will stop clock thread.
                             ;; The caller wants us to be stopped.
                             (when state  ;; We only need to do anything if we were running.
                               (state)  ;; Call the function that stops the clock thread.
                               (-enable-link -AL-pointer false)
                               nil))))))  ;; State becomes nil, indicating we are no longer running.

(defn link-enabled?
  "Returns true if link is enabled"
  []
  (-is-enabled -AL-pointer))

(defn get-beat
  "Sync(update) with link and
   return the current bpm"
  []
  (-get-beat -AL-pointer))

(s/fdef set-beat-now :args (s/cat :beat number?))

(defn set-beat-now
  "Globally set the value of the beat (number)"
  [beat]
  (-set-beat-now -AL-pointer beat))

(s/fdef set-beat-at :args (s/cat :beat number? :timestamp number?))

(defn set-beat-at
  "Globally set the value of the beat (number) at a given timestamp"
  [beat timestamp]
  (-set-beat-at -AL-pointer beat timestamp))

(s/fdef set-beat-now :args (s/cat :beat number?))

(defn set-beat-and-quantum-now
  "Globally set the value of the beat (number) now"
  [beat timestamp]
  (-set-beat-and-quantum-now -AL-pointer beat timestamp))


(s/fdef set-beat-force :args (s/cat :beat number?))

(defn set-beat-force
  "Forcefully and globally set
   the value of the beat (number)"
  [beat]
  (-set-beat-force -AL-pointer beat))

(defn get-phase
  "Get the current phase of a bar"
  []
  (-get-phase -AL-pointer))

(defn get-bpm
  "Get the current global bpm"
  []
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
  (-get-quantum -AL-pointer))

(defn get-timestamp
  "Returns the timestamp in microseconds,
   a format needed by *At setters"
  []
  (-get-timestamp -AL-pointer))

(defn set-is-playing-now
  "If false, the transport will stop playing."
  [isPlaying?]
  (-set-is-playing-now -AL-pointer isPlaying?))

(defn set-is-playing-at
  "If false, the transport will stop playing.
   Give a timestamp to commit this action in the future."
  [isPlaying? timestamp]
  (-set-is-playing-now -AL-pointer isPlaying? timestamp))

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
  (let [time  (+ delay (-get-beat -AL-pointer))
        event (ScheduledEvent. fun false 0 (atom false))]
    (append-event-to-queue event time)
    event))

(defn stop [& scheduled-events]
  (when-not (empty? scheduled-events)
    (run! #(reset! (:stop-atom %) true)
          scheduled-events)))

(defn stop-all []
  (reset! event-queue-atom (priority-map)))


(comment
  (enable-link true)
  (get-beat)
  (enable-link false)
  (stop-all)
  (def fn1 #(println "f"))
  @event-queue-atom
  (def a (every 1 #'fn1))
  (stop a))
