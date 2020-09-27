package org.jojo.util;

import java.awt.TextArea;
import java.util.ArrayList;

import javax.swing.JTextArea;

public class TextUtil {
    private TextUtil() {
        
    }

    public static int[] getTextPositions(String toBeSearched, String seekThis) {
        int[] ret = null;

        char[] tbs = toBeSearched.toCharArray();
        char[] st = seekThis.toCharArray();

        boolean[] right = new boolean[st.length];
        if (tbs != null && st != null && tbs.length != 0 && st.length != 0) {
            outer: for (int i = 0, x = 0; i <= tbs.length;) {

                if (allRightBoolArrayToBoolean(right)) {
                    ret = new int[2];
                    ret[0] = i - st.length;
                    ret[1] = i;
                    break;
                }

                else if (i < tbs.length) {
                    for (; x < st.length;) {
                        if (st[x] == tbs[i]) {
                            right[x] = true;
                            x++;
                            i++;
                            continue outer;
                        } else {
                            x = 0;
                            i++;
                            continue outer;
                        }
                    }
                }
            }
        }

        return ret;
    }

    public static int[] getTextPositions(String toBeSearched, String seekThis, int position) {
        int[] ret = null;

        char[] tbs = toBeSearched.toCharArray();
        char[] st = seekThis.toCharArray();

        boolean[] right = new boolean[st.length];

        if (tbs != null && st != null && tbs.length != 0 && st.length != 0) {
            outer: for (int x = 0; position <= tbs.length;) {

                if (allRightBoolArrayToBoolean(right)) {
                    ret = new int[2];
                    ret[0] = position - st.length;
                    ret[1] = position;
                    break;
                }

                else if (position < tbs.length) {
                    for (; x < st.length;) {
                        if (st[x] == tbs[position]) {
                            right[x] = true;
                            x++;
                            position++;
                            continue outer;
                        } else if (position != tbs.length - 1) {
                            x = 0;
                            position++;
                            continue outer;
                        } else {
                            x = 0;
                            position = 0;
                            continue outer;
                        }

                    }
                }
            }
        }
        return ret;
    }

    public static String getEntry(String[] array, String PartOfEntry) {
        String ret = null;

        for (int i = 0; i < array.length; i++) {
            if (array[i].contains(PartOfEntry)) {
                ret = array[i];
                break;
            }
        }

        return ret;
    }

    public static String getEntry(String[] array, String PartOfEntry, int position) {
        String ret = null;

        for (int i = position; i < array.length; i++) {
            if (array[i].contains(PartOfEntry)) {
                ret = array[i];
                break;
            }
        }

        return ret;
    }

    public static String getEntry(String[] array, String PartOfEntry, int position, TextArea prep) {
        String ret = null;

        for (; position < array.length; position++) {
            if (array[position].contains(PartOfEntry)) {
                ret = array[position];
                prep.setText(array[position]);
                prep.select(getTextPositions(array[position], PartOfEntry)[0],
                        getTextPositions(array[position], PartOfEntry)[1]);
                break;
            }
        }

        return ret;
    }

    public static String getEntry(String[] array, String PartOfEntry, int position, JTextArea prep) {
        String ret = null;

        for (; position < array.length; position++) {
            if (array[position].contains(PartOfEntry)) {
                ret = array[position];
                prep.setText(array[position]);
                prep.select(getTextPositions(array[position], PartOfEntry)[0],
                        getTextPositions(array[position], PartOfEntry)[1]);
                break;
            }
        }

        return ret;
    }

    public static int getEntryNumber(String[] array, String entry, int position) {
        int ret = Integer.MAX_VALUE;

        for (int i = position; i < array.length; i++) {
            if (array[i].equals(entry)) {
                ret = i;
                break;
            }
        }

        return ret;
    }

    public static int getEntryCount(String[] array, String PartOfEntry) {
        int ret = 0;

        for (int position = 0; position < array.length; position++) {
            if (array[position].contains(PartOfEntry)) {
                ret = ret + 1;
            }
        }

        return ret;
    }

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

    public static String unsplitStringArray(String[][] toBeUnsplited, String outer, String inner) {
        String ret = "";

        String[] preret = new String[toBeUnsplited.length];
        for (int i = 0; i < toBeUnsplited.length; i++) {
            preret[i] = unsplitStringArray(toBeUnsplited[i], inner);
        }

        ret = unsplitStringArray(preret, outer);

        return ret;
    }

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

    private static boolean allRightBoolArrayToBoolean(boolean[] b) {
        boolean ret = false;

        for (int i = 0; i < b.length; i++) {
            if (b[i]) {
                ret = true;
            } else {
                ret = false;
                break;
            }
        }

        return ret;
    }

    public static String toHTML(String text) {
        if (text.contains(System.lineSeparator())) {
            text = text.replaceAll(System.lineSeparator(), "<br />");
            text = "<html>" + text + "</html>";
        }
        return text;
    }

    public static String[] toString(Object[] o) {
        String[] ret = new String[o.length];

        for (int i = 0; i < ret.length; i++) {
            ret[i] = o[i].toString();
        }

        return ret;
    }
    
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
