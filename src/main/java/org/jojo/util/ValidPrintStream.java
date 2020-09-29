package org.jojo.util;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Locale;

/**
 * Represents a valid print stream, i.e. a print stream which overrides all public methods defined in {@link PrintStream}.
 * 
 * @author jojo
 * @version 0.9
 */
public abstract class ValidPrintStream extends PrintStream {
    
    /**
     * Constructor of a valid print stream with an underlying output stream and an auto flush setting.
     * 
     * @param out - the underlying output stream
     * @param autoFlush - A boolean; if true, the output buffer will be flushed whenever a byte array is written, one of the println methods is invoked, or a newline character or byte ('\n') is written
     */
    public ValidPrintStream(OutputStream out, boolean autoFlush) {
        super(out, autoFlush);
    }

    /**
     * Constructor of a valid print stream with an underlying output stream.
     * 
     * @param out - the underlying output stream
     */
    public ValidPrintStream(OutputStream out) {
        super(out);
    }

    @Override
    public abstract PrintStream append(char c);

    @Override
    public abstract PrintStream append(CharSequence csq);

    @Override
    public abstract PrintStream append(CharSequence csq, int start, int end);

    @Override
    public abstract PrintStream format(Locale l, String format, Object... args);

    @Override
    public abstract PrintStream format(String format, Object... args);

    @Override
    public abstract void print(boolean b);

    @Override
    public abstract void print(char c);

    @Override
    public abstract void print(char[] s);

    @Override
    public abstract void print(double d);

    @Override
    public abstract void print(float f);

    @Override
    public abstract void print(int i);

    @Override
    public abstract void print(long l);

    @Override
    public abstract void print(Object o);

    @Override
    public abstract void print(String s);

    @Override
    public abstract PrintStream printf(Locale l, String format, Object... args);

    @Override
    public abstract PrintStream printf(String format, Object... args);

    @Override
    public abstract void println();

    @Override
    public abstract void println(boolean b);

    @Override
    public abstract void println(char c);

    @Override
    public abstract void println(char[] s);

    @Override
    public abstract void println(double d);

    @Override
    public abstract void println(float f);

    @Override
    public abstract void println(int i);

    @Override
    public abstract void println(long l);

    @Override
    public abstract void println(Object o);

    @Override
    public abstract void println(String s);
}
