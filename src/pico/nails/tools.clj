(ns pico.nails.tools)

(defn error-nail
  "Creates a nail that prints `msg` to `*err*` and returns with the
  error code specified by the optional keyword argument `:rc`. If no
  `:rc` is given then default error code 1 is assumed."
  [msg & {:keys [rc] :or {rc 1}}]
  (fn [_]
    (binding [*err* *out*]
      (println (format "Error: %s" msg)))
    rc))
