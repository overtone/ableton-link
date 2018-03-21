(defproject overtone/ableton-link "1.0.0-alpha1"
  :description "Collaborative Programmable Music."
  :url "http://overtone.github.io/"
  :mailing-list {:name "overtone"
                 :archive "https://groups.google.com/group/overtone"
                 :post "overtone@googlegroups.com"}
  :license {:name "The MIT License (MIT)"
            :url "http://opensource.org/licenses/MIT"
            :distribution :repo
            :comments "Please use Overtone for good"}

  :dependencies [[org.clojure/clojure "1.9.0"]]
  
  :java-source-paths ["src/overtone/java"]

  :source-paths ["src"]

  :test-paths ["test"] 
  
  :native-path "native"
  
  :min-lein-version "2.0.0"
  )
