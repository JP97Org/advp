package org.jojo.util;

import java.io.*;

import org.jojo.util.TextUtil;

/** for CSV '";"' separated */
public class DataSaverAndLoader {
    private static final String DELIM = "\";\"";
   
    // ALL:
    private String encoding;

    // Contstructor
    /** standard encoding of OS! */
    public DataSaverAndLoader() {
        this.encoding = null;
    }

    // Constructor
    /**
     * @param encoding
     *            - encoding to be used
     */
    public DataSaverAndLoader(String encoding) {
        this.encoding = encoding;
    }

    protected static File data(String address) {
        File data = new File(address);
        return data;
    }

    // TESTED -->nicht direkt verwenden!
    protected BufferedWriter out(File f) throws IOException {
        if (encoding == null)
            return new BufferedWriter(new FileWriter(f));
        else
            return new BufferedWriter(
                    new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(f)), encoding));
    }

    // TESTED -->nicht direkt verwenden!
    protected BufferedWriter out(String fAddress) throws IOException {
        return out(data(fAddress));
    }

    // TESTED -->nicht direkt verwenden!
    protected BufferedReader in(File f) throws UnsupportedEncodingException, FileNotFoundException {
        if (encoding == null)
            return new BufferedReader(new FileReader(f));
        else
            return new BufferedReader(new InputStreamReader(new BufferedInputStream(new FileInputStream(f)), encoding));
    }

    // TESTED -->nicht direkt verwenden!
    protected BufferedReader in(String fAddress) throws UnsupportedEncodingException, FileNotFoundException {
        return in(data(fAddress));
    }

    // TESTED
    public int size(File f) throws UnsupportedEncodingException, FileNotFoundException, IOException {
        int i = 0;
        BufferedReader in = in(f);
        while (in.readLine() != null) {
            i++;
        }
        return i;
    }

    // TESTED
    public int size(String fAddress) throws UnsupportedEncodingException, FileNotFoundException, IOException {
        return size(data(fAddress));
    }

    // LOAD:

    // TESTED
    public String[] aLine(File f, int line) throws IOException {
        BufferedReader in = in(f);
        if (line != 0) {
            for (int i = 0; in.readLine() != null && i < line - 1; i++) {

            }
            String s = in.readLine();
            s = s.substring(1, s.length() - 1);
            String ret[] = s.split(DELIM);
            return ret;
        } else {
            String s = in.readLine();
            s = s.substring(1, s.length() - 1);
            String ret[] = s.split(DELIM);
            return ret;
        }
    }

    // TESTED
    public String[] aLine(String fAddress, int line) throws IOException {
        return aLine(data(fAddress), line);
    }

    // TESTED
    public String[] aColumn(File f, int i) throws IOException {
        String[][] s = allDataValues(f);
        String[] ret = new String[s.length];
        for (int c = 0; c < s.length; c++) {
            ret[c] = s[c][i];
        }
        return ret;
    }

    // TESTED
    public String[] aColumn(String fAddress, int i) throws IOException {
        return aColumn(data(fAddress), i);
    }

    /**
     * @param x
     *            = line
     * @param y
     *            = column
     */
    public String aDataValue(File f, int x, int y) throws IOException {
        String[] ret = aLine(f, x);
        if (ret != null)
            return ret[y];
        else
            return null;
    }

    /**
     * @param x
     *            = line
     * @param y
     *            = column
     */
    public String aDataValue(String fAddress, int x, int y) throws IOException {
        return aDataValue(data(fAddress), x, y);
    }

    // TESTED
    public String[][] allDataValues(File f) throws IOException {
        BufferedReader in = in(f);
        String[] lines = new String[size(f)];
        for (int i = 0; i < lines.length; i++) {
            lines[i] = in.readLine();
            lines[i] = lines[i].substring(1, lines[i].length() - 1);
        }

        return TextUtil.twoDimensionalize(lines, DELIM);
    }

    // TESTED
    public String[][] allDataValues(String fAddress) throws IOException {
        return allDataValues(data(fAddress));
    }

    // SAVE:

    /**
     * @param x
     *            = line
     * @param y
     *            = column
     */
    public void saveAValue(File f, String value, int x, int y) throws IOException {
        String[][] toSave = allDataValues(f);
        toSave[x][y] = value;
        saveData(f, toSave);
    }

    /**
     * @param x
     *            = line
     * @param y
     *            = column
     */
    public void saveAValue(String fAddress, String value, int x, int y) throws IOException {
        saveAValue(data(fAddress), value, x, y);
    }

    // TESTED
    public void saveALine(File f, String[] content, int line) throws IOException {
        String[][] toSave = allDataValues(f);
        toSave[line] = content;
        saveData(f, toSave);
    }

    // TESTED
    public void saveALine(String fAddress, String[] content, int line) throws IOException {
        saveALine(data(fAddress), content, line);
    }

    // TESTED
    public void saveAColumn(File f, String[] content, int i) throws IOException {
        String[][] toSave = allDataValues(f);
        System.out.println("TS_LEN: " + toSave.length);
        for (int c = 0; c < toSave.length; c++) {
            toSave[c][i] = content[c];
        }
        saveData(f, toSave);
    }

    // TESTED
    public void saveData(File f, String[][] valuesin) throws FileNotFoundException, IOException {
        BufferedWriter out = out(f);
        for (int i = 0; i < valuesin.length; i++) {
            out.write("\"" + TextUtil.unsplitStringArray(valuesin[i], DELIM) + "\"");
            out.newLine();
        }
        out.flush();
        out.close();
    }

    // TESTED
    public void saveData(String fAddress, String[][] valuesin) throws IOException {
        saveData(data(fAddress), valuesin);
    }

    // TESTED
    public void appendData(File f, String[] appendix) throws IOException {
        BufferedWriter out = new BufferedWriter(new FileWriter(f, true));
        out.write("\"" + TextUtil.unsplitStringArray(appendix, DELIM) + "\"");
        out.flush();
        out.close();
    }

    // TESTED
    public void appendData(String fAddress, String[] appendix) throws IOException {
        appendData(data(fAddress), appendix);
    }

}
