#!/usr/bin/env bash
set -euo pipefail
export LC_ALL=C

cd tmp

lite_sources=(./content/lite/*.txt)
normal_extra_sources=(./content/normal/*.txt)
plus_extra_sources=(./content/plus/*.txt)

normal_sources=("${lite_sources[@]}" "${normal_extra_sources[@]}")
plus_sources=("${normal_sources[@]}" "${plus_extra_sources[@]}")
update_time="$(TZ=UTC-8 date +'%Y-%m-%d %H:%M:%S')(GMT+8)"

build_filter() {
  local output=$1
  local title=$2
  shift 2
  local filtered="$output.filtered"
  local cleaned="$output.cleaned"
  local source

  {
    cat ../mod/rules/adblock-rules.txt
    for source in "$@"; do
      cat "$source"
      printf '\n'
    done
  } | grep -Ev '^(\!|\[|$)' | sort -u > "$filtered"

  grep -vxFf ../mod/rules/adblock-need-remove.txt "$filtered" > "$cleaned"
  python3 ../script/rule.py "$cleaned"

  {
    cat "$title"
    printf '! Version: %s\n' "$update_time"
    printf '! Total count: %s\n' "$(wc -l < "$cleaned")"
    cat "$cleaned"
  } > "$output"

  rm -f "$filtered" "$cleaned"
}

build_filter adblock_lite.txt ../mod/title/adblock_lite-title.txt "${lite_sources[@]}"
build_filter adblock.txt ../mod/title/adblock-title.txt "${normal_sources[@]}"
build_filter adblock_plus.txt ../mod/title/adblock_plus-title.txt "${plus_sources[@]}"

mv adblock_lite.txt adblock.txt adblock_plus.txt ../
