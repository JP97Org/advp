package org.jojo.util;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Locale;

public abstract class ValidPrintStream extends PrintStream {
    public ValidPrintStream(OutputStream out, boolean autoFlush) {
        super(out, autoFlush);
    }

    public ValidPrintStream(OutputStream out) {
        super(out);
    }

    public abstract PrintStream append(char c);

    public abstract PrintStream append(CharSequence csq);

    public abstract PrintStream append(CharSequence csq, int start, int end);

    public abstract PrintStream format(Locale l, String format, Object... args);

    public abstract PrintStream format(String format, Object... args);

    public abstract void print(boolean b);

    public abstract void print(char c);

    public abstract void print(char[] s);

    public abstract void print(double d);

    public abstract void print(float f);

    public abstract void print(int i);

    public abstract void print(long l);

    public abstract void print(Object o);

    public abstract void print(String s);

    public abstract PrintStream printf(Locale l, String format, Object... args);

    public abstract PrintStream printf(String format, Object... args);

    public abstract void println();

    public abstract void println(boolean b);

    public abstract void println(char c);

    public abstract void println(char[] s);

    public abstract void println(double d);

    public abstract void println(float f);

    public abstract void println(int i);

    public abstract void println(long l);

    public abstract void println(Object o);

    public abstract void println(String s);

}
