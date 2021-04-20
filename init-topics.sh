#!/bin/sh

container='kafka'

docker-compose exec $container kafka-topics \
  --create --zookeeper zookeeper:2181 \
  --replication-factor 1 --partitions 1 \
  --topic streaming.ecommerce.checkout.created --if-not-exists

docker-compose exec $container kafka-topics \
  --create --zookeeper zookeeper:2181 \
  --replication-factor 1 --partitions 1 \
  --topic streaming.ecommerce.payment.paid --if-not-exists

