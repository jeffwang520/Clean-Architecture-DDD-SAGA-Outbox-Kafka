#local-confluent-kafka-cp-zookeeper-headless
kafka-topics --zookeeper $1:2181 --topic payment-request --delete --if-exists
kafka-topics --zookeeper $1:2181 --topic payment-response --delete --if-exists
kafka-topics --zookeeper $1:2181 --topic restaurant-approved-request --delete --if-exists
kafka-topics --zookeeper $1:2181 --topic restaurant-approved-response --delete --if-exists
kafka-topics --zookeeper $1:2181 --topic customer --delete --if-exists

kafka-topics --zookeeper $1:2181 --topic payment-request --create --partitions 3 --replication-factor 3 --if-not-exists
kafka-topics --zookeeper $1:2181 --topic payment-response --create --partitions 3 --replication-factor 3 --if-not-exists
kafka-topics --zookeeper $1:2181 --topic restaurant-approved-request --create --partitions 3 --replication-factor 3 --if-not-exists
kafka-topics --zookeeper $1:2181 --topic restaurant-approved-response --create --partitions 3 --replication-factor 3 --if-not-exists
kafka-topics --zookeeper $1:2181 --topic customer --create --partitions 3 --replication-factor 3 --if-not-exists
