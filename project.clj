(defproject overtone/ableton-link "1.0.0-beta9"
  :description "Collaborative Programmable Music."
  :url "http://overtone.github.io/"
  :mailing-list {:name    "overtone"
                 :archive "https://groups.google.com/group/overtone"
                 :post    "overtone@googlegroups.com"}
  :license {:name         "The MIT License (MIT)"
            :url          "http://opensource.org/licenses/MIT"
            :distribution :repo
            :comments     "Same licence as the Overtone project."}

  :dependencies [[commons-io/commons-io "2.6"]
                 [org.clojure/clojure "1.10.0"]
                 [org.clojure/spec.alpha "0.1.143"]
                 [org.clojure/data.priority-map "0.0.9"]
                 [techascent/tech.jna "3.18"]]

  :source-paths ["src"]

  :resource-paths ["native"]

  :test-paths ["test"]

  :native-path "native"

  :min-lein-version "2.0.0"
  )
