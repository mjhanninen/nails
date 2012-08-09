(ns pico.nails
  (:require
   [pico.nails.server :as server]))

;;; TODO: Document the behaviour return values in normal and exceptional
;;; cases.

;;; TODO: It should be mentioned somewhere that registered nails persist
;;; after a server is shut down. Perhaps in ns docs.

(def
  ^{:arglists '([& {:keys [:addr :port]}])
    :static true
    :doc
  "Start a NailGun server.

  Optional kwargs `:addr` and `:port` can be used to specify the address
  and port of the server. The default address is \"localhost\" and the
  default port the value of NGConstans/DEFAULT_PORT (2113)."}
  start-nailgun server/start-nailgun)

(def
  ^{:arglists '([])
    :static true
    :doc
  "Stops the currently running NailGun server."}
  stop-nailgun server/stop-nailgun)

(def
  ^{:arglists '([id f & {:keys [:desc]}])
    :static true
    :doc
  "Adds a nail `f` to NailGun server.

  The argument `id` must be a keyword that serves dual purpose. On the
  one hand `id` determines the alias which is set to `(name id)`. On the
  other hand `id` is used to as a key that can be used for example when
  removing the nail from the server.

  The argument `f` must be function that takes one argument, i.e. of the
  form `(fn [c] ...)`. Here `c` is a map that provides the context of
  the NailGun call. For example `(:args c)` returns a vector holding the
  client's command line arguments.

  Optional kwarg `:desc` can be used to provide a short description. This
  description is displayed when user runs `ng ng-alias` on the command
  line."}
  add-nail server/add-nail)

(def
  ^{:arglists '([id])
    :static true
    :doc
  "Removes the nail from the NailGun server.

  The argument `id` must be a keyword that identifies the nail."}
  remove-nail server/remove-nail)
