(ns pico.nails.ping
  (use pico.nails.context)
  (import
    [com.martiansoftware.nailgun NGContext])
  (:gen-class
    :methods [#^{:static true} [nailMain [com.martiansoftware.nailgun.NGContext] void]]))

(defn nail-main
  [context]
  (println "Command:")
  (println " " (get-command context))
  (println "Arguments:")
  (doseq [a (get-args context)]
    (println (format "  %s" a)))
  (println "Environment:")
  (doseq [[k v] (sort (get-env context))]
    (println (format "  %s = \"%s\"" k v)))
  (println "Inet Address:" (get-inet-address context))
  (println "Port:" (get-port context))
  (println "Local Client:" (is-local? context))
  (println "Loopback Client:" (is-loopback? context))
  (println "NailGun Server:" (get-server context)))

(defn -nailMain
  [^NGContext context]
  (binding [*in* (get-*in* context)
            *out* (get-*out* context)
            *err* (get-*err* context)]
    (let [ret-val (nail-main context)
          ret-code (cond
                     (integer? ret-val) ret-val
                     (or (nil? ret-val) ret-val) 0
                     :else 1)]
      (exit! context ret-code))))
