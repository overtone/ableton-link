(ns overtone.link-jna-path
  "Sets the jna path depending on os,
   essentially the same as `overtone/jna-path`
   but overtone.ableton-live needs to be accessable to
   non-overtone users too.")


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

(defonce ^:private __SET_JNA_PATH__
  (case (get-os)
    :linux (System/setProperty "jna.library.path" "native/linux/x86_64")
    :mac (System/setProperty "jna.library.path" "native/macosx/x86_64")
    :windows (System/setProperty "jna.library.path" "native/windows/x86_64")))
