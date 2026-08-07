#!/usr/bin/env bash
#
# Reset ONLY the V2 migration on the REAL dev database
# (./data/personal-accountant), so an edited V2 re-applies on the next boot.
#
# Use this only if you are iterating V2 directly against your real dev DB instead of
# the recommended scratch profile (see scripts/scratch-reset.sh).
#
# PREREQUISITE: stop the app first (release the H2 file lock).
#
# CAVEAT: this drops the `category` table and removes the V2 history row. Once V2 also
# ALTERs columns on pending_payment_entity / recurring_payment_template, this script
# will NOT revert those column-type changes — extend the SQL below accordingly, or
# prefer the scratch profile which always starts from an empty database.
set -euo pipefail

cd "$(dirname "$0")/.."

H2JAR="$(find ~/.m2 -name 'h2-2.3.232.jar' | head -1)"
if [ -z "${H2JAR}" ]; then
  echo "H2 jar not found in ~/.m2. Run a Maven build first so the dependency is cached." >&2
  exit 1
fi

java -cp "${H2JAR}" org.h2.tools.Shell \
  -url "jdbc:h2:file:./data/personal-accountant" -user sa -password "" \
  -sql "DROP TABLE IF EXISTS category; DELETE FROM \"flyway_schema_history\" WHERE \"version\" = '2';"

echo "V2 reset on the real dev DB. Next boot will re-apply the current V2 fresh."
