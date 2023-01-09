 ### 4.1 構成管理対象成果物 (scope)

アプリケーションに提供するベースマニフェストは、以下のファイルが存在する。</BR>
下記を参考にして、使用するベースマニフェストを選定する。</BR>

※ベースマニフェスト上の記載内容が異なるため、下記を参考にして使用するベースマニフェストを選定する。</BR>

- 常駐Pod用ベースマニフェスト</BR>
「コンテナコンポーネント一覧」の「常駐/非常駐」列に「常駐」と記載のあるコンテナが使用する。</BR>
Podが起動されると、オンラインアプリケーション処理を開始しPodが常駐する。</BR>

- 非常駐Pod用ベースマニフェスト</BR>
「コンテナコンポーネント一覧」の「常駐/非常駐」列に「非常駐」と記載のあるコンテナが使用する。</BR>
JP1やS3監視ジョブからPodが起動され、バッチアプリケーション処理が終了することでPodが終了する。</BR>

下記に示す `ap-values-[環境].yaml` 以外のファイルの修正は行わないこと。</BR>

| 常駐/非常駐Pod | ファイルの入手先 | ファイル名 | 用途 | 備考 |
| - | - | - | - | - |
| 常駐 | manifest-blank-project | ap-values-com.yaml | 環境共通定義 |  |
| ^ | ^ | ap-values-dev.yaml | 保守開発環境定義 |  |
| ^ | ^ | ap-values-stg.yaml | ステージング環境定義 | ※環境引き渡し時に提供予定 |
| ^ | ^ | ap-values-prd.yaml | 本番環境定義 | ※環境引き渡し時に提供予定 |
| 非常駐 | manifest-batch-blank-project | ap-values-com.yaml | 環境共通定義 |  |
| ^ | ^ | ap-values-dev.yaml | 保守開発環境定義 |  |
| ^ | ^ | ap-values-stg.yaml | ステージング環境定義 | ※環境引き渡し時に提供予定 |
| ^ | ^ | ap-values-prd.yaml | 本番環境定義 | ※環境引き渡し時に提供予定 |

### 4.2 ベースマニフェストの構成管理

ベースマニフェストは以下のリポジトリで管理する。</BR>

| 常駐/非常駐Pod | リポジトリ |
| - | - |
| 常駐Pod | https://github.com/ANA-CASG/manifest-blank-project.git (*TODO* update repository) |
| 非常駐Pod | https://github.com/ANA-CASG/manifest-batch-blank-project.git (*TODO* update repository)|

それぞれのリポジトリにおいて、以下のファイルをベースマニフェストとして提供する。

- 常駐Pod用ベースマニフェスト</BR>
manifest-blank-projectリポジトリの構成は以下の通り。</BR>
  ```
  (manifest-blank-project)
   └ app
      ├ templates               ・・・マニフェストテンプレートリポジトリ(sub-module)
      │ ├ values
      │ │ ├ values-com.yaml
      │ │ ├ values-dev.yaml
      │ │ ├ values-prd.yaml
      │ │ └ values-stg.yaml
      │ ├ configmap.yaml
      │ ├ ingress.yaml
      │ └ service.yaml
      ├ Chart.yaml
      ├ ap-values-com.yaml    ・・・本書にしたがってアプリチームが定義する設定ファイル(環境共通)
      └ ap-values-dev.yaml    ・・・本書にしたがってアプリチームが定義する設定ファイル(保守開発環境用)
  ```

- 非常駐Pod用ベースマニフェスト</BR>
manifest-batch-blank-projectリポジトリの構成は以下の通り。</BR>
  ```
  (manifest-batch-blank-project)
   └ app
      ├ templates               ・・・マニフェストテンプレートリポジトリ(sub-module)
      │ ├ values
      │ │ ├ values-com.yaml
      │ │ ├ values-dev.yaml
      │ │ ├ values-prd.yaml
      │ │ └ values-stg.yaml
      │ ├ configmap.yaml
      │ └ job.yaml
      ├ Chart.yaml
      ├ ap-values-com.yaml    ・・・本書にしたがってアプリチームが定義する設定ファイル(環境共通)
      └ ap-values-dev.yaml    ・・・本書にしたがってアプリチームが定義する設定ファイル(保守開発環境)
  ```

- マニフェストファイルの共通定義(テンプレート)はテンプレート用のリポジトリで管理する。アプリチームでのカスタマイズは不可。</BR>
- マニフェストファイルの共通定義(テンプレート)は、システム共通環境変数を定義しており、アプリケーションで読み取り利用することが可能。</BR>

【システム共通環境変数】</BR>
以下に一例を示す。</BR>
その他環境変数は資料：「環境変数一覧」を参照

| 環境変数 | 名称 | 内容 | 定義値 |
| - | - | - | - |
| TZ | タイムゾーン | Podのタイムゾーンを定義 | Asia/Tokyo |
| CONTAINER_NAME | コンテナ名 | アプリケーション名が設定される(4.4.1.1 name(名前)と同じ) | (例)casp-a101001-01 |
| ENV_EXEC_BASE | 実行環境 | 本番(prd)/ステージング(srg)/保守開発(dev)を定義 | (例)dev |

- マニフェストリポジトリをベアクローンして利用し、 `ap-values-[環境].yaml` にアプリケーションで必要な値の定義を行う。</BR>


<img src="./img_42.jpg" width=100%>


### 4.3 設定ファイルの適用ルール
ベースマニフェストにはアプリチームがカスタマイズ可能な設定ファイルが4つ存在する。設定ファイルには適用される際の優先度があるため、下記に記載するルールに従いカスタマイズを行うこと。</BR>

【設定ファイル
- 共通設定(ap-values-com.yaml)</BR>
- 環境毎設定(ap-values-dev.yaml、ap-values-stg.yaml、ap-values-prd.yaml)</BR>

【設定ファイルのカスタマイズルール】</BR>
- **利用しない設定の値には""を定義する。**
  - GCS の利用が不要な場合は、gcsに関連する設定値を全て""にする。
  - AlloyDBへの接続が不要な場合は、Auroraに関連する設定値を全て""にする。
  など。
- 本番/ステージング/保守開発環境に対して同じ値を設定する場合は、`ap-values-com.yaml`にのみ定義を行い、`ap-values-[環境].yaml`の同一定義箇所はコメントアウトする。
- 本番/ステージング/保守開発環境で異なる定義を行いたい場合は、`ap-values-[環境].yaml`それぞれに値を定義する。

【設定ファイルの適用優先順位】

| 環境 | 説明 |
| - | - |
| 本番環境 | `ap-values-com.yaml` のあとに`ap-values-prd.yaml`の読み込み後を行う。</BR>両ファイルに同一の定義がある場合は `ap-values-prd.yaml` の内容が優先的に適用される。 |
| ステージング環境 | `ap-values-com.yaml` のあとに`ap-values-stg.yaml`の読み込み後を行う。</BR>両ファイルに同一の定義がある場合は `ap-values-stg.yaml` の内容が優先的に適用される。 |
| 保守開発環境 | `ap-values-com.yaml` のあとに`ap-values-dev.yaml`の読み込み後を行う。</BR>両ファイルに同一の定義がある場合は `ap-values-dev.yaml` の内容が優先的に適用される。 |

`ap-values-[環境].yaml` のカスタマイズ要素一覧を以下に示す。

| | | 「共通設定」「環境毎設定」の凡例 |
| - | - | - | 
| ○ | 定義必須 | 必ず定義する項目。</BR>環境毎設定を定義した場合は共通設定(ap-values-com.yaml)は無効となる。 |
| △ | 選択定義 | 共通設定、環境毎設定のどちらかを定義する。</BR>両方に記載した場合、共通設定はコメントアウトする必要はなく、環境毎設定が有効となる。</BR>共通設定を有効にしたい場合、環境毎設定をコメントアウトする。</BR>全環境同一定義となる場合は、共通設定のみとし環境毎設定はコメントアウトする。 |
| × | 定義不可 | |
| 空欄 | 定義不要 | |

| |「常駐/非常駐」の凡例 |
| - | - |
| A | 常駐型でのみ使用する項目 |
| C | 非常駐型でのみ使用する項目 |
| AC | 常駐型、非常駐型ともに使用する項目 |

| カスタマイズ① | カスタマイズ② | カスタマイズ③ | 概要 | デフォルト値 | 共通設定 | 環境毎設定 | 常駐/非常駐 |
| - | - | - | - | - | - | - | - |
| name |  |  | アプリケーションに付与される名称</BR>Pod名も同様となる。 |  | ○ | × | AC |
| apversion |  |  | アプリケーションのバージョン |  | △ | △ | AC |
| namespace |  |  | ネームスペース |  | × | ○ | AC |
| replicaCount |  |  | Podの起動数 | 1 | △ | △ | A |
| image |  |  | アプリケーションのコンテナイメージ |  | × | ○ | AC |
| ^ | repository |  | 起動するコンテナイメージのECRリポジトリのリポジトリ名 |  | × | ○ | AC |
| ^ | tag |  | ECRリポジトリのタグ名</BR>アプリケーションソースリポジトリのCIにより書き換えられる。 |  | × | ○ | AC |
| ports |  |  | ポート番号 |  |  |  | A |
| ^ | containerPort |  | コンテナの待ち受けポート番号 | 8080 | ○ | × | A |
| livenessProbe |  |  | Pod死活監視定義 |  |  |  | A |
| ^ | failureThreshold |  | 調査開始間隔(秒) | 3 | △ | △ | A |
| ^ | periodSeconds |  | 調査開始間隔(回数) | 10 | △ | △ | A |
| readinessProbe |  |  | Pod読み取り監視定義 |  |  |  | A |
| ^ | failureThreshold |  | 調査開始間隔(秒) | 3 | △ | △ | A |
| ^ | periodSeconds |  | 調査開始間隔(回数) | 10 | △ | △ | A |
| startupProbe |  |  | Pod読み取り監視遅延定義 |  |  |  | A |
| ^ | failureThreshold |  | 調査開始間隔(秒) | 30 | △ | △ | A |
| ^ | periodSeconds |  | 調査開始間隔(回数) | 10 | △ | △ | A |
| ingress |  |  | Ingressの定義 |  |  |  | A |
| ^ | create |  | Ingressを作成する／しないを true/falseで定義 | false | ○ | × | A |
| ^ | order |  | ALBの読み取り優先順位 |  | ○ | × | A |
| ^ | path |  | アクセスパス |  | ○ | × | A |
| ^ | pathtype |  | パスのタイプ |  | ○ | × | A |
| resources |  |  | リソース定義 |  |  |  | AC |
| ^ | limits |  | リソース制限 |  |  |  | AC |
| ^ | ^ | cpu | CPUの制限 | 0.25 | △ | △ | AC |
| ^ | ^ | memory | メモリの制限 | 0.5G | △ | △ | AC |
| ^ | requests |  | 要求 |  |  |  | AC |
| ^ | ^ | cpu | 起動時のCPU | 0.25 | △ | △ | AC |
| ^ | ^ | memory | 起動時のメモリ | 0.5G | △ | △ | AC |
| env |  |  | 環境変数 |  |  |  | AC |
| ^ | emptydirvalue |  | 使用するemptyDir |  | △ | △ | AC |
| ^ | snstopicvalue |  | 使用するSNSトピック名 |  | △ | △ | AC |
| ^ | sqsqueuevalue |  | 使用するSQSキュー名 |  | △ | △ | AC |
| ^ | amqtopicname |  | 使用するActiveMQトピック名 |  | △ | △ | AC |
| ^ | amqqueuename |  | 使用するActiveMQキュー名 |  | △ | △ | AC |
| ^ | amqbrokerurl |  | 使用するActiveMQブローカーURL |  | △ | △ | AC |
| ^ | cm |  | コンフィグマップ(cm)参照 |  |  |  | AC |
| ^ | ^ | s3bucketname | 使用するS3バケットcm名 |  | △ | △ | AC |
| ^ | ^ | s3bucketkey | 使用するS3バケットcmキー |  | △ | △ | AC |
| ^ | ^ | postgreshostname | DBホスト名参照先cm名 |  | △ | △ | AC |
| ^ | ^ | postgreshostkey | DBホスト名参照先cmキー |  | △ | △ | AC |
| ^ | ^ | postgresportname | DBポート番号参照先cm名 |  | △ | △ | AC |
| ^ | ^ | postgresportkey | DBポート番号参照先cmキー |  | △ | △ | AC |
| ^ | ^ | postgresdatabasename | DB名参照先cm名 |  | △ | △ | AC |
| ^ | ^ | postgresdatabasekey | DB名参照先cmキー |  | △ | △ | AC |
| ^ | ^ | postgresusername | DBユーザ名参照先cm名 |  | △ | △ | AC |
| ^ | ^ | postgresuserkey | DBユーザ名参照先cmキー |  | △ | △ | AC |
| ^ | ^ | redishostname | Redisホスト名参照先cm名 |  | △ | △ | AC |
| ^ | ^ | redishostkey | Redisホスト名参照先cmキー |  | △ | △ | AC |
| ^ | ^ | redisportname | Redisポート番号参照先cm名 |  | △ | △ | AC |
| ^ | ^ | redisportkey | Redisポート番号参照先cmキー |  | △ | △ | AC |
| ^ | ^ | redisclustername | Redisクラスター名参照先cm名 |  | △ | △ | AC |
| ^ | ^ | redisclusterkey | Redisクラスター名参照先cmキー |  | △ | △ | AC |
| ^ | ^ | redisnumbername | RedisDB番号参照先cm名 |  | △ | △ | AC |
| ^ | ^ | redisnumberkey | RedisDB番号参照先cmキー |  | △ | △ | AC |
| ^ | ^ | redisusername | Redisユーザ名参照先cm名 |  | △ | △ | AC |
| ^ | ^ | redisuserkey | Redisユーザ名参照先cmキー |  | △ | △ | AC |
| ^ | ^ | dynamodbtablename | DynamoDBテーブル名参照先cm名 |  | △ | △ | AC |
| ^ | ^ | dynamodbtablekey | DynamoDBテーブル名参照先cmキー |  | △ | △ | AC |
| ^ | ^ | wsapcpmuseridname | 帳票作成/メール作成用PNR取得ユーザID参照先cm名 |  | △ | △ | AC |
| ^ | ^ | wsapcpmuseridkey | 帳票作成/メール作成用PNR取得等ユーザID参照先cmキー |  | △ | △ | AC |
| ^ | ^ | wsapcpmagentdutycodename | 帳票作成/メール作成用PNR取得等AgentDutyCode参照先cm名 |  | △ | △ | AC |
| ^ | ^ | wsapcpmagentdutycodekey | 帳票作成/メール作成用PNR取得等AgentDutyCode参照先cmキー |  | △ | △ | AC |
| ^ | ^ | wsapcpmpostypename | 帳票作成/メール作成用PNR取得等POS Type参照先cm名 |  | △ | △ | AC |
| ^ | ^ | wsapcpmpostypekey | 帳票作成/メール作成用PNR取得等POS Type参照先cmキー |  | △ | △ | AC |
| ^ | ^ | wsapcpmrequestortypename | 帳票作成/メール作成用PNR取得等RequestorType参照先cm名 |  | △ | △ | AC |
| ^ | ^ | wsapcpmrequestortypekey | 帳票作成/メール作成用PNR取得等RequestorType参照先cmキー |  | △ | △ | AC |
| ^ | ^ | wsapcpmnhofficeidname | 帳票作成/メール作成用PNR取得等(OfficeID:NH)参照先cm名 |  | △ | △ | AC |
| ^ | ^ | wsapcpmnhofficeidkey | 帳票作成/メール作成用PNR取得等(OfficeID:NH)参照先cmキー |  | △ | △ | AC |
| ^ | ^ | wsapcpmhdofficeidname | 帳票作成/メール作成用PNR取得等(OfficeID:HD)参照先cm名 |  | △ | △ | AC |
| ^ | ^ | wsapcpmhdofficeidkey | 帳票作成/メール作成用PNR取得等(OfficeID:HD)参照先cmキー |  | △ | △ | AC |
| ^ | ^ | wsapcpmfwofficeidname | 帳票作成/メール作成用PNR取得等(OfficeID:FW)参照先cm名 |  | △ | △ | AC |
| ^ | ^ | wsapcpmfwofficeidkey | 帳票作成/メール作成用PNR取得等(OfficeID:FW)参照先cmキー |  | △ | △ | AC |
| ^ | ^ | wsapcpm7gofficeidname | 帳票作成/メール作成用PNR取得等(OfficeID:7G)参照先cm名 |  | △ | △ | AC |
| ^ | ^ | wsapcpm7gofficeidkey | 帳票作成/メール作成用PNR取得等(OfficeID:7G)参照先cmキー |  | △ | △ | AC |
| ^ | ^ | wsapcpmocofficeidname | 帳票作成/メール作成用PNR取得等(OfficeID:OC)参照先cm名 |  | △ | △ | AC |
| ^ | ^ | wsapcpmocofficeidkey | 帳票作成/メール作成用PNR取得等(OfficeID:OC)参照先cmキー |  | △ | △ | AC |
| ^ | ^ | wsapcpm6jofficeidname | 帳票作成/メール作成用PNR取得等(OfficeID:6J)参照先cm名 |  | △ | △ | AC |
| ^ | ^ | wsapcpm6jofficeidkey | 帳票作成/メール作成用PNR取得等(OfficeID:6J)参照先cmキー |  | △ | △ | AC |
| ^ | ^ | wsapcivuseridname | インベントリ情報取得ユーザID参照先cm名 |  | △ | △ | AC |
| ^ | ^ | wsapcivuseridkey | インベントリ情報取得ユーザID参照先cmキー |  | △ | △ | AC |
| ^ | ^ | wsapcivagentdutycodename | インベントリ情報取得AgentDutyCode参照先cm名 |  | △ | △ | AC |
| ^ | ^ | wsapcivagentdutycodekey | インベントリ情報取得AgentDutyCode参照先cmキー |  | △ | △ | AC |
| ^ | ^ | wsapcivpostypename | インベントリ情報取得POS Type参照先cm名 |  | △ | △ | AC |
| ^ | ^ | wsapcivpostypekey | インベントリ情報取得POS Type参照先cmキー |  | △ | △ | AC |
| ^ | ^ | wsapcivrequestortypename | インベントリ情報取得RequestorType参照先cm名 |  | △ | △ | AC |
| ^ | ^ | wsapcivrequestortypekey | インベントリ情報取得RequestorType参照先cmキー |  | △ | △ | AC |
| ^ | ^ | wsapcivnhofficeidname | インベントリ情報取得(OfficeID:NH)参照先cm名 |  | △ | △ | AC |
| ^ | ^ | wsapcivnhofficeidkey | インベントリ情報取得(OfficeID:NH)参照先cmキー |  | △ | △ | AC |
| ^ | ^ | wsapcivhdofficeidname | インベントリ情報取得(OfficeID:HD)参照先cm名 |  | △ | △ | AC |
| ^ | ^ | wsapcivhdofficeidkey | インベントリ情報取得(OfficeID:HD)参照先cmキー |  | △ | △ | AC |
| ^ | ^ | wsapcivfwofficeidname | インベントリ情報取得(OfficeID:FW)参照先cm名 |  | △ | △ | AC |
| ^ | ^ | wsapcivfwofficeidkey | インベントリ情報取得(OfficeID:FW)参照先cmキー |  | △ | △ | AC |
| ^ | ^ | wsapciv7gofficeidname | インベントリ情報取得(OfficeID:7G)参照先cm名 |  | △ | △ | AC |
| ^ | ^ | wsapciv7gofficeidkey | インベントリ情報取得(OfficeID:7G)参照先cmキー |  | △ | △ | AC |
| ^ | ^ | wsapcivocofficeidname | インベントリ情報取得(OfficeID:OC)参照先cm名 |  | △ | △ | AC |
| ^ | ^ | wsapcivocofficeidkey | インベントリ情報取得(OfficeID:OC)参照先cmキー |  | △ | △ | AC |
| ^ | ^ | wsapciv6jofficeidname | インベントリ情報取得(OfficeID:6J)参照先cm名 |  | △ | △ | AC |
| ^ | ^ | wsapciv6jofficeidkey | インベントリ情報取得(OfficeID:6J)参照先cmキー |  | △ | △ | AC |
| ^ | ^ | wsapcfruseridname | SPOOK用運賃取得ユーザID参照先cm名 |  | △ | △ | AC |
| ^ | ^ | wsapcfruseridkey | SPOOK用運賃取得ユーザID参照先cmキー |  | △ | △ | AC |
| ^ | ^ | wsapcfragentdutycodename | SPOOK用運賃取得AgentDutyCode参照先cm名 |  | △ | △ | AC |
| ^ | ^ | wsapcfragentdutycodekey | SPOOK用運賃取得AgentDutyCode参照先cmキー |  | △ | △ | AC |
| ^ | ^ | wsapcfrpostypename | SPOOK用運賃取得POS Type参照先cm名 |  | △ | △ | AC |
| ^ | ^ | wsapcfrpostypekey | SPOOK用運賃取得POS Type参照先cmキー |  | △ | △ | AC |
| ^ | ^ | wsapcfrrequestortypename | SPOOK用運賃取得RequestorType参照先cm名 |  | △ | △ | AC |
| ^ | ^ | wsapcfrrequestortypekey | SPOOK用運賃取得RequestorType参照先cmキー |  | △ | △ | AC |
| ^ | ^ | wsapcfrnhdc1officeidname | SPOOK用運賃取得(OfficeID:NH-DC1)e参照先cm名 |  | △ | △ | AC |
| ^ | ^ | wsapcfrnhdc1officeidkey | SPOOK用運賃取得(OfficeID:NH-DC1)参照先cmキー |  | △ | △ | AC |
| ^ | ^ | wsapcfrnhdc2officeidname | SPOOK用運賃取得(OfficeID:NH-DC2)e参照先cm名 |  | △ | △ | AC |
| ^ | ^ | wsapcfrnhdc2officeidkey | SPOOK用運賃取得(OfficeID:NH-DC2)参照先cmキー |  | △ | △ | AC |
| ^ | ^ | wsapcfrnhdc3officeidname | SPOOK用運賃取得(OfficeID:NH-DC3)e参照先cm名 |  | △ | △ | AC |
| ^ | ^ | wsapcfrnhdc3officeidkey | SPOOK用運賃取得(OfficeID:NH-DC3)参照先cmキー |  | △ | △ | AC |
| ^ | ^ | wsapcfrnhdc4officeidname | SPOOK用運賃取得(OfficeID:NH-DC4)e参照先cm名 |  | △ | △ | AC |
| ^ | ^ | wsapcfrnhdc4officeidkey | SPOOK用運賃取得(OfficeID:NH-DC4)参照先cmキー |  | △ | △ | AC |
| ^ | ^ | wsapcfrnhdc5officeidname | SPOOK用運賃取得(OfficeID:NH-DC5)e参照先cm名 |  | △ | △ | AC |
| ^ | ^ | wsapcfrnhdc5officeidkey | SPOOK用運賃取得(OfficeID:NH-DC5)参照先cmキー |  | △ | △ | AC |
| ^ | ^ | soapfrontalbportname | SOAPフロントALBポート参照先cm名 |  | △ | △ | AC |
| ^ | ^ | soapfrontalbportkey | SOAPフロントALBポート参照先cmキー |  | △ | △ | AC |
| ^ | ^ | soapfrontalbnamename | SOAPフロントALB名参照先cm名 |  | △ | △ | AC |
| ^ | ^ | soapfrontalbnamekey | SOAPフロントALB名参照先cmキー |  | △ | △ | AC |
| ^ | es |  | ExternalSecret(es)参照 |  |  |  | AC |
| ^ | ^ | postgrespasswordname | DBパスワード参照先es名 |  | △ | △ | AC |
| ^ | ^ | postgrespasswordkey | DBパスワード参照先esキー |  | △ | △ | AC |
| ^ | ^ | redispasswordname | Redisパスワード参照先es名 |  | △ | △ | AC |
| ^ | ^ | redispasswordkey | Redisパスワード参照先esキー |  | △ | △ | AC |
| ^ | ^ | wsapcpmpasswordname | 帳票作成/メール作成用PNR取得パスワード参照先cm名 |  | △ | △ | AC |
| ^ | ^ | wsapcpmpasswordkey | 帳票作成/メール作成用PNR取得パスワード参照先cmキー |  | △ | △ | AC |
| ^ | ^ | wsapcivpasswordname | インベントリ情報取得パスワード参照先cm名 |  | △ | △ | AC |
| ^ | ^ | wsapcivpasswordkey | インベントリ情報取得パスワード参照先cmキー |  | △ | △ | AC |
| ^ | ^ | wsapcfrpasswordname | SPOOK用運賃取得パスワード参照先cm名 |  | △ | △ | AC |
| ^ | ^ | wsapcfrpasswordkey | SPOOK用運賃取得パスワード参照先cmキー |  | △ | △ | AC |
| addEnv |  |  | 追加環境変数 |  |  |  | AC |
| ^ | enabled |  | 追加環境変数を有効化する／しないをtrue/falseで定義 | false | △ | △ | AC |
| ^ | data |  | 追加環境変数のデータ |  | △ | △ | AC |
| refConfigMap |  |  | コンフィグマップ |  |  |  | AC |
| ^ | enabled |  | コンフィグマップを有効化する／しないをtrue/falseで定義 | false | △ | △ | AC |
| ^ | refer |  | 作成したコンフィグマップを環境変数に取り込む定義 |  | △ | △ | AC |
| refExternalSecret |  |  | ExternalSecret参照 |  |  |  | AC |
| ^ | enabled |  | ExternalSecret参照の有効化をtrue/falseで定義 | false | △ | △ | AC |
| ^ | refer |  | ExternalSecretを環境変数に取り込む定義 |  | △ | △ | AC |

※ `ap-values-[環境].yaml` に定義を行わなかった場合、表のデフォルト値が適用される。</BR>


### 4.4.1 基本属性
Podの基本属性として設定する項目を定義する。</BR>

#### 4.4.1.1 name(名前)
アプリケーション名を定義する。</BR>
- 共通項目定義のみ。</BR>
- 「コンテナコンポーネント一覧」より「コンテナID」欄の記載内容を定義する。すべて小文字とし、アンダーバーはハイフンに変更する。</BR>
  例：casp-a101001-01
- アプリケーション名はコンテナ名、Pod名としても使用される。</BR>

#### 4.4.1.2 apversion(アプリバージョン)
アプリケーションのバージョンを定義する。</BR>
- 本番/ステージング/保守開発環境で異なる定義を行いたい場合は、それぞれ定義する。</BR>
- バージョン名については、アプリケーション開発のガイドラインに従うものとする。</BR>
- 非常駐Podの場合、Pod起動シェルで実行単位の識別番号に置き換えられる。</BR>

#### 4.4.1.3 namespace(ネームスペース)
デプロイ先のネームスペースを定義する。</BR>
- 本番/ステージング/保守開発環境で異なる定義を行いたい場合は、それぞれ定義する。</BR>
- 非常駐Podの場合、JP1ジョブ定義のパラメータでデプロイ先のネームスペースを設定することで、本定義が上書きされる。</BR>
※JP1のジョブ定義については「ジョブ作成手順」を参照</BR>

#### 4.4.1.4 replicaCount(起動Pod数)
起動するPod数を定義する。</BR>
- 非常駐Podでは定義不可。</BR>
- 本番/ステージング/保守開発環境で異なる定義を行いたい場合は、それぞれ定義する。</BR>
- 本定義に合わせて、Pod数が維持される。</BR>
※Podに障害が発生しダウンした場合でも、EKSのオートヒーリング機能により本定義のPod数を維持するように起動数が確保される。</BR>

### 4.4.2 image(コンテナイメージ)
アプリケーションのコンテナイメージを定義する。</BR>

#### 4.4.2.1 repository(イメージリポジトリ)
起動するコンテナイメージが格納されているECRリポジトリのリポジトリ名を定義する。コンテナID配下を定義する。</BR>
例：casp-a101001_01

#### 4.4.2.2 tag(タグ)
起動するコンテナイメージのイメージタグ名を定義する。</BR>
アプリケーションソースコードのリポジトリでCIが動くと、ECRへのイメージプッシュ後にプッシュしたイメージタグ名で書き換えられ、PullRequestが発行される。</BR>
※PullRequestが発行された後のフローについては、「ブランクプロジェクト統廃合＆GitOps(マニフェスト分離) 移行ガイド」のGitOps移行後の作業フローを参照のこと。

### 4.4.3 ports(ポート)
コンテナで利用するポートを定義する。</BR>

#### 4.4.3.1 containerPort(コンテナの待ち受けポート番号)
コンテナの待ち受けるリッスンポート番号を定義する。</BR>

### 4.4.4 livebessProbe(Pod死活監視)
Podの死活監視の間隔(秒)と回数を定義する。</BR>
※MSテンプレートで設定するPod上の `/health/liveness` を定期的に監視するための定義。</BR>
　ここで定義した調査間隔(秒)×回数分の死活監視を行い、その間にPod非活性を検知した場合、Podの再起動を行う。</BR>

#### 4.4.4.1 failureThreshold(調査間隔(秒))
- 本番/ステージング/保守開発環境で異なる定義を行いたい場合は、それぞれ定義する。</BR>

#### 4.4.4.2 periodSeconds(調査回数)
- 本番/ステージング/保守開発環境で異なる定義を行いたい場合は、それぞれ定義する。</BR>

### 4.4.5 readinessProbe(Pod読み取り監視)
Podの読み取り監視の間隔(秒)と回数を定義する。</BR>
※MSテンプレートで設定するPod上の `/health/readiness` を定期的に監視するための定義。</BR>
　ここで定義した調査間隔(秒)×回数分のPod読み取り監視を行い、その間にPodの読み取りが不可であった場合、ALBにて当Podへの通信を閉塞し遮断する。</BR>

#### 4.4.5.1 failureThreshold(調査間隔(秒))
- 本番/ステージング/保守開発環境で異なる定義を行いたい場合は、それぞれ定義する。</BR>

#### 4.4.5.2 periodSeconds(調査回数)
- 本番/ステージング/保守開発環境で異なる定義を行いたい場合は、それぞれ定義する。</BR>

### 4.4.6 startupProbe(Pod読み取り監視開始遅延)
readinessProbeの読み取りを開始する遅延時間を定義する。</BR>
※Podが起動してから、調査間隔(秒)×回数分の遅延時間を待ったのち、readinessProbe(Pod読み取り監視)を開始する。</BR>
　Podが起動してから遅延時間までの間に読み取り不可だった場合にも、ALBによる通信閉塞は行わない。</BR>

#### 4.4.6.1 failureThreshold(調査間隔(秒))
- 本番/ステージング/保守開発環境で異なる定義を行いたい場合は、それぞれ定義する。</BR>

#### 4.4.6.2 periodSeconds(調査回数)
- 本番/ステージング/保守開発環境で異なる定義を行いたい場合は、それぞれ定義する。</BR>

### 4.4.7 ingress(Ingress定義)
クラスター内のServiceに対する外部からのアクセス(主にHTTP)を管理する。</BR>


#### 4.4.7.1 create(有効化フラグ)
Ingressを作成する場合に `true` を設定する。</BR>
- 「コンテナコンポーネント一覧」の「トリガー」列に「外部WebSVC」と記載のあるコンテナの場合のみ `true` とする。
- 「コンテナコンポーネント一覧」の「トリガー」列に「内部WebSVC」と記載のあるコンテナの場合は定義しない。

#### 4.4.7.2 path(パス)
Podへアクセスする際のパスを、「アプリAPI一覧」の「公開APIリソース」記載内容をもとに定義する。


#### 4.4.7.3 orderとpathtype(ALBの読み取り優先順位とパスタイプ)
ingressの定義は、AWS Load Balancer ControllerによってALBに自動的に反映される。
ALBでは、path(パス)へのREST通信を、Ingressに紐づくPodに振り分ける。</BR>
- pathtypeは基本的には「Prefix」を定義する。「Prefix」を指定すると、パスへのマッチング判定は前方一致で行われる。</BR>
- 例2に示すように、パスの一部が複数の宛先Podで重複する場合にpathtypeに「Exact」を定義し、かつorderも指定する

【order定義時の注意事項】</BR>
- orderは設定例2に示すようなパタンでのみ定義し、それ以外では一律「`""`」とする。
- orderはAWS上で文字列型として扱わせる制約があり、「`"'50'"`」のようにダブルコーテーションとシングルコーテーションで囲み、文字列型として記載する。

設定例
- 例1：Ingressにパスとして `/path1` を定義し、pathtypeを「Prefix」とした場合</BR>
    - `/path1` と `/path1/*` へのREST通信の両方がIngressに紐づくPodに振り分けられる。</BR>
      | path | pathtype | order | 宛先 |
      | - | - | - | - |
      | `/path1` | Prefix | - | Pod-A |
      | `/path1/*` ※未定義 | - | - | Pod-A |
- 例2：`/path1` へのREST通信と、`/path1/*` へのREST通信を異なるPodに振り分けたい場合</BR>
    - 階層の浅い方<sup>(※1)</sup>のpathtypeを「Exact」とし、階層の深い方<sup>(※2)</sup>のpathtypeを「Prefix」とする。
    - あわせて、(※1)のorderを(※2)のorderより大きな値とする。
      | path | pathtype | order | 宛先 |
      | - | - | - | - |
      | `/path1` <sup>(※1)</sup> | Exact | 20 | Pod-A |
      | `/path1/*` <sup>(※2)</sup> | Prefix | 10 | Pod-B |

### 4.4.8 resources(リソース)
CPUとメモリの制限(リソース制限)と要求(開始時の要求リソース)を定義する。</BR>

#### 4.4.8.1 limits(制限)
リソース制限を定義する。</BR>

##### 4.4.8.1.1 cpu(CPU)
- 本番/ステージング/保守開発環境で異なる定義を行いたい場合は、それぞれ定義する。</BR>
- 保守開発環境は、本番環境の10%のリソースとするため、 `0.25` をデフォルト定義とする。</BR>
- [0.25, 0.5, 1, 2, 4]のいずれかの数(個数)を定義する。</BR>

##### 4.4.8.1.2 memory(メモリ)
- 本番/ステージング/保守開発環境で異なる定義を行いたい場合は、それぞれ定義する。</BR>
- 保守開発環境は、本番環境の10%のリソースとするため、 `0.5G` をデフォルト定義とする。</BR>
- Fargate で実行されているPodで使用可能な vCPU とメモリの組み合わせには制限があるため、CPUの設定値に合わせて以下の組み合わせから選択すること。</BR>

  | vCPU 値 | メモリの値 |
  | - | - |
  | 0.25 | 0.5 GB、1 GB、2 GB |
  | 0.5 | 1 GB、2 GB、3 GB、4 GB |
  | 1 | 2 GB、3 GB、4 GB、5 GB、6 GB、7 GB、8 GB |
  | 2 | 4 GB ～ 16 GB (1 GB のインクリメント) |
  | 4 | 8 GB ～ 30 GB (1 GB のインクリメント) |

#### 4.4.8.2 requests(要求)
開始時の要求リソースを定義する。</BR>

##### 4.4.8.2.1 cpu(CPU)
- 本番/ステージング/保守開発環境で異なる定義を行いたい場合は、それぞれ定義する。</BR>
- 保守開発環境は、本番環境の10%のリソースとするため、 `0.25` をデフォルト定義とする。</BR>
- [0.25, 0.5, 1, 2, 4]のいずれかの数(個数)を定義する。</BR>

##### 4.4.8.2.2 memory(メモリ)
- 本番/ステージング/保守開発環境で異なる定義を行いたい場合は、それぞれ定義する。</BR>
- 保守開発環境は、本番環境の10%のリソースとするため、`0.5G` をデフォルト定義とする。</BR>
- Fargate で実行されているPodで使用可能な vCPU とメモリの組み合わせには制限があるため、CPUの設定値に合わせて「[4.4.8.1.2 memory(メモリ)](#44812-memoryメモリ)」の表の組み合わせから選択すること。</BR>

### 4.4.9 env(環境変数)
Spring Bootで起動するjavaアプリケーションの起動時に読み込まれる環境変数を定義する。</BR>

#### 4.4.9.1 emptydirvalue(emptyDir)
- アプリケーション処理で利用するemptyDirが必要な場合に定義する。</BR>


#### 4.4.9.2 snstopicvalue(SNSトピック名)
- アプリケーション処理で利用するSNSトピック名を「アプリ公開インフラパラメータ」のSMDI_CAS_APL__機能間連携台帳を元に定義する。</BR>
- 複数のSNSトピックを使用する場合は、snstopicvalue2 ～ snstopicvalue5を使用して、必要な定義の分だけ値を設定する。</BR>
- 定義した値は、コンテナ内の環境変数`SNS_TOPIC_NAME`および`SNS_TOPIC_NAME_2～SNS_TOPIC_NAME_5`に反映される。

#### 4.4.9.3 sqsqueuevalue(SQSキュー名)
- アプリケーション処理で利用するSQSキュー名を「アプリ公開インフラパラメータ」のSMDI_CAS_APL__機能間連携台帳を元に定義する。</BR>
- 複数のSQSキューを利用する場合は、sqsqueuevalue2 ～ sqsqueuevalue5を使用して、必要な定義の分だけ値を設定する。</BR>
- 定義した値は、コンテナ内の環境変数`SQS_QUEUE_NAME`および`SQS_QUEUE_NAME_2～SQS_QUEUE_NAME_5`に反映される。

#### 4.4.9.4 amqtopicname(ActiveMQトピック名)
- アプリケーション処理で利用するActiveMQトピック名を「アプリ公開インフラパラメータ」のSMDI_CAS_APL__機能間連携台帳を元に定義する。</BR>
- 複数のSNSトピックを使用する場合は、amqtopicname2 ～ amqtopicname5を使用して、必要な定義の分だけ値を設定する。</BR>
- 定義した値は、コンテナ内の環境変数`AMQ_TOPIC_NAME`および`AMQ_TOPIC_NAME_2～AMQ_TOPIC_NAME_5`に反映される。

#### 4.4.9.5 amqqueuename(ActiveMQキュー名)
- アプリケーション処理で利用するActiveMQキュー名を「アプリ公開インフラパラメータ」SMDI_CAS_APL__機能間連携台帳を元に定義する。</BR>
- 複数のSQSキューを利用する場合は、amqqueuename2 ～ amqqueuename5を使用して、必要な定義の分だけ値を設定する。</BR>
- 定義した値は、コンテナ内の環境変数`AMQ_QUEUE_NAME`および`AMQ_QUEUE_NAME_2～AMQ_QUEUE_NAME_5`に反映される。

#### 4.4.9.6 amqbrokerurl(ActiveMQブローカーURL)
- アプリケーション処理で利用するActiveMQブローカーURLを「アプリ公開インフラパラメータ」SMDI_CAS_APL__機能間連携台帳を元に定義する。</BR>
- 複数のActiveMQを使用する場合は、amqbrokerurl2 ～ amqbrokerurl5を使用して、必要な定義の分だけ値を設定する。</BR>
- 定義した値は、コンテナ内の環境変数`AMQ_BROKER_URL`および`AMQ_BROKER_URL_2～AMQ_BROKER_URL_5`に反映される。

#### 4.4.9.7 cm(コンフィグマップ)
DB接続などに使用する値の参照先コンフィグマップ名とキーを定義する。
定義した値は、下表のとおりコンテナ内の環境変数の値に反映される。

| コンフィグマップ名 | コンフィグマップキー | 環境変数 | 参照 |
| - | - | - | - |
| s3bucketname[2-5] | s3bucketkey[2-5] | S3_BUCKET[_2-_5] | 4.4.9.7.1, 4.4.9.7.2|
| postgreshostname[2-5] | postgreshostkey[2-5] | POSTGRES_HOST[_2-_5] | 4.4.9.7.3, 4.4.9.7.4  |
| postgresportname[2-5] | postgresportkey[2-5] | POSTGRES_PORT[_2-_5] | 4.4.9.7.5, 4.4.9.7.6 |
| postgresdatabasename[2-5] | postgresdatabasekey[2-5] | POSTGRES_DATABASE[_2-_5] | 4.4.9.7.7, 4.4.9.7.8 |
| postgresusername[2-5] | postgresuserkey[2-5] | POSTGRES_USER[_2-_5] | 4.4.9.7.9, 4.4.9.7.10 |
| redishostname[2-5] | redishostkey[2-5] | REDIS_HOST[_2-_5] | 4.4.9.7.11, 4.4.9.7.12 |
| redisportname[2-5] | redisportkey[2-5] | REDIS_PORT[_2-_5] | 4.4.9.7.13, 4.4.9.7.14 |
| redisclustername[2-5] | redisclusterkey[2-5] | REDIS_CLUSTER[_2-_5] | 4.4.9.7.15, 4.4.9.7.16 |
| redisnumbername[2-5] | redisnumberkey[2-5] | REDIS_NUMBER[_2-_5] | 4.4.9.7.17, 4.4.9.7.18 |
| redisusername[2-5] | redisuserkey[2-5] | REDIS_USER[_2-_5] | 4.4.9.7.19, 4.4.9.7.20 |
| dynamodbtablename[2-5] | dynamodbtablekey[2-5] | DYNAMODB_TABLE[_2-_5] | 4.4.9.7.21, 4.4.9.7.22 |
| wsapcpmuseridname | wsapcpmuseridkey | WSAP_CPM_USER_ID | 4.4.9.7.23, 4.4.9.7.24 |
| wsapcpmagentdutycodename | wsapcpmagentdutycodekey | WSAP_CPM_AGENT_DUTY_CODE | 4.4.9.7.25, 4.4.9.7.26 |
| wsapcpmpostypename | wsapcpmpostypekey | WSAP_CPM_POS_TYPE | 4.4.9.7.27, 4.4.9.7.28 |
| wsapcpmrequestortypename | wsapcpmrequestortypekey | WSAP_CPM_REQUESTOR_TYPE | 4.4.9.7.29, 4.4.9.7.30 |
| wsapcpmnhofficeidname | wsapcpmnhofficeidkey | WSAP_CPM_NH_OFFICE_ID | 4.4.9.7.31, 4.4.9.7.32 |
| wsapcpmhdofficeidname | wsapcpmhdofficeidkey | WSAP_CPM_HD_OFFICE_ID | 4.4.9.7.33, 4.4.9.7.34 |
| wsapcpmfwofficeidname | wsapcpmfwofficeidkey | WSAP_CPM_FW_OFFICE_ID | 4.4.9.7.35, 4.4.9.7.36 |
| wsapcpm7gofficeidname | wsapcpm7gofficeidkey | WSAP_CPM_7G_OFFICE_ID | 4.4.9.7.37, 4.4.9.7.38 |
| wsapcpmocofficeidname | wsapcpmocofficeidkey | WSAP_CPM_OC_OFFICE_ID | 4.4.9.7.39, 4.4.9.7.40 |
| wsapcpm6jofficeidname | wsapcpm6jofficeidkey | WSAP_CPM_6J_OFFICE_ID | 4.4.9.7.41, 4.4.9.7.42 |
| wsapcivuseridname | wsapcivuseridkey | WSAP_CIV_USER_ID | 4.4.9.7.43, 4.4.9.7.44 |
| wsapcivagentdutycodename | wsapcivagentdutycodekey | WSAP_CIV_AGENT_DUTY_CODE | 4.4.9.7.45, 4.4.9.7.46 |
| wsapcivpostypename | wsapcivpostypekey | WSAP_CIV_POS_TYPE | 4.4.9.7.47, 4.4.9.7.48 |
| wsapcivrequestortypename | wsapcivrequestortypekey | WSAP_CIV_REQUESTOR_TYPE | 4.4.9.7.49, 4.4.9.7.50 |
| wsapcivnhofficeidname | wsapcivnhofficeidkey | WSAP_CIV_NH_OFFICE_ID | 4.4.9.7.51, 4.4.9.7.52 |
| wsapcivhdofficeidname | wsapcivhdofficeidkey | WSAP_CIV_HD_OFFICE_ID | 4.4.9.7.53, 4.4.9.7.54 |
| wsapcivfwofficeidname | wsapcivfwofficeidkey | WSAP_CIV_FW_OFFICE_ID | 4.4.9.7.55, 4.4.9.7.56 |
| wsapciv7gofficeidname | wsapciv7gofficeidkey | WSAP_CIV_7G_OFFICE_ID | 4.4.9.7.57, 4.4.9.7.58 |
| wsapcivocofficeidname | wsapcivocofficeidkey | WSAP_CIV_OC_OFFICE_ID | 4.4.9.7.59, 4.4.9.7.60 |
| wsapciv6jofficeidname | wsapciv6jofficeidkey | WSAP_CIV_6J_OFFICE_ID | 4.4.9.7.61, 4.4.9.7.62 |
| wsapcfruseridname | wsapcfruseridkey | WSAP_CFR_USER_ID | 4.4.9.7.63, 4.4.9.7.64 |
| wsapcfragentdutycodename | wsapcfragentdutycodekey | WSAP_CFR_AGENT_DUTY_CODE | 4.4.9.7.65, 4.4.9.7.66 |
| wsapcfrpostypename | wsapcfrpostypekey | WSAP_CFR_POS_TYPE | 4.4.9.7.67, 4.4.9.7.68 |
| wsapcfrrequestortypename | wsapcfrrequestortypekey | WSAP_CFR_REQUESTOR_TYPE | 4.4.9.7.69, 4.4.9.7.70 |
| wsapcfrnhdc1officeidname | wsapcfrnhdc1officeidkey | WSAP_CFR_NHDC1_OFFICE_ID | 4.4.9.7.71, 4.4.9.7.72 |
| wsapcfrnhdc2officeidname | wsapcfrnhdc2officeidkey | WSAP_CFR_NHDC2_OFFICE_ID | 4.4.9.7.73, 4.4.9.7.74 |
| wsapcfrnhdc3officeidname | wsapcfrnhdc3officeidkey | WSAP_CFR_NHDC3_OFFICE_ID | 4.4.9.7.75, 4.4.9.7.76 |
| wsapcfrnhdc4officeidname | wsapcfrnhdc4officeidkey | WSAP_CFR_NHDC4_OFFICE_ID | 4.4.9.7.77, 4.4.9.7.78 |
| wsapcfrnhdc5officeidname | wsapcfrnhdc5officeidkey | WSAP_CFR_NHDC5_OFFICE_ID | 4.4.9.7.79, 4.4.9.7.80 |
| soapfrontalbportname | soapfrontalbportkey | SOAPFRONT_ALB_PORT | 4.4.9.7.81, 4.4.9.7.82 |
| soapfrontalbnamename | soapfrontalbnamekey | SOAPFRONT_ALB_NAME | 4.4.9.7.83, 4.4.9.7.84 |


コンフィグマップ名とキーは「アプリ公開用インフラパラメータ」の以下資料の「name」「Key」を適宜参照</BR>
　
> 資料：SMDI_CAS_INF_アプリ公開用インフラパラメータ（Aurora編）</BR>
　　　SMDI_CAS_INF_アプリ公開用インフラパラメータ（DynamoDB編）</BR>
　　　SMDI_CAS_INF_アプリ公開用インフラパラメータ（Redis編）</BR>
　　　SMDI_CAS_INF_アプリ公開用インフラパラメータ（S3編）</BR>
　　　SMDI_CAS_INF_アプリ公開用インフラパラメータ（WSAP編）</BR>
　　　【XXXX環境】AWSリソース一覧(※)</BR>
　　　　(※)AWSリソース一覧は、XXXXを保守開発／ステージング／本番環境で読み替える

##### 4.4.9.7.1 s3bucketname(S3バケット参照先cm)
S3バケット参照先のコンフィグマップ名を定義する。</BR>
- 複数のS3バケットに接続する場合は、s3bucketname2 ～ s3bucketname5を使用して、必要な定義の分だけ値を設定する。</BR>

##### 4.4.9.7.2 s3bucketkey(S3バケット参照先cmキー)
S3バケット参照先のコンフィグマップキーを定義する。</BR>
- 複数のS3バケットに接続する場合は、s3bucketkey2 ～ s3bucketkey5を使用して、必要な定義の分だけ値を設定する。</BR>

##### 4.4.9.7.3 postgreshostname(DBホスト名参照先cm)
DBホスト名参照先のコンフィグマップ名を定義する。</BR>
- 複数のDBに接続する場合は、postgreshostname2 ～ postgreshostname5を使用して、必要な定義の分だけ値を設定する。</BR>

##### 4.4.9.7.4 postgreshostkey(DBホスト名参照先cmキー)
DBホスト名参照先のコンフィグマップキーを定義する。</BR>
- 複数のDBに接続する場合は、postgreshostkey2 ～ postgreshostkey5を使用して、必要な定義の分だけ値を設定する。</BR>

##### 4.4.9.7.5 postgresportname(DBポート番号参照先cm)
DBポート番号参照先のコンフィグマップ名を定義する。</BR>
- 複数のDBに接続する場合は、postgresportname2 ～ postgresportname5を使用して、必要な定義の分だけ値を設定する。</BR>

##### 4.4.9.7.6 postgresportkey(DBポート番号参照先cmキー)
DBポート番号参照先のコンフィグマップキーを定義する。</BR>
- 複数のDBに接続する場合は、postgresportkey2 ～ postgresportkey5を使用して、必要な定義の分だけ値を設定する。</BR>

##### 4.4.9.7.7 postgresdatabasename(DB名参照先cm)
DB名参照先のコンフィグマップ名を定義する。</BR>
- 複数のDBに接続する場合は、postgresdatabasename2 ～ postgresdatabasename5を使用して、必要な定義の分だけ値を設定する。</BR>

##### 4.4.9.7.8 postgresdatabasekey(DB名参照先cmキー)
DB名参照先のコンフィグマップキーを定義する。</BR>
- 複数のDBに接続する場合は、postgresdatabasekey2 ～ postgresdatabasekey5を使用して、必要な定義の分だけ値を設定する。</BR>

##### 4.4.9.7.9 postgresusername(DBユーザ名参照先cm)
DBユーザ名参照先のコンフィグマップ名を定義する。</BR>
- 複数のDBに接続する場合は、postgresusername2 ～ postgresusername5を使用して、必要な定義の分だけ値を設定する。</BR>

##### 4.4.9.7.10 postgresuserkey(DBユーザ名参照先cmキー)
DBユーザ名参照先のコンフィグマップキーを定義する。</BR>
- 複数のDBに接続する場合は、postgresuserkey2 ～ postgresuserkey5を使用して、必要な定義の分だけ値を設定する。</BR>

##### 4.4.9.7.11 redishostname(Redisホスト名参照先cm)
Redisホスト名参照先のコンフィグマップ名を定義する。</BR>
- 複数のRedisに接続する場合は、redishostname2 ～ redishostname5を使用して、必要な定義の分だけ値を設定する。</BR>

##### 4.4.9.7.12 redishostkey(Redisホスト名参照先cmキー)
Redisホスト名参照先のコンフィグマップキーを定義する。</BR>
- 複数のRedisに接続する場合は、redishostkey2 ～ redishostkey5を使用して、必要な定義の分だけ値を設定する。</BR>

##### 4.4.9.7.13 redisportname(Redisポート番号参照先cm)
Redisポート番号参照先のコンフィグマップ名を定義する。</BR>
- 複数のRedisに接続する場合は、redisportname2 ～ redisportname5を使用して、必要な定義の分だけ値を設定する。</BR>

##### 4.4.9.7.14 redisportkey(Redisポート番号参照先cmキー)
Redisポート番号参照先のコンフィグマップキーを定義する。</BR>
- 複数のRedisに接続する場合は、redisportkey2 ～ redisportkey5を使用して、必要な定義の分だけ値を設定する。</BR>

##### 4.4.9.7.15 redisclustername(Redisクラスター名参照先cm)
Redisクラスター名参照先のコンフィグマップ名を定義する。</BR>
- 複数のRedisに接続する場合は、redisclustername2 ～ redisclustername5を使用して、必要な定義の分だけ値を設定する。</BR>

##### 4.4.9.7.16 redisclusterkey(Redisクラスター名参照先cmキー)
Redisクラスター名参照先のコンフィグマップキーを定義する。</BR>
- 複数のRedisに接続する場合は、redisclusterkey2 ～ redisclusterkey5を使用して、必要な定義の分だけ値を設定する。</BR>

##### 4.4.9.7.17 redisnumbername(RedisDB番号参照先cm)
RedisのDB番号参照先のコンフィグマップ名を定義する。</BR>
- 複数のRedisに接続する場合は、redisnumbername2 ～ redisnumbername5を使用して、必要な定義の分だけ値を設定する。</BR>

##### 4.4.9.7.18 redisnumberkey(RedisDB番号参照先cmキー)
RedisのDB番号参照先のコンフィグマップキーを定義する。</BR>
- 複数のRedisに接続する場合は、redisnumberkey2 ～ redisnumberkey5を使用して、必要な定義の分だけ値を設定する。</BR>

##### 4.4.9.7.19 redisusername(Redisユーザ名参照先cm)
Redisユーザ名参照先のコンフィグマップ名を定義する。</BR>
- 複数のRedisに接続する場合は、redisusername2 ～ redisusername5を使用して、必要な定義の分だけ値を設定する。</BR>

##### 4.4.9.7.20 redisuserkey(Redisユーザ名参照先cmキー)
Redisユーザ名参照先のコンフィグマップキーを定義する。</BR>
- 複数のRedisに接続する場合は、redisuserkey2 ～ redisuserkey5を使用して、必要な定義の分だけ値を設定する。</BR>

##### 4.4.9.7.21 dynamodbtablename(Dynamoテーブル名参照先cm)
DynamoDBテーブル名参照先のコンフィグマップ名を定義する。</BR>
- 複数のDBに接続する場合は、dynamodbtablename2 ～ dynamodbtablename5を使用して、必要な定義の分だけ値を設定する。</BR>

##### 4.4.9.7.22 dynamodbtablekey(Dynamoテーブル名参照先cmキー)
DynamoDBテーブル名参照先のコンフィグマップキーを定義する。</BR>
- 複数のDBに接続する場合は、dynamodbtablekey2 ～ dynamodbtablekey5を使用して、必要な定義の分だけ値を設定する。</BR>

##### 4.4.9.7.23 wsapcpmuseridname(WSAP_CPM_USERID参照先cm)
帳票作成/メール作成用PNR取得ユーザID参照先のコンフィグマップ名を定義する。</BR>

##### 4.4.9.7.24 wsapcpmuseridkey(WSAP_CPM_USERID参照先cmキー)
帳票作成/メール作成用PNR取得ユーザID参照先のコンフィグマップキーを定義する。</BR>

##### 4.4.9.7.25 wsapcpmagentdutycodename(WSAP_CPM_AGENT_DUTY_CODE参照先cm)
帳票作成/メール作成用PNR取得等AgentDutyCode参照先のコンフィグマップ名を定義する。</BR>

##### 4.4.9.7.26 wsapcpmagentdutycodekey(WSAP_CPM_AGENT_DUTY_CODE参照先cmキー)
帳票作成/メール作成用PNR取得等AgentDutyCode参照先のコンフィグマップキーを定義する。</BR>

##### 4.4.9.7.27 wsapcpmpostypename(WSAP_CPM_POS_TYPE参照先cm)
帳票作成/メール作成用PNR取得等POS Type参照先のコンフィグマップ名を定義する。</BR>

##### 4.4.9.7.28 wsapcpmpostypekey(WSAP_CPM_POS_TYPE参照先cmキー)
帳票作成/メール作成用PNR取得等POS Type参照先のコンフィグマップキーを定義する。</BR>

##### 4.4.9.7.29 wsapcpmrequestortypename(WSAP_CPM_REQUESTOR_TYPE参照先cm)
帳票作成/メール作成用PNR取得等RequestorType参照先のコンフィグマップ名を定義する。</BR>

##### 4.4.9.7.30 wsapcpmrequestortypekey(WSAP_CPM_REQUESTOR_TYPE参照先cmキー)
帳票作成/メール作成用PNR取得等RequestorType参照先のコンフィグマップキーを定義する。</BR>

##### 4.4.9.7.31 wsapcpmnhofficeidname(WSAP_CPM_NH_OFFICE_ID参照先cm)
帳票作成/メール作成用PNR取得等(OfficeID:NH)参照先のコンフィグマップ名を定義する。</BR>

##### 4.4.9.7.32 wsapcpmnhofficeidkey(WSAP_CPM_NH_OFFICE_ID参照先cmキー)
帳票作成/メール作成用PNR取得等(OfficeID:NH)参照先のコンフィグマップキーを定義する。</BR>

##### 4.4.9.7.33 wsapcpmhdofficeidname(WSAP_CPM_HD_OFFICE_ID参照先cm)
帳票作成/メール作成用PNR取得等(OfficeID:HD)参照先のコンフィグマップ名を定義する。</BR>

##### 4.4.9.7.34 wsapcpmhdofficeidkey(WSAP_CPM_HD_OFFICE_ID参照先cmキー)
帳票作成/メール作成用PNR取得等(OfficeID:HD)参照先のコンフィグマップキーを定義する。</BR>

##### 4.4.9.7.35 wsapcpmfwofficeidname(WSAP_CPM_FW_OFFICE_ID参照先cm)
帳票作成/メール作成用PNR取得等(OfficeID:FW)参照先のコンフィグマップ名を定義する。</BR>

##### 4.4.9.7.36 wsapcpmfwofficeidkey(WSAP_CPM_FW_OFFICE_ID参照先cmキー)
帳票作成/メール作成用PNR取得等(OfficeID:FW)参照先のコンフィグマップキーを定義する。</BR>

##### 4.4.9.7.37 wsapcpm7gofficeidname(WSAP_CPM_7G_OFFICE_ID参照先cm)
帳票作成/メール作成用PNR取得等(OfficeID:7G)参照先のコンフィグマップ名を定義する。</BR>

##### 4.4.9.7.38 wsapcpm7gofficeidkey(WSAP_CPM_7G_OFFICE_ID参照先cmキー)
帳票作成/メール作成用PNR取得等(OfficeID:7G)参照先のコンフィグマップキーを定義する。</BR>

##### 4.4.9.7.39 wsapcpmocofficeidname(WSAP_CPM_OC_OFFICE_ID参照先cm)
帳票作成/メール作成用PNR取得等(OfficeID:OC)参照先のコンフィグマップ名を定義する。</BR>

##### 4.4.9.7.40 wsapcpmocofficeidkey(WSAP_CPM_OC_OFFICE_ID参照先cmキー)
帳票作成/メール作成用PNR取得等(OfficeID:OC)参照先のコンフィグマップキーを定義する。</BR>

##### 4.4.9.7.41 wsapcpm6jofficeidname(WSAP_CPM_6J_OFFICE_ID参照先cm)
帳票作成/メール作成用PNR取得等(OfficeID:6J)参照先のコンフィグマップ名を定義する。</BR>

##### 4.4.9.7.42 wsapcpm6jofficeidkey(WSAP_CPM_6J_OFFICE_ID参照先cmキー)
帳票作成/メール作成用PNR取得等(OfficeID:6J)参照先のコンフィグマップキーを定義する。</BR>

##### 4.4.9.7.43 wsapcivuseridname(WSAP_CIV_USER_ID参照先cm)
インベントリ情報取得ユーザID参照先のコンフィグマップ名を定義する。</BR>

##### 4.4.9.7.44 wsapcivuseridkey(WSAP_CIV_USER_ID参照先cmキー)
インベントリ情報取得ユーザID参照先のコンフィグマップキーを定義する。</BR>

##### 4.4.9.7.45 wsapcivagentdutycodename(WSAP_CIV_AGENT_DUTY_CODE参照先cm)
インベントリ情報取得AgentDutyCode参照先のコンフィグマップ名を定義する。</BR>

##### 4.4.9.7.46 wsapcivagentdutycodekey(WSAP_CIV_AGENT_DUTY_CODE参照先cmキー)
インベントリ情報取得AgentDutyCode参照先のコンフィグマップキーを定義する。</BR>

##### 4.4.9.7.47 wsapcivpostypename(WSAP_CIV_POS_TYPE参照先cm)
インベントリ情報取得POS Type参照先のコンフィグマップ名を定義する。</BR>

##### 4.4.9.7.48 wsapcivpostypekey(WSAP_CIV_POS_TYPE参照先cmキー)
インベントリ情報取得POS Type参照先のコンフィグマップキーを定義する。</BR>

##### 4.4.9.7.49 wsapcivrequestortypename(WSAP_CIV_REQUESTOR_TYPE参照先cm)
インベントリ情報取得RequestorType参照先のコンフィグマップ名を定義する。</BR>

##### 4.4.9.7.50 wsapcivrequestortypekey(WSAP_CIV_REQUESTOR_TYPE参照先cmキー)
インベントリ情報取得RequestorType参照先のコンフィグマップキーを定義する。</BR>

##### 4.4.9.7.51 wsapcivnhofficeidname(WSAP_CIV_NH_OFFICE_ID参照先cm)
インベントリ情報取得(OfficeID:NH)参照先のコンフィグマップ名を定義する。</BR>

##### 4.4.9.7.52 wsapcivnhofficeidkey(WSAP_CIV_NH_OFFICE_ID参照先cmキー)
インベントリ情報取得(OfficeID:NH)参照先のコンフィグマップキーを定義する。</BR>

##### 4.4.9.7.53 wsapcivhdofficeidname(WSAP_CIV_HD_OFFICE_ID参照先cm)
インベントリ情報取得(OfficeID:HD)参照先のコンフィグマップ名を定義する。</BR>

##### 4.4.9.7.54 wsapcivhdofficeidkey(WSAP_CIV_HD_OFFICE_ID参照先cmキー)
インベントリ情報取得(OfficeID:HD)参照先のコンフィグマップキーを定義する。</BR>

##### 4.4.9.7.55 wsapcivfwofficeidname(WSAP_CIV_FW_OFFICE_ID参照先cm)
インベントリ情報取得(OfficeID:FW)参照先のコンフィグマップ名を定義する。</BR>

##### 4.4.9.7.56 wsapcivfwofficeidkey(WSAP_CIV_FW_OFFICE_ID参照先cmキー)
インベントリ情報取得(OfficeID:FW)参照先のコンフィグマップキーを定義する。</BR>

##### 4.4.9.7.57 wsapciv7gofficeidname(WSAP_CIV_7G_OFFICE_ID参照先cm)
インベントリ情報取得(OfficeID:7G)参照先のコンフィグマップ名を定義する。</BR>

##### 4.4.9.7.58 wsapciv7gofficeidkey(WSAP_CIV_7G_OFFICE_ID参照先cmキー)
インベントリ情報取得(OfficeID:7G)参照先のコンフィグマップキーを定義する。</BR>

##### 4.4.9.7.59 wsapcivocofficeidname(WSAP_CIV_OC_OFFICE_ID参照先cm)
インベントリ情報取得(OfficeID:OC)参照先のコンフィグマップ名を定義する。</BR>

##### 4.4.9.7.60 wsapcivocofficeidkey(WSAP_CIV_OC_OFFICE_ID参照先cmキー)
インベントリ情報取得(OfficeID:OC)参照先のコンフィグマップキーを定義する。</BR>

##### 4.4.9.7.61 wsapciv6jofficeidname(WSAP_CIV_6J_OFFICE_ID参照先cm)
インベントリ情報取得(OfficeID:6J)参照先のコンフィグマップ名を定義する。</BR>

##### 4.4.9.7.62 wsapciv6jofficeidkey(WSAP_CIV_6J_OFFICE_ID参照先cmキー)
インベントリ情報取得(OfficeID:6J)参照先のコンフィグマップキーを定義する。</BR>

##### 4.4.9.7.63 wsapcfruseridname(WSAP_CFR_USER_ID参照先cm)
SPOOK用運賃取得ユーザID参照先のコンフィグマップ名を定義する。</BR>

##### 4.4.9.7.64 wsapcfruseridkey(WSAP_CFR_USER_ID参照先cmキー)
SPOOK用運賃取得ユーザID参照先のコンフィグマップキーを定義する。</BR>

##### 4.4.9.7.65 wsapcfragentdutycodename(WSAP_CFR_AGENT_DUTY_CODE参照先cm)
SPOOK用運賃取得AgentDutyCode参照先のコンフィグマップ名を定義する。</BR>

##### 4.4.9.7.66 wsapcfragentdutycodekey(WSAP_CFR_AGENT_DUTY_CODE参照先cmキー)
SPOOK用運賃取得AgentDutyCode参照先のコンフィグマップキーを定義する。</BR>

##### 4.4.9.7.67 wsapcfrpostypename(WSAP_CFR_POS_TYPE参照先cm)
SPOOK用運賃取得POS Type参照先のコンフィグマップ名を定義する。</BR>

##### 4.4.9.7.68 wsapcfrpostypekey(WSAP_CFR_POS_TYPE参照先cmキー)
SPOOK用運賃取得POS Type参照先のコンフィグマップキーを定義する。</BR>

##### 4.4.9.7.69 wsapcfrrequestortypename(WSAP_CFR_REQUESTOR_TYPE参照先cm)
SPOOK用運賃取得RequestorType参照先のコンフィグマップ名を定義する。</BR>

##### 4.4.9.7.70 wsapcfrrequestortypekey(WSAP_CFR_REQUESTOR_TYPE参照先cmキー)
SPOOK用運賃取得RequestorType参照先のコンフィグマップキーを定義する。</BR>

##### 4.4.9.7.71 wsapcfrnhdc1officeidname(WSAP_CFR_NHDC1_OFFICE_ID参照先cm)
SPOOK用運賃取得(OfficeID:NH-DC1)e参照先のコンフィグマップ名を定義する。</BR>

##### 4.4.9.7.72 wsapcfrnhdc1officeidkey(WSAP_CFR_NHDC1_OFFICE_ID参照先cmキー)
SPOOK用運賃取得(OfficeID:NH-DC1)e参照先のコンフィグマップキーを定義する。</BR>

##### 4.4.9.7.73 wsapcfrnhdc2officeidname(WSAP_CFR_NHDC2_OFFICE_ID参照先cm)
SPOOK用運賃取得(OfficeID:NH-DC2)e参照先のコンフィグマップ名を定義する。</BR>

##### 4.4.9.7.74 wsapcfrnhdc2officeidkey(WSAP_CFR_NHDC2_OFFICE_ID参照先cmキー)
SPOOK用運賃取得(OfficeID:NH-DC2)e参照先のコンフィグマップキーを定義する。</BR>

##### 4.4.9.7.75 wsapcfrnhdc3officeidname(WSAP_CFR_NHDC3_OFFICE_ID参照先cm)
SPOOK用運賃取得(OfficeID:NH-DC3)e参照先のコンフィグマップ名を定義する。</BR>

##### 4.4.9.7.76 wsapcfrnhdc3officeidkey(WSAP_CFR_NHDC3_OFFICE_ID参照先cmキー)
SPOOK用運賃取得(OfficeID:NH-DC3)e参照先のコンフィグマップキーを定義する。</BR>

##### 4.4.9.7.77 wsapcfrnhdc4officeidname(WSAP_CFR_NHDC4_OFFICE_ID参照先cm)
SPOOK用運賃取得(OfficeID:NH-DC4)e参照先のコンフィグマップ名を定義する。</BR>

##### 4.4.9.7.78 wsapcfrnhdc4officeidkey(WSAP_CFR_NHDC4_OFFICE_ID参照先cmキー)
SPOOK用運賃取得(OfficeID:NH-DC4)e参照先のコンフィグマップキーを定義する。</BR>

##### 4.4.9.7.79 wsapcfrnhdc5officeidname(WSAP_CFR_NHDC5_OFFICE_ID参照先cm)
SPOOK用運賃取得(OfficeID:NH-DC5)e参照先のコンフィグマップ名を定義する。</BR>

##### 4.4.9.7.80 wsapcfrnhdc5officeidkey(WSAP_CFR_NHDC5_OFFICE_ID参照先cmキー)
SPOOK用運賃取得(OfficeID:NH-DC5)e参照先のコンフィグマップキーを定義する。</BR>

##### 4.4.9.7.81 soapfrontalbportname(SOAPフロントALBポート参照先cm)
SOAPフロントALBポート参照先のコンフィグマップ名を定義する。</BR>

##### 4.4.9.7.82 soapfrontalbportkey(SOAPフロントALBポート参照先cmキー)
SOAPフロントALBポート参照先のコンフィグマップキーを定義する。</BR>

##### 4.4.9.7.83 soapfrontalbnamename(SOAPフロントALB名参照先cm)
SOAPフロントALB名参照先のコンフィグマップ名を定義する。</BR>

##### 4.4.9.7.84 soapfrontalbnamekey(SOAPフロントALB名参照先cmキー)
SOAPフロントALB名参照先のコンフィグマップキーを定義する。</BR>

#### 4.4.9.8 es(ExternalSecret)
DB接続などに使用する値の参照先ExternalSecret名とキーを定義する。
定義した値は、下表のとおりコンテナ内の環境変数の値に反映される。

| ExternalSecret名 | ExternalSecretキー | 環境変数 | 参照 |
| - | - | - | - |
| postgrespasswordname[2-5] | postgrespasswordkey[2-5] | POSTGRES_PASSWORD[_2-_5] | 4.4.9.8.1, 4.4.9.8.2 |
| redispasswordname[2-5] | redispasswordkey[2-5] | REDIS_PASSWORD[_2-_5] | 4.4.9.8.3, 4.4.9.8.4 |
| wsapcpmpasswordname | wsapcpmpasswordkey | WSAP_CPM_PASSWORD | 4.4.9.8.5, 4.4.9.8.6 |
| wsapcivpasswordname | wsapcivpasswordkey | WSAP_CIV_PASSWORD | 4.4.9.8.7, 4.4.9.8.8 |
| wsapcfrpasswordname | wsapcfrpasswordkey | WSAP_CFR_PASSWORD | 4.4.9.8.9, 4.4.9.8.10 |

ExternalSecret名とキーは「アプリ公開用インフラパラメータ」の以下資料の「name」「Key」を適宜参照</BR>

> 資料：SMDI_CAS_INF_アプリ公開用インフラパラメータ（Aurora編）</BR>
　　　SMDI_CAS_INF_アプリ公開用インフラパラメータ（Redis編）</BR>
　　　SMDI_CAS_INF_アプリ公開用インフラパラメータ（WSAP編）

##### 4.4.9.8.1 postgrespasswordname(DBパスワード参照先es)
DBパスワード参照先のExternalSecret名を定義する。</BR>
- 複数のDBに接続する場合は、postgrespasswordname2 ～ postgrespasswordname5を使用して、必要な定義の分だけ値を設定する。</BR>

##### 4.4.9.8.2 postgrespasswordkey(DBパスワード参照先esキー)
DBパスワード参照先のExternalSecretキーを定義する。</BR>
- 複数のDBに接続する場合は、postgrespasswordkey2 ～ postgrespasswordkey5を使用して、必要な定義の分だけ値を設定する。</BR>

##### 4.4.9.8.3 redispasswordname(Redisパスワード参照先es)
Redisパスワード参照先のExternalSecret名を定義する。</BR>
- 複数のRedisに接続する場合は、redispasswordname2 ～ redispasswordname5を使用して、必要な定義の分だけ値を設定する。</BR>

##### 4.4.9.8.4 redispasswordkey(Redisパスワード参照先esキー)
Redisパスワード参照先のExternalSecretキーを定義する。</BR>
- 複数のRedisに接続する場合は、redispasswordkey2 ～ redispasswordkey5を使用して、必要な定義の分だけ値を設定する。</BR>

##### 4.4.9.8.5 wsapcpmpasswordname(パスワード参照先es)
帳票作成/メール作成用PNR取得パスワード参照先のExternalSecret名を定義する。</BR>

##### 4.4.9.8.6 wsapcpmpasswordkey(パスワード参照先esキー)
帳票作成/メール作成用PNR取得パスワード参照先のExternalSecretキーを定義する。</BR>

##### 4.4.9.8.7 wsapcivpasswordname(パスワード参照先es)
インベントリ情報取得パスワード参照先のExternalSecret名を定義する。</BR>

##### 4.4.9.8.8 wsapcivpasswordkey(パスワード参照先esキー)
インベントリ情報取得パスワード参照先のExternalSecretキーを定義する。</BR>

##### 4.4.9.8.9 wsapcfrpasswordname(パスワード参照先es)
SPOOK用運賃取得パスワード参照先のExternalSecret名を定義する。</BR>

##### 4.4.9.8.10 wsapcfrpasswordkey(パスワード参照先esキー)
SPOOK用運賃取得パスワード参照先のExternalSecretキーを定義する。</BR>

### 4.4.10 addEnv(追加環境変数)
アプリケーションで固有に定義する環境変数が必要となる場合に定義する。</BR>
この方式では、環境変数名とその値の組み合わせを、設定ファイル内に定義する。

【addEnv/refConfigMap/refExternalSecretの使い分けについて】</BR>
- 「4.4.10 addEnv(追加環境変数)」はPod単独で使用する環境変数の定義</BR>
- 「4.4.11 refConfigMap(コンフィグマップ参照)」は共通的に作成されたコンフィグマップから値を取得し環境変数へ反映する定義</BR>
- 「4.4.12 refExternalSecret(ExternalSecret参照)」は共通的に作成されたExternalSecretから値を取得し環境変数へ反映する定義</BR>

#### 4.3.10.1 enabled(有効化フラグ)
追加する環境変数が必要な場合に `true` を設定し、data部に環境変数名と値を定義する。</BR>
作成フラグを `true` に設定した場合、固有に定義する環境変数名と値を定義する。</BR>

#### 4.4.10.2 refer(データ)
- コンフィグマップを作成する場合は有効化フラグ(enabled)を `true` とした上で、キーと値を定義する。</BR>
- キーと値は、「キー名: 値」の形式で記述し、セパレータ":"の後は半角空白文字1文字「 」を空けて値を記入する。</BR>
【定義例】</BR>

  ```
  addEnv:
    enabled: true
    data:
      - name: "ENV_NAME1"
      - value: "ENV_NAME1_VALUE"
      - name: "ENV_NAME2"
      - value: "ENV_NAME2_VALUE"
  ```

### 4.4.11 refConfigMap(コンフィグマップ参照)
アプリケーションで参照するコンフィグマップを参照する場合に定義する。</BR>
この方式では、定義済みのコンフィグマップ名とそのキーを指定し環境変数に読み込む</BR>

【addEnv/refConfigMap/refExternalSecretの使い分けについて】</BR>
- 「4.4.10 addEnv(追加環境変数)」はPod単独で使用する環境変数の定義</BR>
- 「4.4.11 refConfigMap(コンフィグマップ参照)」は共通的に作成されたコンフィグマップから値を取得し環境変数へ反映する定義</BR>
- 「4.4.12 refExternalSecret(ExternalSecret参照)」は共通的に作成されたExternalSecretから値を取得し環境変数へ反映する定義</BR>

#### 4.4.11.1 enabled(有効化フラグ)
コンフィグマップが必要な場合に `true` を設定し、refer部に環境変数名と参照するコンフィグマップ名とキーを定義する。</BR>

#### 4.4.11.2 refer(コンフィグマップ参照定義)
- コンフィグマップを作成する場合は有効化フラグ(enabled)を `true` した上で、コンフィグマップを参照する環境変数を定義する。</BR>
- 参照先のコンフィグマップ名とキーはrefer部で定義する。</BR>
【定義例】</BR>

  ```
  configmap:
    enabled: true
    refer:
      - name: "ENV_CONFIGMAP1"
        valueFrom:
          configMapKeyRef:
            name: "[コンフィグマップ名1]"
            key: "[キー1]"
      - name: "ENV_CONFIGMAP2"
        valueFrom:
          configMapKeyRef:
            name: "[コンフィグマップ名2]"
            key: "[キー2]"
  ```

### 4.4.12 refExternalSecret(ExternalSecret参照)
アプリケーションで参照するExternalSecretの参照する場合に定義する。</BR>
この方式では、定義済みのExternalSecret名とそのキーを指定し環境変数に読み込む。</BR>

【addEnv/refConfigMap/refExternalSecretの使い分けについて】</BR>
- 「4.4.10 addEnv(追加環境変数)」はPod単独で使用する環境変数の定義</BR>
- 「4.4.11 refConfigMap(コンフィグマップ参照)」は共通的に作成されたコンフィグマップから値を取得し環境変数へ反映する定義</BR>
- 「4.4.12 refExternalSecret(ExternalSecret参照)」は共通的に作成されたExternalSecretから値を取得し環境変数へ反映する定義</BR>

#### 4.4.12.1 enabled(有効化フラグ)
ExternalSecretが必要な場合に `true` を設定し、refer部に環境変数名と参照するExternalSecret名とキーを定義する。</BR>

#### 4.4.12.2 refer(ExternalSecret参照定義)
- ExternalSecret参照する場合は、有効化フラグ(enabled)を `true` とした上で、</BR>
ExternalSecretを参照する環境変数を定義する。</BR>
- 参照先のExternalSecret名とキーはrefer部で定義する。</BR>
【定義例】</BR>

  ```
  refExternalSecret:
    enabled: true
    refer:
      - name: "ENV_EXSECRET1"
        valueFrom:
          secretKeyRef:
            name: "[ExternalSecret名1]"
            key: "[キー1]"
      - name: "ENV_EXSECRET2"
        valueFrom:
          secretKeyRef:
            name: "[ExternalSecret名2]"
            key: "[キー2]"
  ```

   
   
   
   
