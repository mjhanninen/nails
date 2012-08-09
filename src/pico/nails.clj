(ns pico.nails
  (:require
   [pico.nails dispatch server]))

(def start-nailgun pico.nails.server/start-nailgun)

(def stop-nailgun pico.nails.server/stop-nailgun)

(def add-nail pico.nails.server/add-nail)

(def remove-nail pico.nails.server/remove-nail)
