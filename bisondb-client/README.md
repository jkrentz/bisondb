# BisonDB Client

A Clojure client interface to BisonDB.

## Example Usage

Assuming a domain with string keys and values serialized to byte
arrays:

```clojure
(use 'bisondb.client)

;; serialize strings to byte arrays
(defn serialize-strings [coll]
  (map #(.getBytes %) coll))

;; deserialize results map back to strings
(defn deserialize-strings [m]
  (into {} (for [[k v] m]
             [(String. k) (String. v)])))

;; are all domain fully loaded?
(with-bison "127.0.0.1" 3578 connection
  (fully-loaded? connection))

;; => true

;; what domains are available?
(with-bison "127.0.0.1" 3578 connection
  (get-domains connection))

;; => ["rappers"]

(with-bison "127.0.0.1" 3578 connection
  (deserialize-strings (multi-get connection "rappers" (serialize-strings ["biggie" "tupac"])))

;; => {"biggie" "east-coast", "tupac" "west-coast"}
```
