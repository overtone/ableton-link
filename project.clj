(defproject overtone/ableton-link "1.0.0-alpha2"
  :description "Collaborative Programmable Music."
  :url "http://overtone.github.io/"
  :mailing-list {:name "overtone"
                 :archive "https://groups.google.com/group/overtone"
                 :post "overtone@googlegroups.com"}
  :license {:name "The MIT License (MIT)"
            :url "http://opensource.org/licenses/MIT"
            :distribution :repo
            :comments "Same licence as the Overtone project."}

  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/spec.alpha "0.1.143"]
                 [net.java.dev.jna/jna "4.4.0"]
                 [clj-native "0.9.5"]
                 [org.clojure/data.priority-map "0.0.9"]]

  :source-paths ["src"]

  :resource-paths ["resources"]
  
  :test-paths ["test"] 
  
  :native-path "native"
  
  :min-lein-version "2.0.0"
  )
