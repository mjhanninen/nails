(ns pico.nails.context
  (:import
   [java.io InputStreamReader OutputStreamWriter]
   [java.lang SecurityException]
   [com.martiansoftware.nailgun NGContext NGServer]))

(defn ^InputStreamReader get-*in*
  "Returns the client's stdin."
  [^NGContext c]
  (InputStreamReader. (.in c)))

(defn ^OutputStreamWriter get-*out*
  "Returns the client's stdout."
  [^NGContext c]
  (OutputStreamWriter. (.out c)))

(defn ^OutputStreamWriter get-*err*
  "Returns the client's stderr."
  [^NGContext c]
  (OutputStreamWriter. (.err c)))

(defn get-command
  "Returns the command that was issued by the client (either an alias or the
  name of a class). This allows multiple aliases to point to the same class but
  result in different behaviors."
  [^NGContext c]
  (.getCommand c))

(defn get-args
  "Returns the command line arguments."
  [^NGContext c]
  (vec (.getArgs c)))

(defn get-working-directory
  "Returns the current working directory of the client, as reported by the
  client."
  [^NGContext c]
  (.getWorkingDirectory c))

(defn get-file-separator
  "Returns the file separator used by the client's OS."
  [^NGContext c]
  (.getFileSeparator c))

(defn get-env
  "Returns a map of the client's environment variables."
  [^NGContext c]
  (into {} (.getEnv c)))

(defn get-inet-address
  "Returns the address of the client at the other side of this connection."
  [^NGContext c]
  (.toString (.getInetAddress c)))

(defn get-port
  "Returns the port on the client side of this connection."
  [^NGContext c]
  (long (.getPort c)))

(defn ^NGServer get-server
  [^NGContext c]
  (.getNGServer c))

(defn is-local?
  "Check that the client is connected from the local machine."
  [^NGContext c]
  (try
    (.assertLocalClient c)
    true
    (catch SecurityException e
      false)))

(defn is-loopback?
  "Check that the client is connected via the loopback address."
  [^NGContext c]
  (try
    (.assertLoopbackClient c)
    true
    (catch SecurityException e
      false)))

(defn exit!
  "Sends an exit command with the specified exit code to the client. The client
  will exit immediately with the specified exit code; you probably want to
  return from nailMain immediately after calling this."
  ([^NGContext c]
     (exit! c 0))
  ([^NGContext c code]
     (.exit c code)))

(defn to-map
  [^NGContext c]
  (into {} (for [[k v] {:*in* get-*in*,
                        :*out* get-*out*,
                        :*err* get-*err*,
                        :cmd get-command,
                        :args get-args,
                        :env get-env,
                        :pwd get-working-directory,
                        :sep get-file-separator,
                        :addr get-inet-address,
                        :port get-port,
                        :server get-server}]
             [k (v c)])))
