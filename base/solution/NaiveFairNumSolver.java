package base.solution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import base.Person;

public class NaiveFairNumSolver extends FairNumSolver {
    private int randomizeLvl;
    
    public NaiveFairNumSolver() {
        super();
        this.randomizeLvl = 0;
    }
    
    public NaiveFairNumSolver(final boolean computePartialSolutionWhenSolvingNotPossible, final int randomizeLvl) {
        super(computePartialSolutionWhenSolvingNotPossible);
        if(randomizeLvl < 0) throw new IllegalArgumentException("randomizeLvl must be >= 0");
        this.randomizeLvl = randomizeLvl;
    }

    @Override
    protected Person getNextPerson(final Map<Person, Integer> map, final Set<Person> alreadyTried) {
        //TODO: optimize
        int min = Integer.MAX_VALUE;
        List<Person> pMin = new ArrayList<>();
        
        for(Entry<Person, Integer> entry : map.entrySet()) {
            final Person person = entry.getKey();
            final int num = entry.getValue();
            if (!alreadyTried.contains(person) && num <= min) {
                if(num < min) {
                    min = num;
                    pMin.clear();
                }
                pMin.add(person);
            }
        }
        for(int i = 0;i < this.randomizeLvl;i++) {
            Collections.shuffle(pMin);
        }
        return pMin.isEmpty() ? null : pMin.get(0);
    }
}
