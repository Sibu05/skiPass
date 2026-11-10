#!/usr/bin/env sh
set -eu
cd "$(dirname "$0")"
mkdir -p build/test-classes
javac --release 17 -d build/test-classes src/cli-app/skipass/src/main/java/com/skipass/*.java tests/VaultTest.java
java -cp build/test-classes com.skipass.VaultTest
