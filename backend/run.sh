#!/usr/bin/env bash
# Runs the API locally using the variables defined in ../.env (values are never printed).
set -euo pipefail
cd "$(dirname "$0")"
set -a
source ../.env
set +a
exec ./mvnw spring-boot:run
