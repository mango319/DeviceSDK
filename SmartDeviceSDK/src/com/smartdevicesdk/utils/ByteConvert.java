/*
 * Created on 2004-5-11
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
/*
 * updated by Hans_j on 2004-6-15
 *
 * 1, overload the method buf2String to print string in one line
 * 2, update the method to deal with the bigIndian
 */
/*
 * update start by hans_j at 2004-9-2 11:24:54
 * add the byteString2Bytes, convert the byte String in hex "02 03 af a4 " to byte array
 */
 /*
 * updated by Hans_j on 2005-6-8
 *
 * 1, the using of big-endian is wrong in int <--> byte 
 */

package com.smartdevicesdk.utils;

import java.util.StringTokenizer;


final public class ByteConvert {
    public final static String lineSeperate = System.getProperty("line.separator");

    final public static int DEFAULT_BUFFERLENGTH = 1024 * 3;

    final public static int DEFAULT_TABLELENGTH = 256;

    private static StringBuffer sBuf;

    private static String[] convertTable;

    static {
        convertTable = new String[DEFAULT_TABLELENGTH];
        sBuf = new StringBuffer(DEFAULT_BUFFERLENGTH);

        int i = 0;
        for (i = 0; i < 16; i++) {
            convertTable[i] = "0" + Integer.toHexString(i) + " ";
        }
        for (; i < 256; i++) {
            convertTable[i] = Integer.toHexString(i) + " ";
        }
    }

    public static int byte2int2(byte[] convertByte) {
        return byte2int2(convertByte, 0, true);
    }

    public static int byte2int2(byte[] convertByte, boolean bigIndian) {
        return byte2int2(convertByte, 0, bigIndian);
    }

    public static int byte2int2(byte[] convertByte, int offset) {
        return byte2int2(convertByte, offset, true);
    }

    public static int byte2int2(byte[] convertByte, int offset,
            boolean bigIndian) {
        int value = 0;
        int byte0, byte1;

        byte0 = convertByte[0 + offset] < 0 ? convertByte[0 + offset] + 256
                : convertByte[0 + offset];
        byte1 = convertByte[1 + offset] < 0 ? convertByte[1 + offset] + 256
                : convertByte[1 + offset];
        if (!bigIndian)
            value = byte1 * 256 + byte0;
        else
            value = byte0 * 256 + byte1;

        return value;
    }


    /** convert four byte to int.
     * @param convertByte byte stream
     * @return int
     * @exception exceptions No exceptions thrown
     */
    public static int byte2int4(byte[] convertByte) {
        return byte2int4(convertByte, 0, true);
    }

    public static int byte2int4(byte[] convertByte, boolean bigIndian) {
        return byte2int4(convertByte, 0, bigIndian);
    }

    /** convert four byte to int.
     * @param convertByte byte stream
     * @param offset offset of byte stream to be converted
     * @return int
     * @exception exceptions No exceptions thrown
     */
    public static int byte2int4(byte[] convertByte, int offset) {
        return byte2int4(convertByte, offset, true);
    }

    public static int byte2int4(byte[] convertByte, int offset,
            boolean bigIndian) {
        int value = 0;
        int byte0, byte1, byte2, byte3;

        byte0 = convertByte[0 + offset] < 0 ? convertByte[0 + offset] + 256
                : convertByte[0 + offset];
        byte1 = convertByte[1 + offset] < 0 ? convertByte[1 + offset] + 256
                : convertByte[1 + offset];
        byte2 = convertByte[2 + offset] < 0 ? convertByte[2 + offset] + 256
                : convertByte[2 + offset];
        byte3 = convertByte[3 + offset] < 0 ? convertByte[3 + offset] + 256
                : convertByte[3 + offset];
        if (!bigIndian)
            value = (byte3 << 24) + (byte2 << 16) + (byte1 << 8) + byte0;
        else
            value = (byte0 << 24) + (byte1 << 16) + (byte2 << 8) + byte3;
        return value;
    }

    /** convert short to two byte.
     * @param value int value represent unsigned short
     * @return byte[] 
     * @exception exceptions No exceptions thrown
     */
    public static byte[] int2byte2(int value) {
        return int2byte2(value, true);
    }

    public static byte[] int2byte2(int value, boolean bigIndian) {
        byte[] byteValue = new byte[2];
        if (!bigIndian) {
            byteValue[0] = (byte) (value & 0xff);
            byteValue[1] = (byte) ((value & 0xff00) >>> 8);
        } else {
            byteValue[1] = (byte) (value & 0xff);
            byteValue[0] = (byte) ((value & 0xff00) >>> 8);
        }
        return byteValue;
    }

    /** convert short to two byte.
     * @param value int value represent unsigned short
     * @param fillByte byte stream to set
     * @return void
     * @exception exceptions No exceptions thrown
     */
    public static void int2byte2(int value, byte[] fillByte) {
        int2byte2(value, fillByte, 0, true);
    }

    public static void int2byte2(int value, byte[] fillByte, boolean bigIndian) {
        int2byte2(value, fillByte, 0, bigIndian);
    }

    /** convert short to two byte.
     * @param value int value represent unsigned short
     * @param fillByte byte stream to set
     * @param fillByte at the offset of byte stream to set
     * @return void
     * @exception exceptions No exceptions thrown
     */
    public static void int2byte2(int value, byte[] fillByte, int offset) {
        int2byte2(value, fillByte, offset, true);
    }

    public static void int2byte2(int value, byte[] fillByte, int offset,
            boolean bigIndian) {
        if (!bigIndian) {
            fillByte[0 + offset] = (byte) (value & 0xff);
            fillByte[1 + offset] = (byte) ((value & 0xff00) >>> 8);
        } else {
            fillByte[1 + offset] = (byte) (value & 0xff);
            fillByte[0 + offset] = (byte) ((value & 0xff00) >>> 8);
        }
    }

    /** convert int to four byte.
     * @param value int
     * @return byte[] 
     * @exception exceptions No exceptions thrown
     */
    public static byte[] int2byte4(int value) {
        return int2byte4(value, true);
    }

    public static byte[] int2byte4(int value, boolean bigIndian) {
        byte[] byteValue = new byte[4];
        if (!bigIndian) {
            byteValue[0] = (byte) (value & 0xff);
            byteValue[1] = (byte) ((value & 0xff00) >>> 8);
            byteValue[2] = (byte) ((value & 0xff0000) >>> 16);
            byteValue[3] = (byte) ((value & 0xff000000) >>> 24);
        } else {
            byteValue[3] = (byte) (value & 0xff);
            byteValue[2] = (byte) ((value & 0xff00) >>> 8);
            byteValue[1] = (byte) ((value & 0xff0000) >>> 16);
            byteValue[0] = (byte) ((value & 0xff000000) >>> 24);
        }
        return byteValue;
    }

    /** convertint to four byte.
     * @param value int value represent unsigned short
     * @param fillByte byte stream to set
     * @return void
     * @exception exceptions No exceptions thrown
     */
    public static void int2byte4(int value, byte[] fillByte) {
        int2byte4(value, fillByte, 0, true);
    }

    public static void int2byte4(int value, byte[] fillByte, boolean bigIndian) {
        int2byte4(value, fillByte, 0, bigIndian);
    }

    /** convertint to four byte.
     * @param value int value represent unsigned short
     * @param fillByte byte stream to set
     * @param fillByte at the offset of byte stream to set
     * @return void
     * @exception exceptions No exceptions thrown
     */
    public static void int2byte4(int value, byte[] fillByte, int offset) {
        int2byte4(value, fillByte, offset, true);
    }

    public static void int2byte4(int value, byte[] fillByte, int offset,
            boolean bigIndian) {
        if (!bigIndian) {
            fillByte[0 + offset] = (byte) (value & 0xff);
            fillByte[1 + offset] = (byte) ((value & 0xff00) >>> 8);
            fillByte[2 + offset] = (byte) ((value & 0xff0000) >>> 16);
            fillByte[3 + offset] = (byte) ((value & 0xff000000) >>> 24);
        } else {
            fillByte[3 + offset] = (byte) (value & 0xff);
            fillByte[2 + offset] = (byte) ((value & 0xff00) >>> 8);
            fillByte[1 + offset] = (byte) ((value & 0xff0000) >>> 16);
            fillByte[0 + offset] = (byte) ((value & 0xff000000) >>> 24);
        }
    }

    public synchronized static String buf2String(String info, byte[] buf) {
        return buf2String(info, buf, 0, buf.length, true);
    }

    public synchronized static String buf2String(String info, byte[] buf,
            boolean oneLine16) {
        return buf2String(info, buf, 0, buf.length, oneLine16);
    }

    public synchronized static String buf2String(String info, byte[] buf,
            int offset, int length) {
        return buf2String(info, buf, offset, length, true);
    }

    public synchronized static String buf2String(String info, byte[] buf,
            int offset, int length, boolean oneLine16) {
        int i, index;

        sBuf.delete(0, sBuf.length());
        sBuf.append(info);

        for (i = 0 + offset; i < length + offset; i++) {
            if (i % 16 == 0) {
                if (oneLine16) {
                    sBuf.append(lineSeperate);
                }
            } else if (i % 8 == 0) {
                if (oneLine16) {
                    sBuf.append("   ");
                }
            }
            index = buf[i] < 0 ? buf[i] + DEFAULT_TABLELENGTH : buf[i];
            sBuf.append(convertTable[index]);
        }

        return sBuf.toString();
    }

    public static byte[] byteString2Bytes(String s) {
        if (s == null) {
            return new byte[0];
        }
        StringTokenizer st = new StringTokenizer(s, " \n\t");
        int ilen = st.countTokens();
        if (ilen <= 0) {
            return new byte[0];
        } else {
            byte[] buf = new byte[ilen];
            int i = 0;
            while (st.hasMoreElements()) {
                try {
                    buf[i] = (byte) ((Integer.parseInt(st.nextToken(), 16)) & 0xff);
                } catch (NumberFormatException ex) {
                    System.out.println("Exception in string2Bytes : "
                            + ex.getMessage());
                    buf[i] = (byte) 0xff;
                }
                i++;
            }
            return buf;
        }
    }
}