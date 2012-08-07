Add Some Nails to Your REPL
===========================

Clojure REPL is a powerful thing and so is the Unix command line. What if you
could mix these two together like this?

    user> (use 'pico.nails)
    user> (start-nailgun)
    user> (add-nail :hello (fn [c] (println "Hello," (first (:args c)))))

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


Getting started
---------------


### Obtaining NailGun client ###

*Nails* builds on the excellent Java tool *NailGun* for which you need to
install the client binary. You can obtain the sources from *NailGun*'s [project
page][ng] and it takes only about two minutes from there to build and deploy
the client. However [Alex Osborne][ato] has made the sources available through
his fork on [GitHub][atong] and so probably the most straightforward way to
build and install the client is:

    $ git clone git://github.com/ato/nailgun.git
    $ cd nailgun
    $ make
    $ ln -s $(pwd)/ng /path/to/bin/ng

[ng]: http://www.martiansoftware.com/nailgun/
    "NailGun: Insanely Fast Java"

[ato]: https://github.com/ato
    "Alex Osborne on GitHub"

[atong]: https://github.com/ato/nailgun
    "Alex's fork of NailGun on GitHub"


### Obtaining Nails ###

If you are using *Nails* with a Leiningen project, you just need to add the
following dependency to your `project.clj`:

    :dependencies [[org.clojars.pico/nails "0.1.0"]]

Similarly with a Maven project you just need to add the following repository
and dependency to your `pom.xlm`:

    <repository>
      <id>clojars.org</id>
      <url>http://clojars.org/repo</url>
    </repository>
    ...
    <dependency>
      <groupId>org.clojars.pico</groupId>
      <artifactId>nails</artifactId>
      <version>0.1.0</version>
    </dependency>

In both cases the build tool's dependency manager should take care of
downloading direct and indirect dependencies.

If neither of the above fits your bill, you can always download
[nails-0.1.0.jar][njar] and [nailgun-0.7.1.jar][ngjar] by hand and ensure that
they are on your `CLASSPATH`.

[njar]: http://clojars.org/repo/org/clojars/pico/nails/0.1.0/nails-0.1.0.jar
    "clojars.org repository - nails-0.1.0.jar"

[ngjar]: http://ooo-maven.googlecode.com/hg/repository/com/martiansoftware/nailgun/0.7.1/nailgun-0.7.1.jar
    "ooo-maven repository - nailgun-0.7.1.jar"


### Using Nails ###

The following simplified API is exposed when you `(use 'pico.nails)`:

- `(start-nailgun [:addr <address> :port <port>])`
- `(stop-nailgun)`
- `(add-nail <id> <fn>)`
- `(remove-nail <id>)`

where `<id>` is a keyword that names the nail and `<fn>` is a function of one
argument through the context of the call is provided. The address and port are
optional when starting a server. See the (currently non-existent) documentation
for details.


-------------------------------------------------------------------------------


License
-------

Copyright © 2012 Matti Hänninen

Distributed under the Eclipse Public License, the same as Clojure.
