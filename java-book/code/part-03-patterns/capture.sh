#!/usr/bin/env bash
# Re-captures real program output and real javac text for chapters 28-40
# (creational, structural, behavioral/strategy). Behavioral 41-49 use capture-behavioral.sh.
# Usage: code/part-03-patterns/capture.sh [path-substring]
#   output/<Class>.txt         stdout+stderr of each class that has main()
#   output/broken/<Class>.txt  javac output for each file in broken/
# Sources are compiled with javac into a private temp dir, so a concurrent
# `mvn` run cannot clobber the class files used here.
set -u
here="$(cd "$(dirname "$0")" && pwd)"
filter="$(printf %s "${1:-}" | tr A-Z a-z)"
cd "$here"
tmp=$(mktemp -d)
mine='/(creational|structural|strategy)/|solutions/ch(2[89]|3[0-9]|40)/'
find src/main/java solutions -name '*.java' | grep -E "$mine" > "$tmp/sources.txt"
javac --release 21 -d "$tmp/classes" @"$tmp/sources.txt" || { rm -rf "$tmp"; exit 1; }
mkdir -p output output/broken
while read -r f; do
  case "$(printf %s "$f" | tr A-Z a-z)" in *"$filter"*) ;; *) continue ;; esac
  grep -q 'static void main' "$f" || continue
  pkg=$(grep -m1 '^package ' "$f" | sed -E 's/package (.*);/\1/')
  cls=$(basename "$f" .java)
  java -cp "$tmp/classes" "$pkg.$cls" > "output/$cls.txt" 2>&1
done < "$tmp/sources.txt"
cd ..
for f in part-03-patterns/broken/*.java; do
  case "$(printf %s "$f" | tr A-Z a-z)" in *"$filter"*) ;; *) continue ;; esac
  name=$(basename "$f" .java)
  case "$name" in Observer*|Command*|State*|Template*|Chain*|Iterator*|Mediator*|Visitor*|Memento*) continue ;; esac
  mkdir -p "$tmp/b"
  javac -d "$tmp/b" -cp "$tmp/classes" "$f" > "part-03-patterns/output/broken/$name.txt" 2>&1
  [ $? -ne 0 ] || echo "WARN: $name compiled (expected failure)"
done
rm -rf "$tmp"
