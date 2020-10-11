;; This configuration is stored globally on HDFS

{ :replication 2
  :hosts ["bison1.server" "bison2.server" "bison3.server"]
  :port 3578
  :domains {"graph" "s3n://mybucket/bisondb/graph"
            "docs"  "/data/docdb"
            }
}
