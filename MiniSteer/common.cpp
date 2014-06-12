/*
This software is available under NYSL(Nirunari Yakunari Sukinishiro License).
*/

/* 内部関数 */

/**
 * 10進数文字一桁を数値に変換 (サブルーチン)
 *
 * @param c 10進数文字
 * @return 0～9の数値 (エラー時は255)
 */
static unsigned char DecToDigit(char c)
{
    if( c>='0' && c<= '9'){
        return (unsigned char)(c - '0');

    }else{
        return 255;         /* エラー */
    }
}

/**
 * 0～9の数値を10進数文字に変換 (サブルーチン)
 *
 * @param val 0～9の数値
 * @return 10進数文字 (エラー時は'Z')
 */
static char DigitToDec(unsigned char val)
{
    if( val <= 9 ){
        return (char)('0' + val);

    }else{
        return 'Z';         /* エラー */
    }
}

/**
 * 16進数文字一桁を数値に変換 (サブルーチン)
 *
 * @param c 16進数文字
 * @return 0～15の数値 (エラー時は255)
 */
static unsigned char HexToDigit(char c)
{
    if( c>='0' && c<= '9'){
        return (unsigned char)(c - '0');

    }else if( (c>='A') && (c<= 'F') ){
        return (unsigned char)(10 + c - 'A');

    }else if( (c>='a') && (c<= 'f') ){
        return (unsigned char)(10 + c - 'a');

    }else{
        return 255;     /* エラー */
    }
}

/**
 * 0～15の数値を16進数文字に変換 (サブルーチン)
 *
 * @param val 0～15の数値
 * @return 16進数文字 (エラー時は'Z')
 */
static char DigitToHex(unsigned char val)
{
    if( val <= 9 ){
        return (char)('0' + val);

    }else if( val <= 15){
        return (char)('A' + (val-10));

    }else{
        return 'Z';     /* エラー */
    }
}

/* API関数 */

/**
 * 10進数文字列を16ビット整数値に変換
 *
 * @param str 10進数文字列
 * @param val 変換した数値を返す
 * @param digit 桁数
 * @return 成否
 */
int DecToUint16(char *str, unsigned short *val, int digit)
{
int result = 0;
unsigned short acc = 0;
unsigned char  d;
int i;
    
    for(i=0;i<digit;i++){
        d = DecToDigit(str[i]);
        if(d > 9){
            result = -1;    /* エラー */
        }
        acc *= 10;
        acc += (unsigned short)d;
    }
    
    if(result == 0){
        *val = acc;
    }else{
        /* エラー時はvalを変更しない */
    }
    return result;
}

/**
 * 16ビット整数値を10進数文字列に変換
 *
 * @param str 変換した10進数文字列を返す
 * @param val 数値
 * @param digit 桁数
 */
void Uint16ToDec(char *str, unsigned short val, int digit)
{
int i;

    for(i=digit;i>0;i--){
        str[i-1] = DigitToDec((unsigned char)(val % 10));
        val /= 10;
    }
}

/**
 * 16進数文字列を16ビット整数値に変換
 *
 * @param str 16進数文字列
 * @param val 変換した数値を返す
 * @param digit 桁数
 * @return 成否
 */
int HexToUint16(char *str, unsigned short *val, int digit)
{
int result = 0;
unsigned short acc = 0;
unsigned char  d;
int i;
    
    for(i=0;i<digit;i++){
        d = HexToDigit(str[i]);
        if(d > 15){
            result = -1;    /* エラー */
        }
        acc <<= 4;
        acc += (unsigned short)d;
    }
    
    if(result == 0){
        *val = acc;
    }else{
        /* エラー時はvalを変更しない */
    }
    return result;
}

/**
 * 16ビット整数値を16進数文字列に変換
 *
 * @param str 変換した16進数文字列を返す
 * @param val 数値
 * @param digit 桁数
 */
void Uint16ToHex(char *str, unsigned short val, int digit)
{
int i;
    
    for(i=digit;i>0;i--){
        str[i-1] = DigitToHex((unsigned char)(val & 0x000F));
        val >>= 4;
    }
}

