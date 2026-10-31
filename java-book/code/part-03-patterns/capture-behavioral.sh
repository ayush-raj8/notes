#!/usr/bin/env bash
# Regenerates output/*.txt for chapters 41-49 (behavioral patterns) from real runs.
#   mains    -> java -cp classes <fqcn>            (stdout+stderr)
#   broken/  -> javac --release 21 <file>           (stderr, verbatim)
#   solutions-> javac + java
# Usage: capture-behavioral.sh [pkg ...]   (default: all nine behavioral packages)
set -u
HERE=$(cd "$(dirname "$0")" && pwd)
CODE=$(dirname "$HERE")
OUT="$HERE/output"
TMP=${TMPDIR:-/tmp}/p3cap
PKGS=("$@")
if [ ${#PKGS[@]} -eq 0 ]; then
  PKGS=(observer command state template chain iterator mediator visitor memento)
fi
prefix_of() { case "$1" in observer) echo Observer;; command) echo Command;; state) echo State;; template) echo Template;; chain) echo Chain;; iterator) echo Iterator;; mediator) echo Mediator;; visitor) echo Visitor;; memento) echo Memento;; esac; }
ch_of() { case "$1" in observer) echo 41;; command) echo 42;; state) echo 43;; template) echo 44;; chain) echo 45;; iterator) echo 46;; mediator) echo 47;; visitor) echo 48;; memento) echo 49;; esac; }
rm -rf "$TMP"; mkdir -p "$TMP/classes" "$OUT"
: > "$TMP/srcs.txt"
for p in "${PKGS[@]}"; do find "$HERE/src/main/java/patterns/behavioral/$p" -name '*.java' >> "$TMP/srcs.txt"; done
javac --release 21 -d "$TMP/classes" @"$TMP/srcs.txt" || { echo "main compile failed"; exit 1; }

for p in "${PKGS[@]}"; do
  # mains
  for f in $(grep -l "static void main" -r "$HERE/src/main/java/patterns/behavioral/$p"); do
    rel=${f#"$HERE/src/main/java/"}
    fq=$(echo "${rel%.java}" | tr / .)
    name=$(basename "${f%.java}")
    (cd "$HERE" && java -cp "$TMP/classes" "$fq" > "$OUT/$name.txt" 2>&1)
  done
  # broken
  for f in "$HERE"/broken/$(prefix_of $p)*Broken.java; do
    [ -e "$f" ] || continue
    name=$(basename "${f%.java}")
    rm -rf "$TMP/b"; mkdir -p "$TMP/b"
    (cd "$CODE" && javac --release 21 -d "$TMP/b" "part-03-patterns/broken/$name.java" > "$OUT/$name.txt" 2>&1)
    if [ ! -s "$OUT/$name.txt" ]; then echo "UNEXPECTED: $name compiled"; fi
  done
  # solutions
  d="$HERE/solutions/ch$(ch_of $p)"
  if ls "$d"/*.java >/dev/null 2>&1; then
    rm -rf "$TMP/s"; mkdir -p "$TMP/s"
    javac --release 21 -Xlint:none -cp "$TMP/classes" -d "$TMP/s" "$d"/*.java || echo "SOLUTION COMPILE FAILED ch$(ch_of $p)"
    for f in "$d"/*.java; do
      name=$(basename "${f%.java}")
      java -cp "$TMP/s:$TMP/classes" "solutions.ch$(ch_of $p).$name" > "$OUT/$name.txt" 2>&1
    done
  fi
done
echo captured
