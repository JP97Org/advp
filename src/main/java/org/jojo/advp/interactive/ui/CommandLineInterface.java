package org.jojo.advp.interactive.ui;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import org.jojo.advp.interactive.core.InteractiveCore;

public class CommandLineInterface {
    private final Scanner scanner;
    private final PrintStream out;
    private final PrintStream err;
    
    private final InteractiveCore core;
    private boolean quit;
    
    private boolean isSavingHistory;
    private final List<String> commands;
    
    public CommandLineInterface() {
        this(System.in, System.out, System.err);
    }
    
    public CommandLineInterface(final InputStream inStream, final PrintStream outStream, final PrintStream errStream) {
        this.scanner = new Scanner(Objects.requireNonNull(inStream));
        this.out = Objects.requireNonNull(outStream);
        this.err = Objects.requireNonNull(errStream);
        
        this.core = new InteractiveCore();
        this.quit = false;
        
        this.isSavingHistory = true;
        this.commands = new ArrayList<>();
    }
    
    public void start() {
        while (!this.quit) {
            final String input = this.scanner.nextLine();
            executeCommand(input);
        }
        this.scanner.close();
    }
    
    public void executeCommand(final String input) {
        if (isSavingHistory()) {
            this.commands.add(input);
        }
        try {
            final Command command = Command.executeMatching(input, this);
            final String output = command.getOutput();
            if (output != null) {
                this.out.println(output);
            }
        } catch (final IllegalArgumentException|UnsupportedOperationException exc) {
            this.err.println(exc.getMessage());
        }
    }
    
    public boolean isSavingHistory() {
        return this.isSavingHistory;
    }
    
    public void setIsSavingHistory(final boolean isSavingHistory) {
        this.isSavingHistory = isSavingHistory;
    }
    
    public void clearHistory() {
        this.commands.clear();
    }
    
    public List<String> getHistory() {
        return new ArrayList<>(this.commands);
    }

    public InteractiveCore getCore() {
        return this.core;
    }
    
    protected void quit() {
        this.quit = true;
    }
}
