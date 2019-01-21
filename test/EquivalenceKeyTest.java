package test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiPredicate;

import org.junit.*;

import base.EquivalenceKey;
import base.eq.*;
import base.factory.EquivalenceKeyDescriptor;

public class EquivalenceKeyTest {
    private EquivalenceKey ekOne;
    private EquivalenceKey ekTwo;
    
    @Before
    public void setUp() {
        //is done in tests
    }
    
    @Test
    public void age() {
        final Comparison comp = Comparison.GR;
        ekOne = new AgeEquivalenceKey(21, comp);
        ekTwo = new AgeEquivalenceKey(18, comp.anti());
        assertTrue(ekOne.isEquivalent(ekTwo));
        assertTrue(ekTwo.isEquivalent(ekOne));
    }
    
    @Test
    public void comp() {
        final Comparison comp = Comparison.SMEQ;
        ekOne = new ComparisonEquivalenceKey<String>(1,"A",comp);
        ekTwo = new ComparisonEquivalenceKey<String>(1,"B",comp.anti());
        assertTrue(ekOne.isEquivalent(ekTwo));
        assertTrue(ekTwo.isEquivalent(ekOne));
        ekTwo = new ComparisonEquivalenceKey<Integer>(1,1,comp.anti());
        assertFalse(ekOne.isEquivalent(ekTwo));
        assertFalse(ekTwo.isEquivalent(ekOne));
    }
    
    //cont, eq, gender, TEK tested in natz instance
    
    @Test
    public void lambda() {
        ekOne = new LambdaEquivalenceKey<Integer,StringBuilder>(0, 0, (x,y) -> x == Integer.valueOf(y.toString()), StringBuilder.class);
        final StringBuilder sb = new StringBuilder("0");
        ekTwo = new LambdaEquivalenceKey<Integer,StringBuilder>(sb , 0, (x,y) -> x == Integer.valueOf(y.toString()), Integer.class);
        assertTrue(ekOne.isEquivalent(ekTwo));
        assertTrue(ekTwo.isEquivalent(ekOne));
        sb.append("1");
        assertFalse(ekOne.isEquivalent(ekTwo));
        assertFalse(ekTwo.isEquivalent(ekOne));
        ekTwo = new LambdaEquivalenceKey<Integer,String>("", 0, (x,y) -> true, Integer.class);
        assertFalse(ekOne.isEquivalent(ekTwo));
        assertFalse(ekTwo.isEquivalent(ekOne));
        ekOne = new LambdaEquivalenceKey<Number,Number>(0, Integer.valueOf(0), (x,y) -> x.doubleValue() == y.doubleValue(), Number.class);
        ekTwo = new LambdaEquivalenceKey<Number,Number>(Double.valueOf(0.0), 0, (x,y) -> x.doubleValue() == y.doubleValue(), Number.class);
        assertTrue(ekOne.isEquivalent(ekTwo));
        assertTrue(ekTwo.isEquivalent(ekOne));
        ekTwo = new LambdaEquivalenceKey<Number,List<Number>>(new ArrayList<Number>(), 0, (x,y) -> y.isEmpty(), Number.class);
        assertFalse(ekOne.isEquivalent(ekTwo));
        assertFalse(ekTwo.isEquivalent(ekOne));
        ekOne = new LambdaEquivalenceKey<Number,List<String>>(0, 0, (x,y) -> y.isEmpty(), List.class);
        assertTrue(ekOne.isEquivalent(ekTwo)); //wg. lazy-eval ok :/
        assertTrue(ekTwo.isEquivalent(ekOne));
    }
    
    @Test
    public void creationPerDescriptorTest() {
        for (final EquivalenceKeyDescription desc : EquivalenceKeyDescription.values()) {
            final List<EquivalenceKey> list = new ArrayList<>();
            list.add(new AgeEquivalenceKey(21, Comparison.GR));
            BiPredicate<String, String> predicate = (x,y) -> x.length() == y.length();
            
            final Set<TimeInterval> timeIntervals = new HashSet<>();
            final TimeInterval timeInterval = new TimeInterval(Calendar.getInstance().getTime(), Calendar.getInstance().getTime());
            timeIntervals.add(timeInterval);
            
            EquivalenceKeyDescriptor eqDesc;
            switch (desc) {
            case AGE: eqDesc = new EquivalenceKeyDescriptor(desc, 21, Comparison.GR); break;
            case COMPARISON: eqDesc = new EquivalenceKeyDescriptor(desc, 1, "a", Comparison.GR); break;
            case CONTAINER: eqDesc = new EquivalenceKeyDescriptor(desc, 1, list, Operation.ALTERNATE); break;
            case EQUAL: eqDesc = new EquivalenceKeyDescriptor(desc, 1, "b"); break;
            case LAMBDA_PERSON: eqDesc = new EquivalenceKeyDescriptor(desc, 1, "a", predicate , String.class); break;
            case LAMBDA_TASK: eqDesc = new EquivalenceKeyDescriptor(desc, "a", 1, predicate , String.class); break;
            case TIME_PERSON: eqDesc = new EquivalenceKeyDescriptor(desc, timeIntervals); break;
            case TIME_TASK: eqDesc = new EquivalenceKeyDescriptor(desc, timeInterval); break;
            default: System.err.println("Some untested!"); return;
            }
            ekOne = eqDesc.getKey();
            System.out.println(ekOne);
        }
    }
    
    @After
    public void clear() {
        ekOne = null;
        ekTwo = null;
    }
}
