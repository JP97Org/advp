package org.jojo.util;

import java.io.PrintStream;
import java.util.Locale;

public class BufferedPrintStream extends ValidPrintStream {

    private boolean doBuffer;
    private StringBuilder buf;
    private PrintStream ps;

    public BufferedPrintStream(ValidPrintStream out) {
        super(out);
        ps = out;
        doBuffer = false;
        buf = new StringBuilder();
    }

    public BufferedPrintStream(PrintStream out) {
        super(out);
        ps = out;
        doBuffer = false;
        buf = new StringBuilder();
    }

    public void begin() {
        if (!doesBuffer()) {
            doBuffer(true);
            buf = new StringBuilder();
        } else
            System.err.println("PrintStream buffers already!");
    }

    @Override
    public void println(String s) {
        print(s + "\n");
    }

    @Override
    public void print(String s) {
        if (doesBuffer()) {
            buf.append(s);
        } else {
            ps.print(s);
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

    public boolean doesBuffer() {
        return doBuffer;
    }

    private void doBuffer(boolean doBuffer) {
        this.doBuffer = doBuffer;
    }

    public void flush() {
        buf = null;
        super.flush();
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

    public void end() {
        if (buf != null)
            ps.print(buf);
        doBuffer(false);
        flush();
        close();
    }

    public void suspend() {
        if (buf != null)
            ps.print(buf);
        doBuffer(false);
        buf = null;
    }

    public String getBuffer() {
        return buf.toString();
    }

}
