To-Do
=====

- **API documentation**

- **Client liveliness:** How to determine if the client is still alive? This is
 important for long-lived nails?

- **Defnail macro:** So that the definition `(defnail foo [bar baz] ...)`
  enables you to `(foo 42 3.14)` on the REPL and `ng foo 42 3.14` on the
  command line.

- **Security:** The *NailGun* is inherently insecure but do what you can. Nails
  should allow only local connections by default etc.

- **Nail class generation:** Geneting a class that can be registered with a
  *NailGun* server is a bit awkward at the moment. (*low priority*)

- **Thread safety** (*low priority*)

- **Multiple NailGun instances** (*low priority*)
