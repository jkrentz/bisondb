#!/usr/local/bin/thrift --gen py:utf8strings --gen java:beans,nocamel,hashcode

namespace java bisondb.generated
namespace py bisondb

struct Value {
  1: optional binary data;
}

// Status Structs

struct LoadingStatus {  
}

struct ReadyStatus {
  1: optional LoadingStatus update_status;
}

struct FailedStatus {
  1: string error_message;
}

struct ShutdownStatus {
}

union DomainStatus {
  1: ReadyStatus ready;
  2: LoadingStatus loading;
  3: FailedStatus failed;
  4: ShutdownStatus shutdown;
}

struct Status {
  1: required map<string, DomainStatus> domain_statuses;
}

// Domain MetaData Structs

struct DomainSpec {
  1: required i32 num_shards;
  2: required string coordinator;
  3: required string shard_scheme;
}

struct DomainMetaData {
  1: required i64 remote_version;
  2: required i64 local_version;
  3: required set<i64> shard_set;
  4: required DomainSpec domain_spec;
}

struct MetaData {
  1: required map<string, DomainMetaData> domain_metadatas;
}

// Exceptions

exception DomainNotFoundException {
  1: required string domain;
}

exception DomainNotLoadedException {
  1: required string domain;
}

exception HostsDownException {
  1: required list<string> hosts;
}

// can happen if domain location changes or if num shards changes
exception InvalidConfigurationException {
  1: required list<string> mismatched_domains; 
  2: required bool port_changed;
  3: required bool hosts_changed;
}

exception WrongHostException {
}

service BisonDBShared {
  DomainStatus getDomainStatus(1: string domain);
  list<string> getDomains();
  Status getStatus();
  bool isFullyLoaded();
  bool isUpdating();
  bool update(1: string domain); // is the supplied domain updating?
  bool updateAll() throws (1: InvalidConfigurationException ice);
  i64 getCount(1: string domain);
  DomainMetaData getDomainMetaData(1: string domain);
  MetaData getMetaData();
}
