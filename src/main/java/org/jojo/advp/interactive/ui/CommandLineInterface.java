package org.jojo.advp.interactive.ui;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import org.jojo.advp.interactive.core.InteractiveCore;

/**
 * Represents a command line argument.
 * 
 * @author jojo
 * @version 0.9
 */
public class CommandLineInterface {
    private final Scanner scanner;
    private final PrintStream out;
    private final PrintStream err;
    
    private final InteractiveCore core;
    private boolean quit;
    
    private boolean isSavingHistory;
    private final List<String> commands;
    
    /**
     * Creates a new command line interface for the System console.
     */
    public CommandLineInterface() {
        this(System.in, System.out, System.err);
    }
    
    /**
     * Creates a new command line interface for the given streams
     * 
     * @param inStream - the input stream
     * @param outStream - the output stream
     * @param errStream - the error stream
     */
    public CommandLineInterface(final InputStream inStream, final PrintStream outStream, final PrintStream errStream) {
        this.scanner = new Scanner(Objects.requireNonNull(inStream));
        this.out = Objects.requireNonNull(outStream);
        this.err = Objects.requireNonNull(errStream);
        
        this.core = new InteractiveCore();
        this.quit = false;
        
        this.isSavingHistory = true;
        this.commands = new ArrayList<>();
    }
    
    /**
     * Starts the command line interface, accepting and executing commands until the interface is quit.
     */
    public void start() {
        while (!this.quit) {
            final String input = this.scanner.nextLine();
            executeCommand(input);
        }
        this.scanner.close();
    }
    
    /**
     * Executes one command.
     * 
     * @param input - the input representing the command and its arguments
     */
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
    
    /**
     * 
     * @return whether this CLI saves a command history
     */
    public boolean isSavingHistory() {
        return this.isSavingHistory;
    }
    
    /**
     * Sets whether this CLI saves a command history.
     * 
     * @param isSavingHistory - whether this CLI should save a command history
     */
    public void setIsSavingHistory(final boolean isSavingHistory) {
        this.isSavingHistory = isSavingHistory;
    }
    
    /**
     * Clears the command history of this CLI.
     */
    public void clearHistory() {
        this.commands.clear();
    }
    
    /**
     * 
     * @return the command history of this CLI
     */
    public List<String> getHistory() {
        return new ArrayList<>(this.commands);
    }

    /**
     * 
     * @return the interactive core connecting this CLI to the ADVP base API
     */
    public InteractiveCore getCore() {
        return this.core;
    }
    
    /**
     * Quits this CLI.
     */
    protected void quit() {
        this.quit = true;
    }
}
