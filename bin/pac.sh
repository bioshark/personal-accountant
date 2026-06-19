#!/bin/bash
DIR="$(cd "$(dirname "$0")/.." && pwd)"
cd "$DIR"
java -Djava.rmi.server.hostname=localhost -jar "$DIR/personal-accountant-0.0.1-SNAPSHOT.jar"
