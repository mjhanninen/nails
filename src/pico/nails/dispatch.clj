(ns pico.nails.dispatch
  (require
   [pico.nails.context :as context]
   [pico.nails.server :as server])
  (import
   [com.martiansoftware.nailgun Alias NGContext])
  (:gen-class
   :methods [#^{:static true} [nailMain [com.martiansoftware.nailgun.NGContext] void]]))

(def dispatch-table (atom {}))

(defn add-nail
  [k f & {:keys [desc] :or {:desc ""}}]
  {:pre [(keyword? k) (fn? f)]}
  (if-let [s (server/get-server)]
    (doto (.getAliasManager s)
      (.addAlias (Alias. (name k) desc pico.nails.dispatch))))
  (swap! dispatch-table #(assoc % k f)))

 (defn remove-nail
   [k]
   {:pre [(keyword? k)]}
   (if-let [s (server/get-server)]
    (doto (.getAliasManager s)
      (.removeAlias (name k))))
   (swap! dispatch-table #(dissoc % k)))

 (defn error-nail
   [msg]
   (fn [_]
     (binding [*err* *out*]
       (println (format "Error: %s" msg)))
     1))

 (defn dispatch
   [cmd ctx]
   ((or (@dispatch-table (keyword cmd))
        (error-nail (format "nail %s not found" cmd)))
    ctx))

 (defn nail-fn
   [c]
   (let [{:keys [cmd args]} c]
     (if (= cmd "pico.nails.dispatch")
       (dispatch (first args) (assoc c :args (vec (rest args))))
       (dispatch cmd c))))

 (defn make-nail-main
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
