package interactive.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import base.Solver;
import base.eq.EquivalenceKeyDescription;
import base.factory.EquivalenceKeyDescriptor;
import base.factory.KeyPairFactory;
import base.factory.TaskDescriptor;
import base.solution.NaiveFairNumSolver;
import base.solution.NaiveSolver;

public enum Command {
    START("start") {
        @Override
        public void execute(MatchResult matcher, CommandLineInterface cli) throws IllegalArgumentException {
            cli.getCore().start();
            this.output = OK;
        }
    },
    /**
     * Symmetric single key creation (one key pair) and symmetric adding at the end of the respective list.
     */
    KEY_SYM("keySym (.+);(.+)") {
        @Override
        public void execute(MatchResult matcher, CommandLineInterface cli) throws IllegalArgumentException {
            final EquivalenceKeyDescription eqDescription = EquivalenceKeyDescription.ofArgs(matcher.group(1), DELIM_USR);
            if (eqDescription != null) {
                Object[] init = createInitArgs(eqDescription, matcher.group(2).split(DELIM_USR));
                if (init != null) {
                    final EquivalenceKeyDescriptor keyPerson = new EquivalenceKeyDescriptor(eqDescription, init);
                    final EquivalenceKeyDescriptor keyTask = new EquivalenceKeyDescriptor(eqDescription, init);
                    final KeyPairFactory keyPairFactory = new KeyPairFactory();
                    keyPairFactory.generateKeyPair(keyPerson, keyTask);
                    cli.getCore().addPersonKeyPairFactory(keyPairFactory);
                    cli.getCore().addTaskKeyPairFactory(keyPairFactory);
                } else {
                    throw new IllegalArgumentException("object creation failed!");
                }
            } else {
                throw new IllegalArgumentException("not a valid key description!");
            }
        }

        private Object[] createInitArgs(final EquivalenceKeyDescription eqDescription, String[] argsStr) {
            return eqDescription.createInitArgs(argsStr);
        }
    },
    
    //TODO: assymetric key creation, i.e. only person key adding and only task key adding
    //TODO: output for both lists
    //TODO: key reusing with factory copying
    //TODO: (assymetric) key editing (adding new keyPairs to exising single key pair factories)
    //TODO: removing one or more key pair factory (in both lists (assymetric))
    
    COMPLETE_PREP_PERSONS("completePrepPersons ((.+;)*(.+){0,1})") {
        @Override
        public void execute(MatchResult matcher, CommandLineInterface cli) throws IllegalArgumentException {
            if (cli.getCore().isStarted()) {
                final List<String> names = Arrays.asList(matcher.group(1).split(DELIM));
                if (cli.getCore().completePersonsCreation(names)) {
                    this.output = OK;
                } else {
                    throw new IllegalArgumentException("completion failed!");
                }
            } else {
                throw new IllegalArgumentException("not started!");
            }
        }
    },
    COMPLETE_PREP_TASKS("completePrepTasks ((.+,d+;)*(.+,d+){0,1})") {
        @Override
        public void execute(MatchResult matcher, CommandLineInterface cli) throws IllegalArgumentException {
            if (cli.getCore().isStarted()) {
                final String[] outer = matcher.group(1).split(DELIM);
                final List<TaskDescriptor> descList = new ArrayList<>(outer.length);
                for (final String dStr : outer) {
                    final String[] inner = dStr.split(DELIM_IN);
                    final String name = inner[0];
                    int num = 0;
                    try {
                        num = Integer.parseInt(inner[1]); 
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("NumberFormatException " + e.getMessage());
                    }
                    descList.add(new TaskDescriptor(name, num));
                }
                if (cli.getCore().completeTasksCreation(descList)) {
                    this.output = OK;
                } else {
                    throw new IllegalArgumentException("completion failed!");
                }
            } else {
                throw new IllegalArgumentException("not started!");
            }
        }
    },
    SET_SOLVER("setSolver ((naive)|(fairNum))(( part)|( full)){0,1}( \\d+){0,1}") {
        @Override
        public void execute(MatchResult matcher, CommandLineInterface cli) throws IllegalArgumentException {
            if (cli.getCore().isStarted()) {
                final String cmd = matcher.group(0);
                final String solverDesc = matcher.group(1);
                final boolean part = cmd.matches(".*(( part)|( full)).*") ? (cmd.matches(".*part.*") ? true : false) : false;
                int rand = 0;
                try {
                    rand = cmd.matches(".*\\d+") ? Integer.parseInt(cmd.replaceAll("[^0-9]", "")) : 0;
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("NumberFormatException " + e.getMessage());
                }
                final Solver solver = solverDesc.equals("naive") ? new NaiveSolver(part) : new NaiveFairNumSolver(part, rand);
                cli.getCore().setSolver(solver);
                this.output = OK; 
            } else {
                throw new IllegalArgumentException("not started!");
            }
        }
    },
    //TODO: maybe more solvers as soon as there are more
    SOLVE("solve") {
        @Override
        public void execute(MatchResult matcher, CommandLineInterface cli) throws IllegalArgumentException {
            if (cli.getCore().hasSolver()) {
                if (cli.getCore().solve()) {
                    this.output = OK; 
                } else {
                    this.output = "not completely solved :(";
                }
            } else {
                throw new IllegalArgumentException("solver not set!");
            }
        }
    },
    //TODO: output des ergebnis (einzelausgabe)
    //TODO: output des ergebnis als Uebersicht
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
    
    private static final String DELIM = ";";
    private static final String DELIM_IN = ",";
    private static final String DELIM_USR = "\\|";
    
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
