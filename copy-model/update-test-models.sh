#!/bin/bash

MODEL=northwind-demo-esm.model
CURR_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
BASE_DIR="${CURR_DIR}/../"

yes | cp -rf ${CURR_DIR}/${MODEL} ${BASE_DIR}/judo-tatami-esm2psm/src/test/resources/northwind-demo-esm.model
mvn clean install -f $BASE_DIR/judo-tatami-esm2psm/pom.xml

yes | cp -rf ${BASE_DIR}/judo-tatami-esm2psm/target/test-classes/northwind.psm ${BASE_DIR}/judo-tatami-psm2asm/src/test/resources/northwind-judopsm.model
mvn clean install -f $BASE_DIR/judo-tatami-psm2asm/pom.xml

yes | cp -rf ${BASE_DIR}/judo-tatami-esm2psm/target/test-classes/northwind.psm ${BASE_DIR}/judo-tatami-psm2measure/src/test/resources/northwind-judopsm.model
mvn clean install -f $BASE_DIR/judo-tatami-psm2measure/pom.xml

yes | cp -rf ${BASE_DIR}/judo-tatami-psm2asm/target/test-classes/northwind.asm ${BASE_DIR}/judo-tatami-asm2jaxrsapi/src/test/resources/northwind-asm.model
mvn clean install -f $BASE_DIR/judo-tatami-asm2jaxrsapi/pom.xml

yes | cp -rf ${BASE_DIR}/judo-tatami-psm2asm/target/test-classes/northwind.asm ${BASE_DIR}/judo-tatami-asm2openapi/src/test/resources/northwind-asm.model
mvn clean install -f $BASE_DIR/judo-tatami-asm2openapi/pom.xml

yes | cp -rf ${BASE_DIR}/judo-tatami-psm2asm/target/test-classes/northwind.asm ${BASE_DIR}/judo-tatami-asm2rdbms/src/test/resources/northwind-asm.model
mvn clean install -f $BASE_DIR/judo-tatami-asm2rdbms/pom.xml

yes | cp -rf ${BASE_DIR}/judo-tatami-psm2asm/target/test-classes/northwind.asm ${BASE_DIR}/judo-tatami-asm2sdk/src/test/resources/northwind-asm.model
mvn clean install -f $BASE_DIR/judo-tatami-asm2sdk/pom.xml

yes | cp -rf ${BASE_DIR}/judo-tatami-asm2rdbms/target/test-classes/northwind-rdbms.model ${BASE_DIR}/judo-tatami-rdbms2liquibase/src/test/resources/northwind-rdbms.model
mvn clean install -f $BASE_DIR/judo-tatami-rdbms2liquibase/pom.xml

rm -rf ${BASE_DIR}/judo-tatami-itest/src/test/java/sdk
yes | cp -rf ${BASE_DIR}/judo-tatami-asm2sdk/target/test-classes/generated/java/ ${BASE_DIR}/judo-tatami-itest/src/test/java/
