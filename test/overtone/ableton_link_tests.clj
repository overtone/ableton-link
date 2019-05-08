(ns overtone.ableton-link-tests
  (:use clojure.test)
  (:require [overtone.ableton-link :as link]))


(deftest initially-disabled
  (is (= false (link/link-enabled?))))

(deftest initial-beat-0
  (is (zero? (link/-get-beat link/-AL-pointer))))

(deftest initial-120bpm
  (is (== 120 (link/get-bpm))))

(deftest initial-quantum-4
  (is (== 4 (link/get-quantum))))

(deftest enabled
  (link/enable-link true)
  (is (= true (link/link-enabled?))))

(deftest enable-starts-the-clock
  (do (Thread/sleep 10)
      (is (not (zero? (link/get-beat))))))

(deftest setting-bpm
  (link/set-bpm 90.5)
  (is (== 90.5 (link/get-bpm))))

(deftest setting-quantum
  (link/set-quantum 2)
  (is (== 2 (link/get-quantum))))

(use-fixtures :once
  (fn [f]
    ;; (class-name)
    (initially-disabled)
    (initial-beat-0)
    (initial-120bpm)
    (initial-quantum-4)
    (Thread/sleep 10)
    (enabled)
    (enable-starts-the-clock)
    (setting-bpm)
    (setting-quantum)))

;; (run-tests)
