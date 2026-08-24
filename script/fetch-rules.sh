#!/usr/bin/env bash
set -euo pipefail
export LC_ALL=C

download_file() {
  local url=$1
  local directory=$2
  local filename
  local identifier
  local filepath
  local temporary

  identifier=$(printf '%s' "$url" | cksum | awk '{print $1}')
  filename="$identifier-$(basename "${url%%\?*}")"
  filepath="$directory/$filename"
  temporary="$filepath.tmp"

  for attempt in 1 2 3; do
    if curl --fail --location --silent --show-error \
      --connect-timeout 20 --max-time 180 --output "$temporary" "$url"; then
      if [[ ! -s "$temporary" ]]; then
        echo "Downloaded empty file: $url" >&2
      elif head -c 512 "$temporary" | grep -Eiq '<!doctype[[:space:]]+html|<html'; then
        echo "Downloaded HTML instead of rules: $url" >&2
      else
        {
          printf '! url: %s\n' "$url"
          cat "$temporary"
          printf '\n'
        } > "$filepath"
        rm -f "$temporary"
        echo "Downloaded $url"
        return 0
      fi
    fi
    rm -f "$temporary"
    echo "Download attempt $attempt failed: $url" >&2
  done

  echo "Failed to download a valid rule file: $url" >&2
  return 1
}

download_group() {
  local directory=$1
  shift
  local url
  mkdir -p "$directory"
  for url in "$@"; do
    download_file "$url" "$directory"
  done
}

content_lite=(
  "https://raw.githubusercontent.com/damengzhu/banad/main/jiekouAD.txt"
  "https://raw.githubusercontent.com/Noyllopa/NoAppDownload/master/NoAppDownload.txt"
  "https://raw.githubusercontent.com/cjx82630/cjxlist/master/cjx-annoyance.txt"
  "https://raw.githubusercontent.com/reek/anti-adblock-killer/master/anti-adblock-killer-filters.txt"
)

content_normal=(
  "https://raw.githubusercontent.com/DandelionSprout/adfilt/master/ClearURLs%20for%20uBo/clear_urls_uboified.txt"
  "https://filters.adtidy.org/extension/ublock/filters/2_optimized.txt"
  "https://easylist-downloads.adblockplus.org/easyprivacy.txt"
)

content_plus=(
  "https://raw.githubusercontent.com/uBlockOrigin/uAssets/master/filters/annoyances.txt"
  "https://raw.githubusercontent.com/uBlockOrigin/uAssets/master/filters/privacy.txt"
  "https://filters.adtidy.org/windows/filters/2.txt"
  "https://filters.adtidy.org/windows/filters/3.txt"
  "https://filters.adtidy.org/windows/filters/224.txt"
  "https://filters.adtidy.org/windows/filters/14.txt"
)

dns=(
  "https://raw.githubusercontent.com/durablenapkin/scamblocklist/master/adguard.txt"
  "https://raw.githubusercontent.com/reek/anti-adblock-killer/master/anti-adblock-killer-filters.txt"
  "https://raw.githubusercontent.com/bigdargon/hostsVN/master/filters/adservers-all.txt"
  "https://raw.githubusercontent.com/crazy-max/WindowsSpyBlocker/master/data/hosts/spy.txt"
  "https://gitlab.com/quidsup/notrack-blocklists/-/raw/master/malware.hosts"
  "https://raw.githubusercontent.com/StevenBlack/hosts/master/data/StevenBlack/hosts"
  "https://someonewhocares.org/hosts/zero/hosts"
  "https://raw.githubusercontent.com/brave/adblock-lists/master/brave-lists/brave-firstparty.txt"
  "https://raw.githubusercontent.com/Cats-Team/dns-filter/main/abp.txt"
  "https://raw.githubusercontent.com/Perflyst/PiHoleBlocklist/master/SmartTV-AGH.txt"
  "https://raw.githubusercontent.com/ilxp/koolproxy/main/rules/daily.txt"
  "https://raw.githubusercontent.com/ilxp/koolproxy/main/rules/adg.txt"
  "https://adguardteam.github.io/AdGuardSDNSFilter/Filters/filter.txt"
  "https://raw.githubusercontent.com/privacy-protection-tools/anti-AD/master/anti-ad-adguard.txt"
)

rm -rf ./tmp
mkdir -p ./tmp

download_group ./tmp/content/lite "${content_lite[@]}"
download_group ./tmp/content/normal "${content_normal[@]}"
download_group ./tmp/content/plus "${content_plus[@]}"
download_group ./tmp/dns "${dns[@]}"

python3 ./script/normalize-downloads.py ./tmp

echo "Downloaded and validated all upstream rules"
