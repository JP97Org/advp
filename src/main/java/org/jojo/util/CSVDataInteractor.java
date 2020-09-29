package org.jojo.util;

import java.io.*;

import org.jojo.util.TextUtil;

/**
 * Represents a CSV interactor, i.e. a saver and loader for CSV files 
 * (separated by '";"', i.e. all elements enquoted and separated by semicolon).
 * 
 * @author jojo
 * @version 0.9
 */
public class CSVDataInteractor {
    private static final String DELIM = "\";\"";
   
    private String encoding;

    /**
     * Creates a new CSV interactor with standard encoding of OS.
     */
    public CSVDataInteractor() {
        this.encoding = null;
    }

    /**
     * Creates a new CSV interactor with the given encoding.
     * 
     * @param encoding - encoding to be used
     */
    public CSVDataInteractor(String encoding) {
        this.encoding = encoding;
    }

    private static File fileOf(String address) {
        File data = new File(address);
        return data;
    }

    private BufferedWriter out(File f) throws IOException {
        if (encoding == null)
            return new BufferedWriter(new FileWriter(f));
        else
            return new BufferedWriter(
                    new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(f)), encoding));
    }

    private BufferedReader in(File f) throws UnsupportedEncodingException, FileNotFoundException {
        if (encoding == null)
            return new BufferedReader(new FileReader(f));
        else
            return new BufferedReader(new InputStreamReader(new BufferedInputStream(new FileInputStream(f)), encoding));
    }

    /**
     * Gets the number of lines of the given file.
     * 
     * @param f - the given file
     * @return the number of lines of the given file
     * @throws UnsupportedEncodingException
     * @throws FileNotFoundException
     * @throws IOException
     */
    public int size(File f) throws UnsupportedEncodingException, FileNotFoundException, IOException {
        int i = 0;
        BufferedReader in = in(f);
        while (in.readLine() != null) {
            i++;
        }
        return i;
    }

    /**
     * Gets the number of lines of the given file.
     * 
     * @param fAddress - the address of the given file
     * @return the number of lines of the given file
     * @throws UnsupportedEncodingException
     * @throws FileNotFoundException
     * @throws IOException
     */
    public int size(String fAddress) throws UnsupportedEncodingException, FileNotFoundException, IOException {
        return size(fileOf(fAddress));
    }

    /**
     * Loads all data values of the given file.
     * 
     * @param f - the given file
     * @return all data values of the given file
     * @throws IOException
     */
    public String[][] allDataValues(File f) throws IOException {
        BufferedReader in = in(f);
        String[] lines = new String[size(f)];
        for (int i = 0; i < lines.length; i++) {
            lines[i] = in.readLine();
            lines[i] = lines[i].substring(1, lines[i].length() - 1);
        }

        return TextUtil.twoDimensionalize(lines, DELIM);
    }

    /**
     * Loads all data values of the given file.
     * 
     * @param fAddress - the address of the file
     * @return all data values of the given file
     * @throws IOException
     */
    public String[][] allDataValues(String fAddress) throws IOException {
        return allDataValues(fileOf(fAddress));
    }

    /**
     * Saves all data values to the given file.
     * 
     * @param f - the given file
     * @param valuesin - all data values
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void saveData(File f, String[][] valuesin) throws FileNotFoundException, IOException {
        BufferedWriter out = out(f);
        for (int i = 0; i < valuesin.length; i++) {
            out.write("\"" + TextUtil.unsplitStringArray(valuesin[i], DELIM) + "\"");
            out.newLine();
        }
        out.flush();
        out.close();
    }

    /**
     * Saves all data values to the given file.
     * 
     * @param fAddress - the address of the given file
     * @param valuesin - all data values
     * @throws IOException
     */
    public void saveData(String fAddress, String[][] valuesin) throws IOException {
        saveData(fileOf(fAddress), valuesin);
    }

    /**
     * Appends the given data to the given file.
     * 
     * @param f - the given file
     * @param appendix - the given data
     * @throws IOException
     */
    public void appendData(File f, String[] appendix) throws IOException {
        BufferedWriter out = new BufferedWriter(new FileWriter(f, true));
        out.write("\"" + TextUtil.unsplitStringArray(appendix, DELIM) + "\"");
        out.flush();
        out.close();
    }


    /**
     * Appends the given data to the given file.
     * 
     * @param fAddress - the given file
     * @param appendix - the given data
     * @throws IOException
     */
    public void appendData(String fAddress, String[] appendix) throws IOException {
        appendData(fileOf(fAddress), appendix);
    }

}
