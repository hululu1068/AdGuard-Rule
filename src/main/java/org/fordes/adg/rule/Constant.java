package org.fordes.adg.rule;

import java.io.File;

public class Constant {

    public static final String ROOT_PATH = System.getProperty("user.dir");

    public static final String UPDATE = "# Update time: {}\r\n";

    public static final String REPO = "# Repo URL: AdGuard、AdGuardHome广告过滤规则合并/去重\r\n\r\n###################################   合并/去重自以下规则   ####################################\r\n# - 'https://cdn.jsdelivr.net/gh/hoshsadiq/adblock-nocoin-list/hosts.txt'  #adblock-nocoin-list\r\n# - 'https://cdn.jsdelivr.net/gh/durablenapkin/scamblocklist/adguard.txt' #Scam Blocklist\r\n# - 'https://someonewhocares.org/hosts/zero/hosts' #Dan Pollock's List\r\n# - 'https://pgl.yoyo.org/adservers/serverlist.php?hostformat=adblockplus&showintro=1&mimetype=plaintext' #Peter Lowe's List\r\n# - 'https://abp.oisd.nl/basic/' #OISD Blocklist Basic\r\n# - 'https://cdn.jsdelivr.net/gh/crazy-max/WindowsSpyBlocker/data/hosts/spy.txt' #WindowsSpyBlocker\r\n# - 'https://raw.githubusercontent.com/jdlingyu/ad-wars/master/hosts' #大圣净化\r\n# - 'https://code.gitlink.org.cn/hacamer/AdRules/raw/branch/main/adguard-full.txt' #AdRules AdGuard Full List\r\n# - 'https://raw.githubusercontent.com/AdguardTeam/FiltersRegistry/master/filters/filter_2_Base/filter.txt' #adguard base\r\n# 自用添加↓\r\n# - 'https://anti-ad.net/easylist.txt' #name: anti-AD\r\n# - 'https://raw.githubusercontent.com/AdguardTeam/cname-trackers/master/combined_disguised_trackers.txt' #name: AdGuard CNAME 伪装跟踪器列表\r\n# - 'https://adguardteam.github.io/AdGuardSDNSFilter/Filters/filter.txt' #name: AdGuard DNS filter\r\n# - 'https://raw.githubusercontent.com/Crystal-RainSlide/AdditionalFiltersCN/master/CN.txt' #name: AdditionalFiltersCN\r\n# - 'https://raw.githubusercontent.com/banbendalao/ADgk/master/ADgk.txt' #name: ADgk 移动广告规则\r\n# - 'https://raw.githubusercontent.com/xinggsf/Adblock-Plus-Rule/master/rule.txt' #name: 乘风 广告过滤规则\r\n# - 'https://raw.githubusercontent.com/xinggsf/Adblock-Plus-Rule/master/mv.txt' #name: 乘风 视频过滤规则\r\n# - 'https://raw.githubusercontent.com/o0HalfLife0o/list/master/ad.txt' #name: HalfLife_合并自乘风视频广告过滤规则、EasylistChina、EasylistLite、CJX'sAnnoyance\r\n# - 'https://adaway.org/hosts.txt' #name: AdAway 官方的去广告 Host 规则\r\n# - 'https://easylist-downloads.adblockplus.org/antiadblockfilters.txt' #name: 去除禁止广告拦截提示规则\r\n# - 'https://raw.githubusercontent.com/VeleSila/yhosts/master/hosts.txt' #name: Yhosts规则\r\n# - 'https://raw.githubusercontent.com/Cats-Team/AdRules/main/dns.txt' #name: 杏稍AdRules DNS List\r\n# - 'https://cdn.jsdelivr.net/gh/blackmatrix7/ios_rule_script@master/rule/AdGuard/Advertising/Advertising.txt' #name: AdGuard_blackmatrix7合并\r\n# - 'https://raw.githubusercontent.com/zsakvo/AdGuard-Custom-Rule/master/rule/zhihu.txt' #name: 知乎 普通版\r\n# - 'https://github.com/217heidai/adblockfilters' #name: 217heidai/adblockfilters去重合并(比较大)\r\n# - 'https://raw.githubusercontents.com/timlu85/AdGuard-Home_Youtube-Adfilter/master/Youtube-Adfilter-Web.txt' #name: Youtube-Adfilter-Web\r\n# - 'https://raw.githubusercontents.com/91ajames/ublock-filters-ulist-youtube/main/blocklist.txt' #name: ublock-filters-ulist-youtube\r\n# KoolProxy规则\r\n# - 'https://raw.iqiq.io/ilxp/koolproxy/master/rules/koolproxy.txt' #name:静态规则\r\n# - 'https://raw.iqiq.io/ilxp/koolproxy/master/rules/daily.txt' #name:每日规则\r\n# - 'https://raw.iqiq.io/ilxp/koolproxy/master/rules/steven.txt' #name:StevenBlack规则\r\n# uBlock内置规则\r\n# - 'https://cdn.jsdelivr.net/gh/uBlockOrigin/uAssetsCDN@main/filters/filters.txt' #name: uBlock filters\r\n# - 'https://ublockorigin.pages.dev/filters/badware.txt' #name: uBlock filters – Badware risks\r\n# - 'https://gitcdn.link/cdn/uBlockOrigin/uAssetsCDN/main/filters/privacy.txt' #name: uBlock filters – Privacy\r\n# - 'https://ublockorigin.github.io/uAssets/filters/quick-fixes.txt' #name: uBlock filters – Quick fixes\r\n# - 'https://cdn.statically.io/gh/uBlockOrigin/uAssetsCDN/main/filters/resource-abuse.txt' #name: uBlock filters – Resource abuse\r\n# - 'https://gitcdn.link/cdn/uBlockOrigin/uAssetsCDN/main/filters/unbreak.txt' #name: uBlock filters – Unbreak\r\n# - 'https://filters.adtidy.org/extension/ublock/filters/11.txt' #name: AdGuard Mobile Ads移动设备\r\n# 本地规则\r\n# - 'mylist.txt'\r\n###############################################################################################\r\n\r\n# 每12小时同步一次、如有误杀、请手动解除\r\n\r\n";

    public static final String LOCAL_RULE_SUFFIX = ROOT_PATH + File.separator + "rule";

    /**
     * 基本的有效性检测正则，!开头，[]包裹，非特殊标记的#号开头均视为无效规则
     */
    public static final String EFFICIENT_REGEX = "^!|^#[^#,^@,^%,^\\$]|^\\[.*\\]$";

    /**
     * 去除首尾基础修饰符号 的正则，方便对规则进行分类
     * 包含：@@、||、@@||、/ 开头，$important、/ 结尾
     */
    public static final String BASIC_MODIFY_REGEX = "^@@\\|\\||^\\|\\||^@@|\\$important$|\\s#[^#]*$";

}
