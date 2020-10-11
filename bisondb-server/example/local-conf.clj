;; This configuration is stored on a per machine basis

{:local-root "/data1/bisondb"
 :download-rate-limit 1024
 :update-interval-s 60 ;; check for domain updates every minute
 :hdfs-conf {"fs.default.name" "s3n://hdfs"}}
