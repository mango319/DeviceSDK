package com.smartdevicesdk.fingerprint;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by Administrator on 8/2/13.
 */
public class DebugManage {

    public static void  DeleteLog() {
        File file = new File("mnt/sdcard/log/log.txt");
        file.delete();
    }
    
    public static void WriteLog(String strLog)
    {
        String logPath = "log\\log.txt";

        File templog = new File(logPath);

        try {

            if(!templog.exists())
                templog.createNewFile();

            RandomAccessFile raf = new RandomAccessFile(logPath, "rw");
            raf.seek(raf.length());

            raf.writeBytes(strLog);
            raf.close();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void  WriteLog2(String str) {
        String str_Path_Full = "mnt/sdcard/log/log.txt";
        File file = new File(str_Path_Full);
        if (file.exists() == false) {
            try {
                file.createNewFile();
            } catch (IOException e) {
            }
        } else {
            try {
                BufferedWriter bfw = new BufferedWriter(new FileWriter(str_Path_Full,true));
                bfw.write(str);
                bfw.write("\n");
                bfw.flush();
                bfw.close();
            } catch (FileNotFoundException e) {

            } catch (IOException e) {

            }
        }
    }
    
    public static void WriteBmp(byte[] image, int nlen)
    {
        String logPath = "mnt/sdcard/log/fp.bmp";

        File templog = new File(logPath);

        try {

            if(!templog.exists())
                templog.createNewFile();

            RandomAccessFile raf = new RandomAccessFile(logPath, "rw");
            raf.seek(raf.length());

            raf.write(image, 0, nlen);
            raf.close();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }    
}
