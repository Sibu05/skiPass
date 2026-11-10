#!/usr/bin/env sh
set -eu
cd "$(dirname "$0")"
mkdir -p build/classes
javac --release 17 -d build/classes src/cli-app/skipass/src/main/java/com/skipass/*.java
exec java -cp build/classes com.skipass.main "$@"
