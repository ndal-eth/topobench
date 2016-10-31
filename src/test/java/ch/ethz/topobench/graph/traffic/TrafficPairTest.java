/* *******************************************************
 * Released under the MIT License (MIT) --- see LICENSE
 * Copyright (c) 2014 Ankit Singla, Sangeetha Abdu Jyothi,
 * Chi-Yao Hong, Lucian Popa, P. Brighten Godfrey,
 * Alexandra Kolla, Simon Kassing
 * ******************************************************** */

package ch.ethz.topobench.graph.traffic;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class TrafficPairTest {

    @Test
    public void testNormal() {

        TrafficPair p1 = new TrafficPair(10, 7);
        TrafficPair p2 = new TrafficPair(9, 13);
        TrafficPair p3 = new TrafficPair(20, 30);
        TrafficPair p4 = new TrafficPair(9, 13);

        // Properties
        assertEquals(10, p1.getFrom());
        assertEquals(7, p1.getTo());
        assertEquals(9, p2.getFrom());
        assertEquals(13, p2.getTo());
        assertEquals(20, p3.getFrom());
        assertEquals(30, p3.getTo());
        assertEquals(9, p4.getFrom());
        assertEquals(13, p4.getTo());

        // Equality
        assertEquals(p1, p1);
        assertEquals(p2, p2);
        assertEquals(p3, p3);
        assertEquals(p4, p4);
        assertEquals(p2, p4);
        assertEquals(p4, p2);

        // In-equalities
        assertFalse(p1.equals(p2));
        assertFalse(p1.equals(p3));
        assertFalse(p1.equals(p4));
        assertFalse(p2.equals(p1));
        assertFalse(p2.equals(p3));
        assertFalse(p3.equals(p1));
        assertFalse(p3.equals(p2));
        assertFalse(p3.equals(p4));
        assertFalse(p4.equals(p1));
        assertFalse(p4.equals(p3));
        assertFalse(p4.equals(null));
        assertFalse(p4.equals(new Object()));

        // Hash codes
        assertEquals(p2.hashCode(), p4.hashCode());

    }

    @Test
    public void testIllegal() {

        boolean thrown = false;
        try {
            new TrafficPair(-1, 0);
        } catch (IllegalArgumentException e) {
            thrown = true;
        }
        assertTrue(thrown);
        thrown = false;

        try {
            new TrafficPair(0, -1);
        } catch (IllegalArgumentException e) {
            thrown = true;
        }
        assertTrue(thrown);
        thrown = false;

        try {
            new TrafficPair(-1, -1);
        } catch (IllegalArgumentException e) {
            thrown = true;
        }
        assertTrue(thrown);

        new TrafficPair(0, 0);

    }

}
