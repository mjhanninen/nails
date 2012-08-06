(ns pico.nails.server
  (:require
   [pico.nails.context :as context])
  (:import
   [com.martiansoftware.nailgun NGConstants NGServer]
   [java.lang Thread]
   [java.net InetAddress]))

;;; TODO: Thread safety. Currently we have taken only minimal measures
;;; to address the thread safety issues.

(def server-instance (atom nil))

(defn new-server
  [& {:keys [addr port]
      :or {addr "localhost" port NGConstants/DEFAULT_PORT}}]
  {:pre [(string? addr) (integer? port)]}
  (let [server (NGServer. (InetAddress/getByName addr) port)
        thread (doto (Thread. server) (.start))]
    {:addr addr, :port port, :server server, :thread thread}))

(defn get-server
  "Returns the current `NGServer` instance or `nil` if none is
  present."
  []
  (:server @@server-instance))

(defn start
  "Start a NailGun server."
  [& args]
  (let [p (promise)]
    (if (compare-and-set! server-instance nil p)
      (let [{:keys [addr port]} (deref (deliver p (apply new-server args)))]
        (println (format "NailGun server started on %s:%s." addr port)))
      (let [{:keys [addr port]} @@server-instance]
        (println (format "Server already running on %s:%s." addr port))))))

(defn stop
  "Stop a NailGun server."
  ([]
     (stop false))
  ([exit-vm]
     (if-let [s @server-instance]
       (if (compare-and-set! server-instance s nil)
         (let [{:keys [server]} @s]
           (.shutdown server (boolean exit-vm))
           (println "Server stopped."))
         (println "Failed stop server. Try again."))
       (println "No server running."))))
