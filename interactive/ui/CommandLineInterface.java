package interactive.ui;

import java.io.PrintStream;
import java.util.Scanner;

import interactive.core.InteractiveCore;

public class CommandLineInterface {
    private final Scanner scanner;
    private final PrintStream out;
    private final PrintStream err;
    
    private final InteractiveCore core;
    private boolean quit;
    
    public CommandLineInterface() {
        this.scanner = new Scanner(System.in);
        this.out = System.out;
        this.err = System.err;
        
        this.core = new InteractiveCore();
        this.quit = false;
    }
    
    public void start() {
        while (!this.quit) {
            final String input = this.scanner.nextLine();
            executeCommand(input);
        }
        this.scanner.close();
    }
    
    private void executeCommand(final String input) {
        try {
            final Command command = Command.executeMatching(input, this);
            final String output = command.getOutput();
            if (output != null) {
                this.out.println(output);
            }
        } catch (final IllegalArgumentException exc) {
            this.err.println(exc.getMessage());
        }
    }
    
    protected InteractiveCore getCore() {
        return this.core;
    }
    
    protected void quit() {
        this.quit = true;
    }
}
