(ns overtone.ableton-link-tests
  (:use clojure.test)
  (:require [overtone.ableton-link :as link]))

(deftest class-name
  (is (= "class AbletonLink"
         (str (type link/ableton-link)))))

(deftest initially-disabled
  (is (= false (link/link-enabled?))))

(deftest enabled
  (link/enable-link true)
  (is (= true (link/link-enabled?))))

(deftest initial-beat-0
  (is (zero? (.getBeat link/ableton-link))))

(deftest get-beat-updates-the-clock
  (is (not (zero? (link/get-beat)))))

(deftest initial-120bpm 
  (is (== 120 (link/get-bpm))))

(deftest setting-bpm
  (link/set-bpm 90.5)
  (is (== 90.5 (link/get-bpm))))

(deftest initial-quantum-4
  (is (== 4 (link/get-quantum))))

(deftest setting-quantum
  (link/set-quantum 2)
  (is (== 2 (link/get-quantum))))

(use-fixtures :once
  (fn [f]
    (class-name)
    (initially-disabled)
    (enabled)
    (initial-beat-0)
    (get-beat-updates-the-clock)
    (initial-120bpm)
    (setting-bpm)
    (initial-quantum-4)
    (setting-quantum)))
