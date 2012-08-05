Add Some Nails to Your REPL
===========================

Clojure REPL is a powerful thing and so is the Unix command line. What if you
could mix these two together like this?

    user> (require [pico.nails :as nails])
    user> (nails/start)
    user> (nails/add-nail :hello (fn [c] (println "Hello," (first (:args c)))))

and then on your Bash prompt:

    $ ng hello World
    Hello, World

Well, that is exactly what *Nails* does at its simplest. *Nails* aims to provide
seamless integration between your project and the command line. So that you can
run stuff at which the shell excels like

    $ find . -name "*.mp3" -exec ng add-track {} \;

and then reap the benefits from the REPL. But it doesn't stop at the REPL. There
is more.

Status
------

As of writing this **the project is at very early stage**. No products has been
published. Everything is going to be unstable for some time. But please try it
out and give some feedback. I would appreciate it very much.

Dependencies
------------

*Nails* builds on the excellent Java tool *NailGun*. You need to install the
*NailGun* client.  You can obtain the sources from the *NailGun*'s [project
page](http://www.martiansoftware.com/nailgun/) and it takes about two minutes to
build and deploy it.

Usage
-----

(Once this project has been deployed for the first time) add the
following dependency to your `project.clj`:

    :dependencies [[org.clojars.pico/nails "0.1.0-SNAPSHOT"]]

License
-------

Copyright © 2012 Matti Hänninen

Distributed under the Eclipse Public License, the same as Clojure.
