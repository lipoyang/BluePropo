BluePropo
=========

## 概要
BluetoothのSPPを使ったAndroid用ラジコンプロポアプリ、およびArduino用スケッチです。  
TODO　イラスト、写真

## 動作環境
### システム要件
* Android端末: Android 2.2 (API Level 8)以上で、BluetoothでSPPが使用可能な機種
* マイコン: GR-KURUMI (Arduino Pro Mini 互換ボード, ルネサスRL78/G13マイコン) ※1  
* Bluetoothモジュール: 3.3VのUART I/F (RxD,TxDのみ使用)を持つBluetoothシリアルモジュール ※2
* DCモータードライバ: テキサスインスツルメンツ DRV8830

※1 基本的にArduino とスケッチ互換なので、ヘッダの#includeを変更すればArduinoでも動作するはず。
※2 スレーブモード・19200 baudで動作するようあらかじめ設定可能であること。

### 確認済み環境
* Android端末: Galaxy Nexus, Android 4.2.2, xdpi 1280×720 pixel
* マイコン: GR-KURUMI
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
TODO

