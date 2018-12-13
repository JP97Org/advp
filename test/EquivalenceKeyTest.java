package test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.*;

import base.EquivalenceKey;
import base.eq.*;

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
    
    @After
    public void clear() {
        ekOne = null;
        ekTwo = null;
    }
}
