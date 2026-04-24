#!/usr/bin/env bash
set -euo pipefail

./mvnw spring-boot:run \
  -Dspring-boot.run.arguments="--spring.main.web-application-type=none --spring.cache.type=none --recitations.import.enabled=true"
