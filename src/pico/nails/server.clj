(ns pico.nails.server
  (:require
   [clojure.set :as set]
   [pico.nails.context :as context]
   [pico.nails.tools :as tools])
  (:import
   [com.martiansoftware.nailgun Alias NGConstants NGContext NGServer]
   [java.lang Thread]
   [java.net InetAddress])
  (:gen-class
   :methods [#^{:static true} [nailMain [com.martiansoftware.nailgun.NGContext] void]]))

;;;; Dispatch table

(def
  ^{:doc "An atom containing mappings from nail ids to nail functions."
    :private true
    :static true}
  dispatch-table (atom {}))

(defn add-nail
  [k f & {:keys [desc] :or {:desc ""}}]
  {:pre [(keyword? k) (fn? f)]}
  ;; TODO: Is `locking` necessary or does `swap!` do it implicitly
  (locking dispatch-table
    (swap! dispatch-table #(assoc % k {:fn f :desc desc}))))

(defn has-nail
  "Test does the dispatch table contain a Nail for `k`."
  [k]
  (boolean (@dispatch-table k)))

(defn remove-nail
  [k]
  {:pre [(keyword? k)]}
  ;; TODO: Is `locking` necessary or does `swap!` do it implicitly
  (locking dispatch-table
    (swap! dispatch-table #(dissoc % k))))

;;;; Registering and unregistering

(defn- add-alias
  "Adds an alias with name `(name k)` and description `d` to the
  `NGServer` instance `s` and wire it call our dispatch method."
  [^NGServer s k d]
  (doto (.getAliasManager s)
    (.addAlias (Alias. (name k) d pico.nails.server))))

(defn- remove-alias
  "Remove the alias with name `(name k)` from the `NGServer` instance `s`."
  [^NGServer s k]
  (doto (.getAliasManager s)
    (.removeAlias (name k))))

;;;; Starting and stopping server

(def
  ^{:doc "An atom containing either `nil` when the server is not
         running or a map containing the server instance with some
         other related information."
    :private true
    :static true}
  server-instance (atom nil))

(defn- new-server
  "Creates a new `NGServer` instance and starts it in a separate
  thread. Returns a map with the following entries: `:addr`, `:port`,
  `:server`, and `:thread`."
  [& {:keys [addr port]
      :or {addr "localhost" port NGConstants/DEFAULT_PORT}}]
  {:pre [(string? addr) (integer? port)]}
  (let [server (NGServer. (InetAddress/getByName addr) port)
        thread (doto (Thread. server) (.start))]
    {:addr addr, :port port, :server server, :thread thread}))

(defn dispatch-table-did-change
  [server _ old-tbl new-tbl]
  (let [old-keys (set (keys old-tbl))
        new-keys (set (keys new-tbl))]
    (doseq [id (set/difference old-keys new-keys)]
      (remove-alias server id))
    (doseq [id (set/difference new-keys old-keys)]
      (add-alias server id (get-in new-tbl [id :desc])))))

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
      (let [{:keys [addr port server]} (reset! server-instance
                                               (apply new-server args))]
        (locking dispatch-table
          (dispatch-table-did-change server dispatch-table {} @dispatch-table)
          (add-watch dispatch-table server dispatch-table-did-change))
        (println (format "NailGun server started on %s:%s." addr port))))))

(defn stop-nailgun
  "Stop the currently running NailGun server."
  ([]
     (stop-nailgun false))
  ([exit-vm?]
     (locking server-instance
       (if-let [{:keys [server]} @server-instance]
         (do
           (locking dispatch-table
             (dispatch-table-did-change server dispatch-table @dispatch-table {})
             (remove-watch dispatch-table server))
           (.shutdown server (boolean exit-vm?))
           (reset! server-instance nil)
           (println "Server stopped."))
         (println "No server running.")))))

(def
  ^{:dynamic true
    :doc "Bound to the NailGun server serving the nail."}
  *nailgun-server*)

(def
  ^{:dynamic true
    :doc "The client's address."}
  *nailgun-client-address*)

(def
  ^{:dynamic true
    :doc "The client's side port."}
  *nailgun-client-port*)

(def
  ^{:dynamic true
    :doc "The NailGun command issued on the client's side."}
  *nailgun-command*)

(def
  ^{:dynamic true
    :doc "A vector of the client's command line arguments."}
  *nailgun-args*)

(def
  ^{:dynamic true
    :doc "The client's present working directory."}
  *nailgun-directory*)

(def
  ^{:dynamic true
    :doc "The path separator on the client's system."}
  *nailgun-separator*)

(def
  ^{:dynamic true
    :doc "The client's environment variables in a map from variable names to
  values. Both are strings."}
  *nailgun-env*)

(defn bind-context
  "Binds `context` to dynamic vars and calls `f` in this dynamic scope."
  [f context]
  (binding [*nailgun-server* (:server context)
            *nailgun-client-address* (:addr context)
            *nailgun-client-port* (:port context)
            *nailgun-command* (:cmd context)
            *nailgun-args* (:args context)
            *nailgun-env* (:env context)
            *nailgun-directory* (:pwd context)
            *nailgun-separator* (:sep context)]
    (apply f *nailgun-args*)))

(defn- dispatch
  "Looks up the nail function from the dispatch table and calls it with the
  whole context bound to dynamic vars."
  [cmd context]
  (bind-context (get-in @dispatch-table
                        [(keyword cmd) :fn]
                        (tools/error-nail (format "nail %s not found" cmd)))
                context))

(defn- nail-fn
  [c]
  (let [{:keys [cmd args]} c]
    (if (= cmd "pico.nails.server")
      (dispatch (first args) (assoc c :args (vec (rest args))))
      (dispatch cmd c))))

(defn- make-nail-main
  [f]
  (fn [c]
    (binding [*in* (context/get-*in* c)
              *out* (context/get-*out* c)
              *err* (context/get-*err* c)]
      (let [ret-val (f (context/to-map c))
            ret-code (cond
                       (integer? ret-val) ret-val
                       (or (nil? ret-val) ret-val) 0
                       :else 1)]
        (context/exit! c ret-code)))))

(defn -nailMain
  [^NGContext context]
  ((make-nail-main nail-fn) context))
