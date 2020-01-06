#!/bin/bash

mvn validate -Dtycho.mode=maven -P modules-eclipse,update-target-versions -f ./judo-tatami-eclipse-targetdefinition/pom.xml

mvn validate -Dtycho.mode=maven -P modules-eclipse,update-category-versions -f ./judo-tatami-eclipse-site/pom.xml

