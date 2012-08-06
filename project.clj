(defproject org.clojars.pico/nails "0.1.1-SNAPSHOT"
  :description "Adds some nails to your REPL"
  :url "http://github.com/pico/nails"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [com.martiansoftware/nailgun "0.7.1"]]
  :repositories {"ooo-maven-repo" "http://ooo-maven.googlecode.com/hg/repository"}
  :aot [pico.nails.dispatch])
