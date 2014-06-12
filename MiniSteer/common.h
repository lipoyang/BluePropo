/*
This software is available under NYSL(Nirunari Yakunari Sukinishiro License).
*/

//#ifndef    _COMMON_H_
//#define    _COMMON_H_

/* 汎用の関数 */

void Uint16ToDec(char *str, unsigned short  val, int digit);
int  DecToUint16(char *str, unsigned short *val, int digit);
void Uint16ToHex(char *str, unsigned short  val, int digit);
int  HexToUint16(char *str, unsigned short *val, int digit);

//#endif

