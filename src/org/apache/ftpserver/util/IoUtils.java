package org.apache.ftpserver.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Random;

public class IoUtils {

    private static final Random RANDOM_GEN = new Random(System.currentTimeMillis());

    public static final BufferedInputStream getBufferedInputStream(InputStream in) {
        BufferedInputStream bin = null;
        if (in instanceof java.io.BufferedInputStream) {
            bin = (BufferedInputStream) in;
        } else {
            bin = new BufferedInputStream(in);
        }
        return bin;
    }

    public static final BufferedOutputStream getBufferedOutputStream(OutputStream out) {
        BufferedOutputStream bout = null;
        if (out instanceof java.io.BufferedOutputStream) {
            bout = (BufferedOutputStream) out;
        } else {
            bout = new BufferedOutputStream(out);
        }
        return bout;
    }

    public static final BufferedReader getBufferedReader(Reader reader) {
        BufferedReader buffered = null;
        if (reader instanceof java.io.BufferedReader) {
            buffered = (BufferedReader) reader;
        } else {
            buffered = new BufferedReader(reader);
        }
        return buffered;
    }

    public static final BufferedWriter getBufferedWriter(Writer wr) {
        BufferedWriter bw = null;
        if (wr instanceof java.io.BufferedWriter) {
            bw = (BufferedWriter) wr;
        } else {
            bw = new BufferedWriter(wr);
        }
        return bw;
    }

    public static final File getUniqueFile(File oldFile) {
        File newFile = oldFile;
        while (true) {
            if (!newFile.exists()) {
                break;
            }
            newFile = new File(oldFile.getAbsolutePath() + '.' + Math.abs(RANDOM_GEN.nextLong()));
        }
        return newFile;
    }

    public static final void close(InputStream is) {
        if (is != null) {
            try {
                is.close();
            } catch (Exception ex) {
            }
        }
    }

    public static final void close(OutputStream os) {
        if (os != null) {
            try {
                os.close();
            } catch (Exception ex) {
            }
        }
    }

    public static final void close(Reader rd) {
        if (rd != null) {
            try {
                rd.close();
            } catch (Exception ex) {
            }
        }
    }

    public static final void close(Writer wr) {
        if (wr != null) {
            try {
                wr.close();
            } catch (Exception ex) {
            }
        }
    }

    public static final String getStackTrace(Throwable ex) {
        String result = "";
        if (ex != null) {
            try {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                ex.printStackTrace(pw);
                pw.close();
                sw.close();
                result = sw.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static final void copy(Reader input, Writer output, int bufferSize) throws IOException {
        char buffer[] = new char[bufferSize];
        int n = 0;
        while ((n = input.read(buffer)) != -1) {
            output.write(buffer, 0, n);
        }
    }

    public static final void copy(InputStream input, OutputStream output, int bufferSize) throws IOException {
        byte buffer[] = new byte[bufferSize];
        int n = 0;
        while ((n = input.read(buffer)) != -1) {
            output.write(buffer, 0, n);
        }
    }

    public static final String readFully(Reader reader) throws IOException {
        StringWriter writer = new StringWriter();
        copy(reader, writer, 1024);
        return writer.toString();
    }

    public static final String readFully(InputStream input) throws IOException {
        StringWriter writer = new StringWriter();
        InputStreamReader reader = new InputStreamReader(input);
        copy(reader, writer, 1024);
        return writer.toString();
    }

    public static final void delete(File file) throws IOException {
        if (!file.exists()) {
            return;
        } else if (file.isDirectory()) {
            deleteDir(file);
        } else {
            deleteFile(file);
        }
    }

    private static final void deleteDir(File dir) throws IOException {
        File[] children = dir.listFiles();
        if (children == null) {
            return;
        }
        for (int i = 0; i < children.length; i++) {
            File file = children[i];
            delete(file);
        }
        if (!dir.delete()) {
            throw new IOException("Failed to delete directory: " + dir);
        }
    }

    private static final void deleteFile(File file) throws IOException {
        if (!file.delete()) {
            if (OS.isFamilyWindows()) {
                System.gc();
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }
            if (!file.delete()) {
                throw new IOException("Failed to delete file: " + file);
            }
        }
    }
}
