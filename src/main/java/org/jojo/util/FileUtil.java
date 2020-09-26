package org.jojo.util;

import java.io.*;
import java.nio.channels.FileChannel;

public class FileUtil {
    private FileUtil() {
        
    }
    
    public static void copyFiles(File[] in, File[] out) throws IOException {
        if (in.length == out.length) {
            for (int i = 0; i < in.length; i++) {
                copyFile(in[i], out[i]);
            }
        }
    }

    public static void cutFiles(File[] in, File[] out) throws IOException {
        if (in.length == out.length) {
            for (int i = 0; i < in.length; i++) {
                cutFile(in[i], out[i]);
            }
        }
    }

    public static void copyFile(File in, File out) throws IOException {
        if (!out.exists())
            out.createNewFile();

        FileInputStream fisIn = new FileInputStream(in);
        FileInputStream fisOut = new FileInputStream(in);
        FileChannel inChannel = fisIn.getChannel();
        FileChannel outChannel = fisOut.getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } catch (IOException e) {
            throw e;
        } finally {
            if (inChannel != null) {
                inChannel.close();
                fisIn.close();
            }
            if (outChannel != null) {
                outChannel.close();
                fisOut.close();
            }
        }
    }

    public static void cutFile(File in, File out) throws IOException {
        if (!out.exists())
            out.createNewFile();

        FileInputStream fisIn = new FileInputStream(in);
        FileInputStream fisOut = new FileInputStream(in);
        FileChannel inChannel = fisIn.getChannel();
        FileChannel outChannel = fisOut.getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
            in.delete();
        } catch (IOException e) {
            throw e;
        } finally {
            if (inChannel != null) {
                inChannel.close();
                fisIn.close();
            }
            if (outChannel != null) {
                outChannel.close();
                fisOut.close();
            }
        }
    }
}
