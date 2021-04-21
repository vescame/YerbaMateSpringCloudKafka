GRADLE=gradle
ARGS=clean build

JAVA=~/workspace/share/jdk-14.0.2/bin/java -jar

SRC_CHECKOUT_API=yerbamate.checkout
SRC_PAYMENT_API=yerbamate.payment
SRC_MAILER_API=yerbamate.mailer

CHECKOUT_API:=$(addsuffix /build/libs, $(SRC_CHECKOUT_API))
PAYMENT_API:=$(addsuffix /build/libs, $(SRC_PAYMENT_API))
MAILER_API:=$(addsuffix /build/libs, $(SRC_MAILER_API))

all: devops $(PAYMENT_API) $(CHECKOUT_API) $(MAILER_API)

$(PAYMENT_API): $(SRC_PAYMENT_API)
	$(GRADLE) $(ARGS) -p $<
	setsid $(JAVA) $@/*.jar > $</$(addsuffix .log, $<) &

$(CHECKOUT_API): $(SRC_CHECKOUT_API)
	$(GRADLE) $(ARGS) -p $<
	setsid $(JAVA) $@/*.jar > $</$(addsuffix .log, $<) &

$(MAILER_API): $(SRC_MAILER_API)
	$(GRADLE) $(ARGS) -p $<
	setsid $(JAVA) $@/*.jar > $</$(addsuffix .log, $<) &

devops:
	./checkout.sh start

test:
	./checkout.sh test
	./checkout.sh database

clean:
	pgrep -of '$(PAYMENT_API)' | xargs -r kill -15
	pgrep -of '$(CHECKOUT_API)' | xargs -r kill -15
	pgrep -of '$(MAILER_API)' | xargs -r kill -15
	$(GRADLE) clean -p $(SRC_PAYMENT_API)
	$(GRADLE) clean -p $(SRC_CHECKOUT_API)
	$(GRADLE) clean -p $(SRC_MAILER_API)
	./checkout.sh stop

