#!/usr/bin/env bash
#
# Wipe the scratch database used for authoring Flyway migrations.
#
# After running this, boot with the 'scratch' profile and Flyway will re-apply every
# migration (V1..Vn) from an empty database. Use this whenever you have edited a
# not-yet-finalized migration and want a clean re-run.
#
#   ./scripts/scratch-reset.sh
#   ./mvnw spring-boot:run -Dspring-boot.run.profiles=scratch
#
# Safe: only touches ./data/scratch*, never your real dev database
# (./data/personal-accountant).
set -euo pipefail

cd "$(dirname "$0")/.."

removed=0
for f in ./data/scratch.mv.db ./data/scratch.trace.db; do
  if [ -f "$f" ]; then
    rm -f "$f"
    echo "removed $f"
    removed=1
  fi
done

if [ "$removed" -eq 0 ]; then
  echo "No scratch database found (already clean)."
fi

echo "Next boot with profile 'scratch' will re-apply all migrations from scratch."
