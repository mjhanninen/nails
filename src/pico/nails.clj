(ns pico.nails
  (:require
   [pico.nails.server :as server]))

(def start-nailgun server/start-nailgun)

(def stop-nailgun server/stop-nailgun)

(def add-nail server/add-nail)

(def remove-nail server/remove-nail)
