package org.jojo.util;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Locale;

/**
 * Represents a print stream for printing to a ComponentOutputStream.
 * 
 * @author jojo
 * @version 0.9
 */
public class ComponentPrintStream extends ValidPrintStream {

    /**
     * Creates a new component print stream.
     * 
     * @param out - the ComponentOutputStream to which this stream should print
     */
    public ComponentPrintStream(ComponentOutputStream out) {
        super(out, false);
    }

    @Override
    public void println(String s) {
        print(s + "\n");
    }

    @Override
    public void print(String s) {
        if (s == null) {
            print("null");
        } else {
            for (int i = 0; i < s.length(); i++) {
                try {
                    out.write(s.codePointAt(i));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public PrintStream printf(String format, Object... args) {
        String s = String.format(format, args);
        print(s);
        return this;
    }

    @Override
    public PrintStream printf(Locale l, String format, Object... args) {
        String s = String.format(l, format, args);
        print(s);
        return this;
    }

    @Override
    public PrintStream append(char c) {
        print(c);
        return this;
    }

    @Override
    public PrintStream append(CharSequence csq) {
        print(csq.toString());
        return this;
    }

    @Override
    public PrintStream append(CharSequence csq, int start, int end) {
        print(csq.subSequence(start, end).toString());
        return this;
    }

    @Override
    public PrintStream format(Locale l, String format, Object... args) {
        printf(l, format, args);
        return this;
    }

    @Override
    public PrintStream format(String format, Object... args) {
        printf(format, args);
        return this;
    }

    @Override
    public void print(boolean b) {
        print(Boolean.toString(b));
    }

    @Override
    public void print(char c) {
        print(Character.toString(c));
    }

    @Override
    public void print(char[] s) {
        print(String.copyValueOf(s));
    }

    @Override
    public void print(double d) {
        print(Double.toString(d));
    }

    @Override
    public void print(float f) {
        print(Float.toString(f));
    }

    @Override
    public void print(int i) {
        print(Integer.toString(i));
    }

    @Override
    public void print(long l) {
        print(Long.toString(l));
    }

    @Override
    public void print(Object o) {
        print(String.valueOf(o));
    }

    @Override
    public void println() {
        print("\n");
    }

    @Override
    public void println(boolean b) {
        print(b + "\n");
    }

    @Override
    public void println(char c) {
        print(c + "\n");
    }

    @Override
    public void println(char[] s) {
        print(String.copyValueOf(s) + "\n");
    }

    @Override
    public void println(double d) {
        print(d + "\n");
    }

    @Override
    public void println(float f) {
        print(f + "\n");
    }

    @Override
    public void println(int i) {
        print(i + "\n");
    }

    @Override
    public void println(long l) {
        print(l + "\n");
    }

    @Override
    public void println(Object o) {
        print(String.valueOf(o) + "\n");
    }

}
