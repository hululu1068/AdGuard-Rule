<div align="center">
<h1>AdGuard Rule</h1>
  <p>
    一个简易的Java程序，用于合并与更新 AdGuard 过滤规则
</p>
</div>


<h2 id="a">📔 说明</h2>

本项目旨在按需求整合 `AdGuard` 规则。定时从上游订阅获取规则，去除`重复`和`不受支持`的规则并进行分类。如果存在误杀请手动放行。  
支持`AdGuard`、`AdGuard Home`,每`12小时`自动更新一次   

#### 上游规则

<details>
<summary>点击查看</summary>
<ul>
    <li><a href="https://github.com/hoshsadiq/adblock-nocoin-list/">adblock-nocoin-list</a></li>
    <li><a href="https://github.com/durablenapkin/scamblocklist">Scam Blocklist</a></li>
    <li><a href="https://someonewhocares.org/hosts/zero/hosts">Dan Pollock's List</a></li>
    <li><a href="https://cdn.jsdelivr.net/gh/AdguardTeam/FiltersRegistry/filters/filter_15_DnsFilter/filter.txt">AdGuard DNS filter</a></li>
    <li><a href="https://pgl.yoyo.org/adservers/serverlist.php?hostformat=adblockplus&showintro=1&mimetype=plaintext">Peter Lowe's List</a></li>
    <li><a href="https://abp.oisd.nl/basic/">OISD Blocklist Basic</a></li>
    <li><a href="https://adaway.org/hosts.txt">AdAway Default Blocklist</a></li>
    <li><a href="https://github.com/crazy-max/WindowsSpyBlocker">WindowsSpyBlocker</a></li>
    <li><a href="https://github.com/o0HalfLife0o/list">HalfLife（pc）</a></li>
    <li><a href="https://github.com/banbendalao/ADgk">Adgk</a></li>
    <li><a href="https://github.com/VeleSila/yhosts">yhosts</a></li>
    <li><a href="https://github.com/jdlingyu/ad-wars">ad-wars</a></li> 
    <li><a href="https://gitlab.com/quidsup/notrack-blocklists">NoTrack Tracker Blocklist</a></li> 
    <li><a href="https://gitlab.com/cats-team/adrules/">AdRules(AdGuard Full List)</a></li>
    <li><a href="https://raw.githubusercontent.com/AdguardTeam/FiltersRegistry/master/filters/filter_2_Base/filter.txt">AdGuard Base</a></li>
    # 自用添加↓
    <li><a href="https://anti-ad.net/easylist.txt">anti-AD</a></li>
    <li><a href="https://raw.githubusercontent.com/AdguardTeam/cname-trackers/master/combined_disguised_trackers.txt">AdGuard CNAME 伪装跟踪器列表</a></li>
    <li><a href="https://adguardteam.github.io/AdGuardSDNSFilter/Filters/filter.txt">AdGuard DNS filter</a></li>
    <li><a href="https://raw.githubusercontent.com/Crystal-RainSlide/AdditionalFiltersCN/master/CN.txt">AdditionalFiltersCN</a></li>
    <li><a href="https://raw.githubusercontent.com/banbendalao/ADgk/master/ADgk.txt">ADgk 移动广告规则</a></li>
    <li><a href="https://raw.githubusercontent.com/xinggsf/Adblock-Plus-Rule/master/rule.txt">乘风 广告过滤规则</a></li>
    <li><a href="https://raw.githubusercontent.com/xinggsf/Adblock-Plus-Rule/master/mv.txt">乘风 视频过滤规则</a></li>
    <li><a href="https://raw.githubusercontent.com/o0HalfLife0o/list/master/ad.txt"> HalfLife_合并自乘风视频广告过滤规则、EasylistChina、EasylistLite、CJX'sAnnoyance</a></li>
    <li><a href="https://adaway.org/hosts.txt">AdAway 官方的去广告 Host 规则</a></li>
    <li><a href="https://easylist-downloads.adblockplus.org/antiadblockfilters.txt">去除禁止广告拦截提示规则</a></li>
    <li><a href="https://raw.githubusercontent.com/VeleSila/yhosts/master/hosts.txt">Yhosts规则</a></li>
    <li><a href="https://raw.githubusercontent.com/Cats-Team/AdRules/main/dns.txt">杏稍AdRules DNS List</a></li>
    <li><a href="https://cdn.jsdelivr.net/gh/blackmatrix7/ios_rule_script@master/rule/AdGuard/Advertising/Advertising.txt">AdGuard_blackmatrix7合并</a></li>
    <li><a href="https://raw.githubusercontent.com/zsakvo/AdGuard-Custom-Rule/master/rule/zhihu.txt">知乎 普通版</a></li>
    <li><a href="https://github.com/217heidai/adblockfilters">217heidai/adblockfilters去重合并(比较大)</a></li>
    <li><a href="https://raw.githubusercontents.com/timlu85/AdGuard-Home_Youtube-Adfilter/master/Youtube-Adfilter-Web.txt">Youtube-Adfilter-Web</a></li>
    <li><a href="https://raw.githubusercontents.com/91ajames/ublock-filters-ulist-youtube/main/blocklist.txt">ublock-filters-ulist-youtube</a></li>
    # KoolProxy规则
    <li><a href="https://raw.iqiq.io/ilxp/koolproxy/master/rules/koolproxy.txt">静态规则</a></li>
    <li><a href="https://raw.iqiq.io/ilxp/koolproxy/master/rules/daily.txt">每日规则</a></li>
    <li><a href="https://raw.iqiq.io/ilxp/koolproxy/master/rules/steven.txt">StevenBlack规则</a></li>
    # uBlock内置规则
    <li><a href="https://cdn.jsdelivr.net/gh/uBlockOrigin/uAssetsCDN@main/filters/filters.txt">uBlock filters</a></li>
    <li><a href="https://ublockorigin.pages.dev/filters/badware.txt">uBlock filters – Badware risks</a></li>
    <li><a href="https://gitcdn.link/cdn/uBlockOrigin/uAssetsCDN/main/filters/privacy.txt">uBlock filters – Privacy</a></li>
    <li><a href="https://ublockorigin.github.io/uAssets/filters/quick-fixes.txt">uBlock filters – Quick fixes</a></li>
    <li><a href="https://cdn.statically.io/gh/uBlockOrigin/uAssetsCDN/main/filters/resource-abuse.txt">uBlock filters – Resource abuse</a></li>
    <li><a href="https://gitcdn.link/cdn/uBlockOrigin/uAssetsCDN/main/filters/unbreak.txt">uBlock filters – Unbreak</a></li>
    <li><a href="https://filters.adtidy.org/extension/ublock/filters/11.txt">AdGuard Mobile Ads移动设备</a></li>
</ul>
</details>

#### 本地规则

- [mylist](#)
> 主要是对上游规则的修正补充，根据日常使用体验，解除一些失误拦截

<h2 id="b">🎯 订阅</h2>

| 名称           | 说明                                                | Github Raw                                                                              | jsDelivr加速                                                                        |
|--------------|---------------------------------------------------|---------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------|
| `all.txt`    | 去重的规则合集，包含以下所有规则，适用于 `AdGuard` 客户端                | [✈️点此查看](https://raw.githubusercontent.com/hululu1068/AdGuard-Rule/main/rule/all.txt)      | [🚀点此查看(延迟)](https://cdn.jsdelivr.net/gh/hululu1068/AdGuard-Rule@main/rule/all.txt)    |
| `adgh.txt`   | 针对 `AdGuardHome` 的规则，包含 `domain.txt` `regex.txt`和`mylist.txt` | [✈️点此查看](https://raw.githubusercontent.com/hululu1068/AdGuard-Rule/main/rule/adgh.txt)   | [🚀点此查看(延迟)](https://cdn.jsdelivr.net/gh/hululu1068/AdGuard-Rule@main/rule/adgh.txt)   |
| `domain.txt` | 域名规则，`AdGuard`和`AdGuardHome`都支持                                       | [✈️点此查看](https://raw.githubusercontent.com/hululu1068/AdGuard-Rule/main/rule/domain.txt) | [🚀点此查看(延迟)](https://cdn.jsdelivr.net/gh/hululu1068/AdGuard-Rule@main/rule/domain.txt) |
| `hosts.txt`  | `hosts` 规则，~~包含一些访问加速~~                           | [✈️点此查看](https://raw.githubusercontent.com/hululu1068/AdGuard-Rule/main/rule/hosts.txt)  | [🚀点此查看(延迟)](https://cdn.jsdelivr.net/gh/hululu1068/AdGuard-Rule@main/rule/hosts.txt)  |
| `modify.txt` | 修饰规则，`AdGuard`支持                                      | [✈️点此查看](https://raw.githubusercontent.com/hululu1068/AdGuard-Rule/main/rule/modify.txt) | [🚀点此查看(延迟)](https://cdn.jsdelivr.net/gh/hululu1068/AdGuard-Rule@main/rule/modify.txt) |
| `regex.txt` | 正则规则，`AdGuardHome`支持                                       | [✈️点此查看](https://raw.githubusercontent.com/hululu1068/AdGuard-Rule/main/rule/regex.txt) | [🚀点此查看(延迟)](https://cdn.jsdelivr.net/gh/hululu1068/AdGuard-Rule@main/rule/regex.txt) |
| `mylist.txt` | 自用补充规则，人工更新                                       | [✈️点此查看](https://raw.githubusercontent.com/hululu1068/AdGuard-Rule/main/rule/mylist.txt) | [🚀点此查看(延迟)](https://cdn.jsdelivr.net/gh/hululu1068/AdGuard-Rule@main/rule/mylist.txt) |

<br/>
<h2 id="c">🛠️ 配置</h2>

#### 示例配置

```yaml
application:
  rule:       
    #远程规则订阅，仅支持http、https
    remote:
      - 'https://example.com/list.txt'
    #本地规则，请将文件移动到项目路径rule目录中
    local: 
      - 'mylist.txt'
  output:
    path: rule   #规则文件输出路径，相对路径默认从 项目目录开始
    files:
      all.txt:    #输出文件名
        - DOMAIN  #域名规则，仅完整域名
        - REGEX   #正则规则，包含正则的域名规则，AdGH支持
        - MODIFY  #修饰规则，添加了一些修饰符号的规则，AdG支持
        - HOSTS   #Hosts规则
```

#### 使用 Github Action

- fork本项目
- 参照示例配置，修改配置文件: `src/main/resources/application.yml`，注意本地规则文件应放入项目根目录 `rule` 文件夹
- 编辑 `.github/workflows/auto-update.yml` 文件，将 `Commit Changes` 区块下邮箱与用户名修改为自己的（Github邮箱与用户名）
- 提交所有修改并等待 `Github Action` 执行，执行完成后相应规则生成在配置中指定的目录下
- 👉 特别感谢@fordes123

<br/>
<h2 id="c">🧭 支持</h2>

<table>
  <tr>
    <td><a href="https://dartnode.com?aff=GrumpySalamander981"><img src="./src/img/DartNode.png" width="350" height="150" border="0" alt="Stop Overpaying Start Sharing Save More with DolOffer"></a></td>
    <td><a href="https://doloffer.com/friend/0ApoCZTz"><img src="./src/img/DolOffer.png" width="350" height="150" border="0" alt="Stop Overpaying Start Sharing Save More with DolOffer"></a></td>
  </tr>
</table>