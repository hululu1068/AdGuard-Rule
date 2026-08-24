#!/usr/bin/env bash
set -euo pipefail

## 一个简易的脚本，将GitHub-Hosts转化为SmartDNS支持的格式.

# GitHub hosts链接地址
url="https://raw.hellogithub.com/hosts"

# 配置文件、Title
echo "# Title: GitHub Hosts" > github-hosts.conf
echo "# Update: $(TZ=UTC-8 date +'%Y-%m-%d %H:%M:%S')(GMT+8)" >> github-hosts.conf

# 转化
curl --fail --location --silent --show-error --retry 3 "$url" \
  | grep -v "^\s*#\|^\s*$" \
  | awk 'NF >= 2 {print "address /"$2"/"$1}' >> github-hosts.conf

if [[ $(grep -c '^address /' github-hosts.conf) -eq 0 ]]; then
  echo "GitHub hosts source produced no entries" >&2
  exit 1
fi

# 移动到SmartDNS目录下
# mv github-hosts.conf /etc/smartdns/domain-set

exit
