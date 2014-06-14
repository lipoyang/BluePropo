BluePropo
=========

## 概要
Bluetoothを使ったAndroid用ラジコンプロポアプリ、およびArduino用スケッチです。

![概念図](http://licheng.sakura.ne.jp/steer/overview2.png)

Androidアプリは、下図のようなUIです。

![アプリの画面](http://licheng.sakura.ne.jp/steer/UI_small.png)

ラジコンは、Arduino(互換ボード)やBluetoothモジュールなどを組み合わせて作ります。
下図はミニ四駆を改造して作ったラジコンです。

![ラジコンの写真](http://licheng.sakura.ne.jp/steer/photo2.jpg)

## 動作環境
### システム要件
* Android端末: Android 2.2 (API Level 8)以上で、BluetoothでSPPが使用可能な機種
* マイコン: Arduino またはスケッチ互換のボード
* Bluetoothモジュール: 3.3VのUART I/F (RxD,TxDのみ使用)を持つBluetoothシリアルモジュール ※
* DCモータードライバ: テキサスインスツルメンツ DRV8830

※ スレーブモード・19200 baudで動作するようあらかじめ設定可能であること。

### 確認済み環境
* Android端末(1): Galaxy Nexus, Android 4.2.2, xdpi 1280×720 pixel
* Android端末(2): Xperia Pro, Android 2.3.4, hdpi 854×480 pixel
* マイコン: GR-KURUMI (Arduino Pro Mini 互換ボード, ルネサスRL78/G13マイコン) 
* Bluetoothモジュール: 浅草ギ研 BlueMaster
* DCモータードライバ: ストロベリーリナックス DRV8830 I2Cモータードライバ・モジュール

## ファイル一覧
* BluePropo/: Android用プロポアプリのソース一式
* BlueSerial/: BluePropoで使用するライブラリのソース一式
* MiniSteer/: ラジコン受信器となるマイコンのソース一式
* BluePropo_UI.svg: BluePropoのUIデザイン素材データ (Inkscape等で編集可能)
* LICENSE: Apache Licence 2.0です
* README.md これ

## 使い方

### Androidアプリのインストール
* BlueSerialとBluePropoのプロジェクトフォルダをADTにインポートします。
* BluePropoをADTからAndroid端末にインストールして実行します。

### Arduinoスケッチのインストール
* MiniSteerのプロジェクトフォルダをArduino IDEで開きます。
* GR-KURUMIの場合は、Arduino IDEベースのIDE for GRの使用を推奨します。
* コンパイルしてターゲットのボードに書き込みます。

### ハードウェア(例)
![実体配線図](http://licheng.sakura.ne.jp/steer/wiring.png)

### アプリの操作
* Bluetoothロゴのボタンを押すと、接続するデバイスを選択する画面になります。
* ボタンの色は橙が未接続、黄が接続中、青が接続済を示します。
* 見てのとおり、ラジコンプロポの要領で2本のスティックを操作します。
