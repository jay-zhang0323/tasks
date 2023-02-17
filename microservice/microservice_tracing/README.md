## REQUIREMENTS

【分散トレーシングの理解】

マイクロサービス内のサービスごとの処理の流れを追う。
例　マイクロサービスの処理の流れを追う。

		

　結果として、確認したいのが、以下のイメージ




		

一連の処理に、IDを埋め込み
　CloudLoggingにて、検索できるようにする。
　　観点１：サービス間のレスポンスを追いたいのか
　　　→Google Traceができそう。　　

　　トレースの詳細の表示  |  Cloud Trace  |  Google Cloud　　　　　




参考情報：

APIを埋め込む？Import trace とかで。どこに埋め込む？

[GCP Cloud Traceを使ってみた - GMOインターネットグループ グループ研究開発本部（次世代システム研究室）](https://recruit.gmo.jp/engineer/jisedai/blog/gcp-cloud-trace/)

[Open Telemetry + Google Cloud Trace やってみた (zenn.dev)](https://zenn.dev/satohjohn/articles/e37e8575966204)

トレースIDなどどうやって埋め込む？
Appigxや、kafukaとか、どうやって連動させる？（GCP外のサービス）
オンラインの場合、Appigxから入ってくる。

トレースの詳細の表示  |  Cloud Trace  |  Google Cloud　　　
OpenTelemetoryって何？
OpenSensusって何？
![image](https://user-images.githubusercontent.com/72186199/213107039-a1c9cefd-a2fc-4a91-a2b2-28cfe03bde09.png)
