DFBreak
===============

DAYFILERのroot化ツールです。

## ビルド方法
1. 通常のAndroidプロジェクトとしてコンパイルする
2. DAYFILERの/system/appに存在するAlarmShutDownNotification.apkを使って偽装署名する

### 偽装署名の方法
1. [こちら](https://github.com/Fuzion24/ZipArbitrage/tree/master/bin)よりAndroidMasterKeys.jarをダウンロードする
2. 次のようにターミナルで入力し、実行する
`java -jar AndroidMasterKeys.jarへのパス -a オリジナルのAPKのパス -z ビルドしたAPKのパス`

その後、偽装署名されたAPKが"MasterKeysModded-AlarmShutDownNotification.apk"という名前で出力されます。
