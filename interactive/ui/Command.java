package interactive.ui;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum Command {
    START("start") {
        @Override
        public void execute(MatchResult matcher, CommandLineInterface cli) throws IllegalArgumentException {
            cli.getCore().start();
            this.output = OK;
        }
    },
    //TODO: other commands!
    RESET("reset") {
        @Override
        public void execute(MatchResult matcher, CommandLineInterface cli) throws IllegalArgumentException {
            cli.getCore().reset();
            this.output = OK;
        }
    }, 
    QUIT("quit") {
        @Override
        public void execute(MatchResult matcher, CommandLineInterface cli) throws IllegalArgumentException {
            cli.quit();
        }
    };
    
    private static final String OK = "OK";
    
    private final Pattern pattern;
    
    protected String output;
    
    Command(final String patternStr) {
        this.pattern = Pattern.compile(patternStr);
        this.output = null;
    }
    
    public static Command executeMatching(final String input, final CommandLineInterface cli) throws IllegalArgumentException {
        for (final Command command : Command.values()) {
            final Matcher matcher = command.pattern.matcher(input);
            if (matcher.matches()) {
                command.execute(matcher, cli);
                return command;
            }
        }

        throw new IllegalArgumentException("not a valid command!");
    }
    
    public abstract void execute(MatchResult matcher, CommandLineInterface cli) throws IllegalArgumentException;
    
    public String getOutput() {
        return output;
    }
}
