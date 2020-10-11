#!/usr/local/bin/thrift --gen py:utf8strings --gen java:beans,nocamel,hashcode

namespace java bisondb.generated.keyval

include "core.thrift"

service BisonDB extends core.BisonDBShared {
  core.Value get(1: string domain, 2: binary key) throws (
  1: core.DomainNotFoundException dnfe,
  2: core.HostsDownException hde,
  3: core.DomainNotLoadedException dnle);

  map<binary, core.Value> multiGet(1: string domain, 2: set<binary> key) throws (
  1: core.DomainNotFoundException dnfe,
  2: core.HostsDownException hde,
  3: core.DomainNotLoadedException dnle);

  map<binary, core.Value> directMultiGet(1: string domain, 2: set<binary> key) throws (
  1: core.DomainNotFoundException dnfe,
  2: core.HostsDownException hde,
  3: core.DomainNotLoadedException dnle);
}
