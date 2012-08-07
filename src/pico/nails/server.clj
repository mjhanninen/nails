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
  (locking server-instance
    (:server @server-instance)))

(defn start-nailgun
  "Start a NailGun server."
  [& args]
  (locking server-instance
    (if-let [{:keys [addr port]} @server-instance]
      (println (format "Server already running on %s:%s." addr port))
      (let [{:keys [addr port]} (reset! server-instance (apply new-server args))]
        (println (format "NailGun server started on %s:%s." addr port))))))

(defn stop-nailgun
  "Stop a NailGun server."
  ([]
     (stop-nailgun false))
  ([exit-vm]
     (locking server-instance
       (if-let [{:keys [server]} @server-instance]
         (do
           (.shutdown server (boolean exit-vm))
           (reset! server-instance nil)
           (println "Server stopped."))
         (println "No server running.")))))
