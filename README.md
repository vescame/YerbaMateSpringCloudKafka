# Yerba Mate Kafka Microservices

## Manually create
Create environment:
```shell
docker-compose up -d
```

Applications properties are setup with topic auto-creation, but, if needed you should run
```shell
sh ./init-topics.sh
```

Start applications with your IDE or manually with gradle and java -jar. Then, test the yerbamate.checkout API:
```shell
sh ./checkout.sh test
```

Display database data:
```shell
sh ./checkout.sh database
```

## Using the _Makefile_

Startup everything (DevOps and Microservices)
```shell
make all
```

Test and show database
```shell
make test
```

Cleanup (DevOps and Microservices)
```shell
make clean
```
