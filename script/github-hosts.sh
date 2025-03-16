#!/bin/bash

## 一个简易的脚本，将GitHub-Hosts转化为SmartDNS支持的格式.

# GitHub hosts链接地址
url="https://raw.hellogithub.com/hosts"

# 配置文件、Title
echo "# Title: GitHub Hosts" > github-hosts.conf
echo "# Update: $(TZ=UTC-8 date +'%Y-%m-%d %H:%M:%S')(GMT+8)" >> github-hosts.conf

# 转化
curl -s "$url" | grep -v "^\s*#\|^\s*$" | awk '{print "address /"$2"/"$1}' >> github-hosts.conf

# 移动到SmartDNS目录下
# mv github-hosts.conf /etc/smartdns/domain-set

exit
