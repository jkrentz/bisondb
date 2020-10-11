[![Build Status](https://travis-ci.org/jkrentz/bisondb.png?branch=develop)](https://travis-ci.org/jkrentz/bisondb)

# BisonDB 0.6.0

BisonDB is a database that specializes in exporting key/value data
from Hadoop. BisonDB is composed of two components. The first is a
library that is used in MapReduce jobs for creating an indexed
key/value dataset that is stored on a distributed filesystem. The
second component is a daemon that can download a subset of a dataset
and serve it in a read-only, random-access fashion. A group of
machines working together to serve a full dataset is called a ring.

Since BisonDB server doesn't support random writes, there's almost 
no moving parts. Once the server loads up its subset of the data, 
it does very little. This leads to BisonDB being rock-solid in
production.

BisonDB server has a Thrift interface, so any language can make
reads from it. The database itself is implemented in Clojure.

A BisonDB datastore contains a fixed number of shards of a "Local
Persistence". BisonDB's local persistence engine is pluggable, and
BisonDB comes bundled with local persistence implementations for
Berkeley DB Java Edition and LevelDB. On the MapReduce side, each
reducer creates or updates a single shard into the DFS, and on the
server side, each server serves a subset of the shards.

BisonDB supports hot-swapping so that a live server can be updated
with a new set of shards without downtime.

# Using BisonDB in MapReduce Jobs

TODO: Documentation on using BisonDB in mapreduce.

# Deploying BisonDB server

TODO: Documentation on how to deploy BisonDB.

# Running the BDB Jar

TODO: Documentation on how to run BisonDB
