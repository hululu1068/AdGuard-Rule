#!/bin/sh
LC_ALL='C'

download_file() {
  url=$1
  directory=$2
  filename=$(basename $url)
  filepath="$directory/$filename"
  retries=3
  while [ $retries -gt 0 ]; do
    if curl -sS -o $filepath $url; then
      echo "Downloaded $url successfully"
      # 在文件的第一行插入 ! url: $url
      sed -i "1i\\! url: $url" $filepath
      return  
    else
      echo "Failed to download $url, retrying..."
      retries=$((retries-1))
    fi
  done
  echo "Failed to download $url after 3 retries, exiting script."
  exit 1  
}

wait
# Create temporary folder
mkdir -p ./tmp/
cd tmp

# Start Download Filter File
echo 'Start Downloading...'

content=(  
  #damengzhu
  "https://raw.githubusercontent.com/damengzhu/banad/main/jiekouAD.txt" 
  #Noyllopa NoAppDownload
  "https://raw.githubusercontent.com/Noyllopa/NoAppDownload/master/NoAppDownload.txt" 
  #china
  #"https://filters.adtidy.org/extension/ublock/filters/224.txt" 
  #cjx
  "https://raw.githubusercontent.com/cjx82630/cjxlist/master/cjx-annoyance.txt"
  #anti-anti-ad
  "https://raw.githubusercontent.com/reek/anti-adblock-killer/master/anti-adblock-killer-filters.txt"
  #"https://easylist-downloads.adblockplus.org/antiadblockfilters.txt"
  #"https://easylist-downloads.adblockplus.org/abp-filters-anti-cv.txt"
  #--normal
  #Clean Url
  "https://raw.githubusercontent.com/DandelionSprout/adfilt/master/ClearURLs%20for%20uBo/clear_urls_uboified.txt" 
  #english opt
  "https://filters.adtidy.org/extension/ublock/filters/2_optimized.txt"
  #EasyListPrvacy
  "https://easylist-downloads.adblockplus.org/easyprivacy.txt"
  #--plus
  #ubo annoyance
  "https://raw.githubusercontent.com/uBlockOrigin/uAssets/master/filters/annoyances.txt" 
  #ubo privacy
  "https://raw.githubusercontent.com/uBlockOrigin/uAssets/master/filters/privacy.txt" 
  #adg base
  "https://filters.adtidy.org/windows/filters/2.txt" 
  #adg privacy
  "https://filters.adtidy.org/windows/filters/3.txt" 
  #adg cn
  "https://filters.adtidy.org/windows/filters/224.txt" 
  #adg annoyance
  "https://filters.adtidy.org/windows/filters/14.txt" 
)

dns=(
  #Ultimate Ad Filter (横幅、弹窗)(适合浏览器扩展)
  #"https://filters.adavoid.org/ultimate-ad-filter.txt"
  #Ultimate Privacy Filter （移动端隐私过滤）(终极隐私过滤器)
  #"https://filters.adavoid.org/ultimate-privacy-filter.txt"
  #Social 社交媒体过滤器 （适合国外网站）
  #"https://filters.adtidy.org/windows/filters/4.txt"
  #Annoying (!适合桌面端)
  #"https://filters.adtidy.org/windows/filters/14.txt"
  #"https://easylist-downloads.adblockplus.org/fanboy-annoyance.txt"
  #Mobile Ads (国内适配差)
  #"https://filters.adtidy.org/windows/filters/11.txt"
  #EasyList + AdGuard English filter # 英语网站
  #"https://filters.adtidy.org/windows/filters/2.txt"
  #"https://easylist-downloads.adblockplus.org/easylistchina+easylist.txt"
  #"https://filters.adtidy.org/windows/filters/224.txt" 
  #Fuck Tracking （主打隐私、跟踪）
  #"https://easylist-downloads.adblockplus.org/easyprivacy.txt"
  #"https://filters.adtidy.org/windows/filters/3.txt"
  #anti-coin （!误杀icon）
  #"https://raw.githubusercontent.com/hoshsadiq/adblock-nocoin-list/master/nocoin.txt"
  #scam
  "https://raw.githubusercontent.com/durablenapkin/scamblocklist/master/adguard.txt"
  #damengzhu (主要去除色情悬浮广告)
  #"https://raw.githubusercontent.com/damengzhu/banad/main/jiekouAD.txt"
  #xinggsf (乘风 视频过滤规则)(前面已合并)
  #"https://raw.githubusercontent.com/xinggsf/Adblock-Plus-Rule/master/mv.txt" 
  #uBO （!提取误差大）
  #"https://raw.githubusercontent.com/uBlockOrigin/uAssets/master/filters/annoyances.txt" 
  #"https://raw.githubusercontent.com/uBlockOrigin/uAssets/master/filters/badware.txt" 
  #"https://raw.githubusercontent.com/uBlockOrigin/uAssets/master/filters/filters.txt"
  #"https://raw.githubusercontent.com/uBlockOrigin/uAssets/master/filters/privacy.txt"
  #cjx #提取误差大、太多误杀了
  #"https://raw.githubusercontent.com/cjx82630/cjxlist/master/cjx-annoyance.txt"
  #anti-anti-ad
  "https://raw.githubusercontent.com/reek/anti-adblock-killer/master/anti-adblock-killer-filters.txt"
  #"https://easylist-downloads.adblockplus.org/antiadblockfilters.txt"
  #"https://easylist-downloads.adblockplus.org/abp-filters-anti-cv.txt"
  #HostsVN
  "https://raw.githubusercontent.com/bigdargon/hostsVN/master/filters/adservers-all.txt"
  #hosts
  #anti-windows-spy
  "https://raw.githubusercontent.com/crazy-max/WindowsSpyBlocker/master/data/hosts/spy.txt"
  #Notarck-Malware
  "https://gitlab.com/quidsup/notrack-blocklists/-/raw/master/malware.hosts"
  #hostsVN
  "https://raw.githubusercontent.com/bigdargon/hostsVN/master/filters/adservers-all.txt"
  #StevenBlack
  "https://raw.githubusercontent.com/StevenBlack/hosts/master/data/StevenBlack/hosts"
  #SomeoneNewWhoCares
  "https://someonewhocares.org/hosts/zero/hosts"
  #Brave
  "https://raw.githubusercontent.com/brave/adblock-lists/master/brave-lists/brave-firstparty.txt"
  #Me
  "https://raw.githubusercontent.com/Cats-Team/dns-filter/main/abp.txt"
  #Smart-TV
  "https://raw.githubusercontent.com/Perflyst/PiHoleBlocklist/master/SmartTV-AGH.txt"
  ### 自用添加 ↓ ###
  #KoolProxy规则
  #"https://github.com/ilxp/koolproxy/blob/main/rules/koolproxy.txt" #静态规则、不定时更新
  "https://raw.githubusercontent.com/ilxp/koolproxy/main/rules/daily.txt"
  "https://github.com/ilxp/koolproxy/blob/main/rules/adg.txt"
  #"https://github.com/ilxp/koolproxy/blob/main/rules/adgk.txt"
  #"https://github.com/ilxp/koolproxy/blob/main/rules/yhosts.txt"
  #"https://github.com/ilxp/koolproxy/blob/main/rules/steven.txt"
  #乘风 广告过滤规则 (下方已合并)
  #"https://raw.githubusercontent.com/xinggsf/Adblock-Plus-Rule/master/rule.txt"
  #"https://raw.githubusercontent.com/xinggsf/Adblock-Plus-Rule/master/mv.txt"
  #HalfLife吧 (合并自乘风视频广告过滤规则、EasylistChina、EasylistLite、CJX'sAnnoyance)
  "https://raw.githubusercontent.com/o0HalfLife0o/list/master/ad.txt"
  #AdditionalFiltersCN (适合浏览器扩展)
  #"https://raw.githubusercontent.com/Crystal-RainSlide/AdditionalFiltersCN/master/CN.txt"
  #AdGuard DNS (AdGuard Base filter, Social media filter, Tracking Protection filter, Mobile ads filter, EasyList, EasyPrivacy, etc)
  "https://adguardteam.github.io/AdGuardSDNSFilter/Filters/filter.txt"
  #GoodbyeAds-YouTube(可能误杀)
  #"https://raw.githubusercontent.com/jerryn70/GoodbyeAds/master/Formats/GoodbyeAds-YouTube-AdBlock-Filter.txt"
  #anti-AD easylist
  "https://raw.githubusercontent.com/privacy-protection-tools/anti-AD/master/anti-ad-adguard.txt"
  #Peter Lowe's List (国外网站居多)
  #"https://pgl.yoyo.org/adservers/serverlist.php?hostformat=adblockplus&showintro=1&mimetype=plaintext"
)

mkdir -p content
mkdir -p dns

for content in "${content[@]}"
do
  download_file $content "content"
done

for dns in "${dns[@]}" 
do
  download_file $dns "dns"
done

#修复换行符问题
sed -i 's/\r//' ./content/*.txt

echo 'Finish'

exit
