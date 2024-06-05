# Sigmob-Topon-Adapter-Android
基于Topon v6.2.80聚合，实现自定义适配器支持Sigmob新插屏功能

## 目录结构
```
|---- App
|     |---- Libs  # Topon SDK & Sigmob SDK
|     |---- src 
|           |---- main
|                |---- java/com/customsigmobadapter   # 基于topon聚合的自定义渠道实现sigmob新插屏的适配器
|                              |---- CustomSigmobATBiddingNotice.java    # C2S竞价的通知类
|                              |---- CustomSigmobATBiddingATInterstitialAdapter.java   # C2S 竞价及普通新插屏广告加载及播放
```



**在基于Topon聚合实现sigmob自定义适配器接入新插屏时，需要确保自定义ADN和topon支持的sigmob ADN的appId保持一致。**



实现自定义ADN，首先需要在Topon平台创建自定义ADN广告源，同时填写对应广告类型的适配器包名及类名，本示例新插屏的适配器类名：**com.customsigmobadapter.CustomSigmobATBiddingATInterstitialAdapter**


本示例接收的自定义参数在topon平台配置如下

{"app_id": "xxxx", "app_key": "xxxxx", "placement_id": "xxxxx"}

开发者可根据业务自行调整参数。


Topon官方自定义广告平台文档：[https://docs.toponad.com/?id=/zh-cn/android/adroid_adv_doc/customnetwork/custom_network_adapter#/zh-cn/android/adroid_adv_doc/customnetwork/custom_network_adapter](https://docs.toponad.com/?id=/zh-cn/android/adroid_adv_doc/customnetwork/custom_network_adapter#/zh-cn/android/adroid_adv_doc/customnetwork/custom_network_adapter)

实现自定义 header bidding：[https://docs.toponad.com/#/zh-cn/android/adroid_adv_doc/customnetwork/custom_network_c2s_adapter_simple](https://docs.toponad.com/#/zh-cn/android/adroid_adv_doc/customnetwork/custom_network_c2s_adapter_simple)


