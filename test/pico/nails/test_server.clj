(ns pico.nails.test-server
  (:use
   clojure.test)
  (:require
   [clojure.java.shell :as shell]
   [pico.nails.server :as server])
  (:import
   java.lang.Runtime))

(def
  ^{:dynamic true
    :doc
  "The path to NailGun binary. You should set this by setting the environment
  variable NAILGUN_BINARY_PATH when running the tests:

      $ export NAILGUN_BINARY_PATH=/path/to/ng
      $ lein test"}
  *nailgun-binary-path*
  (or (System/getenv "NAILGUN_BINARY_PATH") "ng"))

(def
  ^{:dynamic true
    :doc
  "The port the NailGun server is set up to listen during the tests. It
  is good to use non-standard port here in order not to interfere with
  possible live server."}
  *nailgun-test-port* 8989)

(defn ng-fixture
  [f]
  (server/start-nailgun :port *nailgun-test-port*)
  ;; Give Nailgun server some time to wake up
  (Thread/sleep 500)
  (f)
  (server/stop-nailgun))

(use-fixtures :once ng-fixture)

(deftest test-add-nail
  (server/add-nail :nail-1 (fn []) :desc "Test Nail 1")
  (is (server/has-nail :nail-1)))

(deftest test-args
  (let [a (atom nil)]
    (server/add-nail :temp
                     (fn [& args]
                       (reset! a args))
                     :desc "Temporary nail")
    (is (server/has-nail :temp))
    (shell/sh *nailgun-binary-path*
              "--nailgun-port" (str *nailgun-test-port*)
              "temp" "1" "2" "3")
    (server/remove-nail :temp)
    (is (= @a ["1" "2" "3"]))))
