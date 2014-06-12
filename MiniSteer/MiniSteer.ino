/*
This software is available under NYSL(Nirunari Yakunari Sukinishiro License).
*/

/*GR-(RL78) Sketch Template Version: E0.80*/
#include <RLduino78.h>
#include <Wire.h>
#include <Servo.h>
#include "common.h"

// Pin 22,23,24 are assigned to RGB LEDs.
int led_red   = 22; // LOW active
int led_green = 23; // LOW active
int led_blue  = 24; // LOW active

// 駆動輪モータドライバのI2Cスレーブアドレス
#define I2C_DRV8830_ADRS 0x64  // 7bit, 0b1100100

// ステアリング用サーボ
Servo servo;

// 0x02/0x03だとデバッグしにくいので、#/$ を 電文開始/終了 に使う
// 電文開始
#define CODE_STX '#'
// 電文終了
#define CODE_ETX '$'
// 電文開始待ち状態
#define STATE_READY     0
// 電文受信中状態
#define STATE_RECEIVING 1

// 受信コマンド処理 (後述)
void execute_command(char* buff);

// 初期設定
void setup() {
    // シリアル通信の設定
    Serial.begin(19200);
    
    // I2Cの設定
    Wire.begin();
    
    // PWMの設定
    servo.attach(5); // D5にサーボを接続
    servo.write(90); // 初期値は90°(ニュートラル)
    
    
     pinMode(led_red, OUTPUT);
     pinMode(led_green, OUTPUT);
     pinMode(led_blue, OUTPUT);
     digitalWrite(led_red, HIGH);
     digitalWrite(led_green, HIGH);
     digitalWrite(led_blue, HIGH);
}

// メインループ
void loop() {

    char c;
    static int state = STATE_READY;
    static int ptr=0;
    static char buff[16];
    
    /* シリアル受信データがあるか？ */
    if (Serial.available() > 0)
    {
        //Serial.println("RECV ");
        c = Serial.read();
        switch(state)
        {
        /* 電文開始待ち状態 */
        case STATE_READY:
            /* 電文開始コードが来たら電文受信中状態へ */
            if(c == CODE_STX)
            {
                //Serial.println("STX ");
                state = STATE_RECEIVING;
                ptr = 0;
            }
            break;
        /* 電文受信中状態 */
        case STATE_RECEIVING:
            /* もしも電文開始コードが来たら受信中のデータを破棄 */
            if(c == CODE_STX)
            {
                //Serial.println("STX ");
                ptr = 0;
            }
            /* 電文終了コードが来たら、受信した電文のコマンドを実行 */
            else if(c == CODE_ETX)
            {
                //Serial.println("ETX ");
                execute_command(buff);
                state = STATE_READY;
            }
            /* 1文字受信 */
            else
            {
                buff[ptr] = c;
                ptr++;
                if(ptr>=16)
                {
                    state = STATE_READY;
                }
            }
            break;
        default:
            state = STATE_READY;
        }
    }
}

/**
 * 受信したコマンドの実行
 *
 * @param buff 受信したコマンドへのポインタ
 */
void execute_command(char* buff)
{
    int servoNum;
    unsigned short val;
    unsigned char data;
    
    switch(buff[0])
    {
    /* 駆動輪コマンド */
    case 'D':
        if( HexToUint16(&buff[1], &val, 2) == 0 )
        {
            // I2C送信
            data = (unsigned char)val;
                digitalWrite(led_red, LOW);
            Wire.beginTransmission(I2C_DRV8830_ADRS);  // スタート
                digitalWrite(led_green, LOW);
            Wire.write(0x00);                          // コントロールレジスタのアドレス
            Wire.write(data);                          // コントロールレジスタのデータ
            Wire.endTransmission();                    // ストップ
                digitalWrite(led_blue, LOW);
        }
        break;
        
    /* ステアリングコマンド */
    case 'B':
       /* 指令角度が有効(180以下)なら設定 */
        if( DecToUint16(&buff[1], &val, 3) == 0 )
        {
            if( val<=180 )
            {
                // サーボ制御
                servo.write(val);
            }
        }
        break;
    }
}
