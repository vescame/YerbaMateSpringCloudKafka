#!/bin/sh

container='kafka'

docker-compose exec $container kafka-topics \
  --create --zookeeper zookeeper:2181 \
  --replication-factor 1 --partitions 1 \
  --topic streaming.yerbamate.checkout.created --if-not-exists

docker-compose exec $container kafka-topics \
  --create --zookeeper zookeeper:2181 \
  --replication-factor 1 --partitions 1 \
  --topic streaming.yerbamate.payment.verified --if-not-exists

docker-compose exec $container kafka-topics \
  --create --zookeeper zookeeper:2181 \
  --replication-factor 1 --partitions 1 \
  --topic streaming.yerbamate.checkout.updated --if-not-exists
