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


(ns overtone.link-jna-path
  "Sets the jna path depending on os,
   essentially the same as `overtone/jna-path`
   but overtone.ableton-link needs to be accessable to
   non-overtone users too.")


(defn- os-name
  "Returns a string representing the current operating system. Useful
   for debugging, etc. Prefer get-os for os-specific logic."
  []
  (System/getProperty "os.name"))

(defn get-os
  "Return the OS as a keyword. One of :windows :linux :mac"
  []
  (let [os (os-name)]
    (cond
      (re-find #"[Ww]indows" os) :windows
      (re-find #"[Ll]inux" os)   :linux
      (re-find #"[Mm]ac" os)     :mac)))

(defonce ^:private __SET_JNA_PATH__
  (case (get-os)
    :linux (System/setProperty "jna.library.path" "native/linux/x86_64")
    :mac (System/setProperty "jna.library.path" "native/macosx/x86_64")
    :windows (System/setProperty "jna.library.path" "native/windows/x86_64")))
