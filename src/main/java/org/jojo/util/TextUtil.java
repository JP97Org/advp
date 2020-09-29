package org.jojo.util;

import java.util.ArrayList;
import static org.apache.commons.text.StringEscapeUtils.escapeHtml4;

/**
 * An utility class for text processing.
 * 
 * @author jojo
 * @version 0.9
 */
public class TextUtil {
    private TextUtil() {
        
    }

    /**
     * Unsplits the given string array by the given delim.
     * 
     * @param toBeUnsplited - the array to be unsplitted
     * @param spliter - the given delim
     * @return the unsplitted string
     */
    public static String unsplitStringArray(String[] toBeUnsplited, String spliter) {
        String ret = "";
        if (spliter.equals("\\n")) {
            spliter = "\n";
        }
        if (toBeUnsplited != null) {
            for (int i = 0; i < toBeUnsplited.length; i++) {
                if (toBeUnsplited[i] != null) {
                    if (i == 0)
                        ret = ret.concat(toBeUnsplited[0]);

                    else
                        ret = ret.concat(spliter).concat(toBeUnsplited[i]);
                }
            }
        }

        return ret;
    }

    /**
     * Unsplits the given string array by the given delims.
     * 
     * @param toBeUnsplited - the array to be unsplitted
     * @param outer - the outer delim
     * @param inner - the inner delim
     * @return the unsplitted string
     */
    public static String unsplitStringArray(String[][] toBeUnsplited, String outer, String inner) {
        String ret = "";

        String[] preret = new String[toBeUnsplited.length];
        for (int i = 0; i < toBeUnsplited.length; i++) {
            preret[i] = unsplitStringArray(toBeUnsplited[i], inner);
        }

        ret = unsplitStringArray(preret, outer);

        return ret;
    }

    /**
     * Two-dimensionalizes the given string array by the given delim.
     * 
     * @param s - the given string array
     * @param spliter - the given delim
     * @return the two dimensional array
     */
    public static String[][] twoDimensionalize(String[] s, String spliter) {
        if (spliter.equals("\\n")) {
            spliter = "\n";
        }

        ArrayList<String[]> al = new ArrayList<String[]>();

        for (int i = 0; i < s.length; i++) {
            al.add(i, s[i].split(spliter));
        }

        String[][] ret = new String[s.length][al.size()];

        for (int i = 0; i < s.length; i++) {
            ret[i] = al.get(i);
        }

        return ret;
    }

    /**
     * Converts the given text to html.
     * 
     * @param text - the given text
     * @return html version of the given text
     */
    public static String toHTML(String text) {
        text = escapeHtml4(text);
        if (text.contains(System.lineSeparator())) {
            text = text.replaceAll(System.lineSeparator(), "<br />");
            text = "<html>" + text + "</html>";
        }
        return text;
    }

    /**
     * Converts the given object array to a string array.
     * 
     * @param o - the given object array
     * @return a string array representing the given object array
     */
    public static String[] toString(Object[] o) {
        String[] ret = new String[o.length];

        for (int i = 0; i < ret.length; i++) {
            ret[i] = o[i].toString();
        }

        return ret;
    }
    
    /**
     * Converts the given object to a string with the given count of characters per line.
     * 
     * @param o - the given object
     * @param charsPerLine - the given count of characters per line
     * @return a string with the given count of characters per line representing the given object
     */
    public static String toStringWithLineFeeds(Object o, int charsPerLine) {
        StringBuilder ret = new StringBuilder();
        final String in = o.toString();
        for (int i = 0;i < in.length();i++) {
            ret.append(in.charAt(i));
            if (i % charsPerLine == 0 && i > 0) {
                ret.append(System.lineSeparator());
            }
        }
        return ret.toString();
    }
}
