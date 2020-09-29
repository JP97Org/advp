package org.jojo.advp.interactive.ui;

/**
 * The main class of the command line interface of ADVP.
 * 
 * @author jojo
 * @version 0.9
 */
public class Main {

    /**
     * Starts the CLI.
     * @param args - not used
     */
    public static void main(final String[] args) {
        final CommandLineInterface cli = new CommandLineInterface();
        cli.start();
    }

}
