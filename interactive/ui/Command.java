package interactive.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import base.Solver;
import base.eq.EquivalenceKeyDescription;
import base.eq.Operation;
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
            keyGen(matcher, cli, true, true);
        }
    },
    KEY_PERSON("keyPerson (.+);(.+)") {
        @Override
        public void execute(MatchResult matcher, CommandLineInterface cli) throws IllegalArgumentException {
            keyGen(matcher, cli, true, false);
        }
    },
    KEY_TASK("keyTask (.+);(.+)") {
        @Override
        public void execute(MatchResult matcher, CommandLineInterface cli) throws IllegalArgumentException {
            keyGen(matcher, cli, false, true);
        }
    },
    KEY_LIST("keyList ((person)|(task))") {
        @Override
        public void execute(MatchResult matcher, CommandLineInterface cli) throws IllegalArgumentException {
            final boolean bPerson = matcher.group(1).equals("person");
            final List<KeyPairFactory> list = bPerson ? 
                    cli.getCore().getPersonKeyPairFactoryList() : cli.getCore().getTaskKeyPairFactoryList();
            String lStr = list.stream().map(x -> x.toString(bPerson)).reduce("", (a,b) -> a + "," + b);
            lStr = lStr.startsWith(",") ? lStr.substring(1) : lStr;
            this.output = "[" + lStr + "]";
        }
    },
    KEY_OF("keyOf ((person)|(task)) (\\d+)") {
        @Override
        public void execute(MatchResult matcher, CommandLineInterface cli) throws IllegalArgumentException {
            final boolean bPerson = matcher.group(1).equals("person");
            try {
                final int index = Integer.parseInt(matcher.group(4));
                final List<KeyPairFactory> list = bPerson ? cli.getCore().getPersonKeyPairFactoryList() : cli.getCore().getTaskKeyPairFactoryList();
                if (index < list.size()) {
                    this.output = list.get(index).toString(bPerson);
                } else {
                    throw new IllegalArgumentException("indexOutOfBounds with index= " + index + " | size= " + list.size());
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("NumberFormatException " + e.getMessage());
            }
        }
    },
    KEY_REUSE("keyReuse ((person)|(task)) (\\d+)") {
        @Override
        public void execute(MatchResult matcher, CommandLineInterface cli) throws IllegalArgumentException {
            final boolean bPerson = matcher.group(1).equals("person");
            try {
                final int index = Integer.parseInt(matcher.group(4));
                final List<KeyPairFactory> list = bPerson ? cli.getCore().getPersonKeyPairFactoryList() : cli.getCore().getTaskKeyPairFactoryList();
                if (index < list.size()) {
                    final KeyPairFactory toAdd = new KeyPairFactory(list.get(index));
                    if (bPerson) {
                        cli.getCore().addPersonKeyPairFactory(toAdd);
                    } else {
                        cli.getCore().addTaskKeyPairFactory(toAdd);
                    }
                    this.output = OK;
                } else {
                    throw new IllegalArgumentException("indexOutOfBounds with index= " + index + " | size= " + list.size());
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("NumberFormatException " + e.getMessage());
            }
        }
    },
    /**
     * Add all contained keys from key factory at second index into factory at first.
     * Afterwards removing key at second index.
     */
    KEY_ADD_ALL("keyAddAll ((person)|(task)) (\\d+) (\\d+)") {
        @Override
        public void execute(MatchResult matcher, CommandLineInterface cli) throws IllegalArgumentException {
            final boolean bPerson = matcher.group(1).equals("person");
            try {
                final int indexTo = Integer.parseInt(matcher.group(4));
                final int indexFrom = Integer.parseInt(matcher.group(5));
                final List<KeyPairFactory> list = bPerson ? cli.getCore().getPersonKeyPairFactoryList() : cli.getCore().getTaskKeyPairFactoryList();
                if (indexTo < list.size() && indexFrom < list.size()) {
                    list.get(indexTo).addKeyPairs(list.get(indexFrom));
                    if (bPerson) {
                        cli.getCore().removePersonKeyPairFactory(indexFrom);
                    } else {
                        cli.getCore().removeTaskKeyPairFactory(indexFrom);
                    }
                    this.output = OK;
                } else {
                    throw new IllegalArgumentException("indexOutOfBounds with index= " + (indexFrom >= list.size() ? indexFrom : indexTo) + " | size= " + list.size());
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("NumberFormatException " + e.getMessage());
            }
        }
    },
    /**
     * Adds a container key at the end of the list with the keys at the given indices.
     * The keys at the given indices get removed from the list.
     * last d is id.
     */
    KEY_CONTAINER("keyContainer ((person)|(task)) ((\\d+)( \\d+)*) ((OR)|(ALTERNATE)|(AND)) (\\d+)") {
        @Override
        public void execute(MatchResult matcher, CommandLineInterface cli) throws IllegalArgumentException {
            final boolean bPerson = matcher.group(1).equals("person");
            final String opStr = matcher.group(7);
            try {
                final int id = Integer.parseInt(matcher.group(11));
                final String indicesStr = matcher.group(4);
                final int[] indices = Arrays.stream(indicesStr.split("\\s")).mapToInt(s -> Integer.parseInt(s)).toArray();
                Arrays.sort(indices); //important for removing!
                final List<KeyPairFactory> list = bPerson ? cli.getCore().getPersonKeyPairFactoryList() : cli.getCore().getTaskKeyPairFactoryList();
                if (Arrays.stream(indices).allMatch(x -> x < list.size())) {
                   final List<KeyPairFactory> listCont = new ArrayList<>();
                   for (final int index : indices) {
                       listCont.add(list.get(index));
                   }
                   final KeyPairFactory container = new KeyPairFactory();
                   listCont.forEach(x -> container.addKeyPairs(x));
                   container.container(id, bPerson, Operation.of(opStr));
                   if (bPerson) {
                       cli.getCore().addPersonKeyPairFactory(container);
                       
                       for (int i = indices.length - 1; i >= 0;i--) {
                           final int index = indices[i];
                           cli.getCore().removePersonKeyPairFactory(index);
                       }
                   } else {
                       cli.getCore().addTaskKeyPairFactory(container);
                       for (final int index : indices) {
                           cli.getCore().removeTaskKeyPairFactory(index);
                       }
                   }
                   this.output = OK;
                } else {
                    final int index = Arrays.stream(indices).filter(x -> x >= list.size()).iterator().nextInt();
                    throw new IllegalArgumentException("indexOutOfBounds with index= " + index + " | size= " + list.size());
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("NumberFormatException " + e.getMessage());
            }
        }
    },
    KEY_REMOVE("keyRemove ((person)|(task)) (\\d+)") {
        @Override
        public void execute(MatchResult matcher, CommandLineInterface cli) throws IllegalArgumentException {
            final boolean bPerson = matcher.group(1).equals("person");
            try {
                final int index = Integer.parseInt(matcher.group(4));
                final List<KeyPairFactory> list = bPerson ? cli.getCore().getPersonKeyPairFactoryList() : cli.getCore().getTaskKeyPairFactoryList();
                if (index < list.size()) {
                    if (bPerson) {
                        cli.getCore().removePersonKeyPairFactory(index);
                    } else {
                        cli.getCore().removeTaskKeyPairFactory(index);
                    }
                    this.output = OK;
                } else {
                    throw new IllegalArgumentException("indexOutOfBounds with index= " + index + " | size= " + list.size());
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("NumberFormatException " + e.getMessage());
            }
        }
    },
   
    COMPLETE_PREP_PERSONS("completePrepPersons ((.+;)*(.+))") {
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
    COMPLETE_PREP_TASKS("completePrepTasks ((.+,\\d+;)*(.+,\\d+))") { 
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
    CLEAR_PREP("clearPrep") {
        @Override
        public void execute(MatchResult matcher, CommandLineInterface cli) throws IllegalArgumentException {
            if (cli.getCore().isStarted()) {
                cli.getCore().clearPreparations();
                this.output = OK;
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
    
    PRINT_TASK("printTaskPerson (.+) ([0-9]+)") {
        @Override
        public void execute(MatchResult matcher, CommandLineInterface cli) throws IllegalArgumentException {
            if(cli.getCore().isStarted()) {
                final String taskName = matcher.group(1);
                final int instanceNum = Integer.parseInt(matcher.group(2));
                this.output = cli.getCore().getPersonOfTaskInstance(taskName, instanceNum).getName();
            } else {
                throw new IllegalArgumentException("not started!");
            }
        }
    },
    PRINT("print") {
        @Override
        public void execute(MatchResult matcher, CommandLineInterface cli) throws IllegalArgumentException {
            if(cli.getCore().isStarted()) {
                this.output = cli.getCore().getPrintResult();
            } else {
                throw new IllegalArgumentException("not started!");
            }
        }   
    },
    
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
    
    protected void keyGen(final MatchResult matcher, final CommandLineInterface cli, final boolean bPerson, final boolean bTask) {
        final EquivalenceKeyDescription eqDescription = EquivalenceKeyDescription.ofArgs(matcher.group(1), DELIM_USR);
        if (eqDescription != null) {
            Object[] init = eqDescription.createInitArgs(matcher.group(2).split(DELIM_USR));
            if (init != null) {
                final EquivalenceKeyDescriptor keyPerson = new EquivalenceKeyDescriptor(eqDescription, init);
                final EquivalenceKeyDescriptor keyTask = new EquivalenceKeyDescriptor(eqDescription, init);
                final KeyPairFactory keyPairFactory = new KeyPairFactory();
                keyPairFactory.generateKeyPair(keyPerson, keyTask);
                if (bPerson) {
                    cli.getCore().addPersonKeyPairFactory(keyPairFactory);
                }
                if (bTask) {
                    cli.getCore().addTaskKeyPairFactory(keyPairFactory);
                }
                this.output = OK;
            } else {
                throw new IllegalArgumentException("object creation failed!");
            }
        } else {
            throw new IllegalArgumentException("not a valid key description!");
        }
    }
}
