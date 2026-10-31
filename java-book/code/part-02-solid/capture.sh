#!/usr/bin/env bash
# Re-captures every program output and every javac failure used by Part 2 chapters.
# Run from java-book/code:  bash part-02-solid/capture.sh
# Outputs land in part-02-solid/out/{run,broken,solutions}; chapters include them verbatim.
set -u
cd "$(dirname "$0")/.."
MOD=part-02-solid
OUT=$MOD/out
rm -rf "$OUT" && mkdir -p "$OUT/run" "$OUT/broken" "$OUT/solutions"
TMP=$(mktemp -d)

mvn -q -pl $MOD compile >"$TMP/mvn.log" 2>&1 || { cat "$TMP/mvn.log"; exit 1; }
CP=$MOD/target/classes
fail=0

# 1. every main() under src/main/java
while IFS= read -r f; do
  cls=$(echo "${f#$MOD/src/main/java/}" | sed 's#/#.#g; s#\.java$##')
  name=$(basename "$f" .java)
  if [ -e "$OUT/run/$name.txt" ]; then echo "duplicate main name: $name"; fail=1; fi
  java -cp "$CP" "$cls" >"$OUT/run/$name.txt" 2>&1 || { echo "RUN FAILED: $cls"; fail=1; }
done < <(grep -rl 'static void main' $MOD/src/main/java | sort)

# 2. broken/: each must fail to compile; keep javac text verbatim
for f in $(ls $MOD/broken/*.java 2>/dev/null | sort); do
  name=$(basename "$f" .java)
  mkdir -p "$TMP/b"
  if javac --release 21 -Xlint:none -d "$TMP/b" -cp "$CP" "$f" >"$OUT/broken/$name.txt" 2>&1; then
    echo "BROKEN COMPILED: $f"; fail=1
  fi
done

# 3. solutions/: compile and run
for f in $(ls $MOD/solutions/*/*.java 2>/dev/null | sort); do
  name=$(basename "$f" .java)
  mkdir -p "$TMP/s"
  if javac --release 21 -Xlint:none -d "$TMP/s" -cp "$CP" "$f" >"$TMP/s.log" 2>&1; then
    if grep -q 'static void main' "$f"; then
      pkg=$(grep -m1 '^package ' "$f" | sed 's/package //; s/;//')
      java -cp "$TMP/s:$CP" "$pkg.$name" >"$OUT/solutions/$name.txt" 2>&1 || { echo "SOLUTION RUN FAILED: $f"; fail=1; }
    fi
  else
    echo "SOLUTION DID NOT COMPILE: $f"; cat "$TMP/s.log"; fail=1
  fi
done

rm -rf "$TMP"
[ $fail -eq 0 ] && echo "capture ok" || { echo "capture finished with problems"; exit 1; }
