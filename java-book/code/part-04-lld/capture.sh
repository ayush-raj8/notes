#!/usr/bin/env bash
# Capture real stdout / javac output for chapter includes.
# Usage: ./capture.sh <package> <chNN>     e.g. ./capture.sh tictactoe ch61
# Needs: mvn -q compile already run (target/classes present). Uses PATH java/javac (Temurin/OpenJDK 21).
set -u
cd "$(dirname "$0")"
pkg="$1"
ch="$2"
out="out/$pkg"
tmp="$(mktemp -d)"
rm -rf "$out"
mkdir -p "$out"

# 1. every compiling class that declares main -> stdout
while IFS= read -r f; do
  rel="${f#src/main/java/}"
  cls="${rel%.java}"
  cls="${cls//\//.}"
  simple="${cls##*.}"
  java -cp target/classes "$cls" >"$out/$simple.txt" 2>&1
done < <(grep -rl 'static void main' "src/main/java/lld/$pkg" 2>/dev/null | sort)

# 2. every broken file -> javac diagnostics (verbatim)
for f in broken/"$pkg"/*.java; do
  [ -e "$f" ] || continue
  name="$(basename "$f" .java)"
  javac -d "$tmp" -cp target/classes "$f" >"$out/$name.javac.txt" 2>&1
  if [ $? -eq 0 ]; then echo "UNEXPECTED: $f compiled" >&2; fi
done

# 3. solutions: compile then run
if ls solutions/"$ch"/*.java >/dev/null 2>&1; then
  javac -d "$tmp/sol" -cp target/classes solutions/"$ch"/*.java || echo "SOLUTIONS FAILED $ch" >&2
  for f in solutions/"$ch"/*.java; do
    name="$(basename "$f" .java)"
    java -cp "target/classes:$tmp/sol" "$name" >"$out/$name.txt" 2>&1
  done
fi
rm -rf "$tmp"
echo "captured $pkg -> $out"
