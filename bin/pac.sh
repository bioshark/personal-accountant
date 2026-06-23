#!/bin/bash

DIR="$(cd "$(dirname "$0")/.." && pwd)"

export PATH="$HOME/.jenv/bin:$PATH"
eval "$(jenv init -)"

jenv shell 25

java -Djava.rmi.server.hostname=localhost \
     -jar "$DIR/personal-accountant-0.0.1-SNAPSHOT.jar"