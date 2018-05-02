/* *******************************************************
 * Released under the MIT License (MIT) --- see LICENSE
 * Copyright (c) 2014 Ankit Singla, Sangeetha Abdu Jyothi,
 * Chi-Yao Hong, Lucian Popa, P. Brighten Godfrey,
 * Alexandra Kolla, Simon Kassing
 * ******************************************************** */

package ch.ethz.topobench.graph.traffic;

import ch.ethz.topobench.Main;
import ch.ethz.topobench.graph.SelectorResult;
import ch.ethz.topobench.graph.TestGraph;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(MockitoJUnitRunner.class)
public class AllToOneTrafficTest {

    @Test
    public void testTrafficPairsAllToOne() {

        for (int seed = 3; seed < 3563; seed += 17) {

            Main.initGlobalRandomness(seed);

            for (int w = 1; w < 20; w++) {

                TestGraph g = new TestGraph("test", 18);
                g.setNodeWeight(0, 0);
                g.setNodeWeight(1, w);
                g.setNodeWeight(2, 0);
                g.setNodeWeight(3, w);
                g.setNodeWeight(4, w);
                g.setNodeWeight(5, 0);
                g.setNodeWeight(6, w);
                g.setNodeWeight(7, w);
                g.setNodeWeight(8, 0);
                g.setNodeWeight(9, w);
                g.setNodeWeight(10, w);
                g.setNodeWeight(11, 0);
                g.setNodeWeight(12, w);
                g.setNodeWeight(13, 0);
                g.setNodeWeight(14, 0);
                g.setNodeWeight(15, 0);
                g.setNodeWeight(16, w);
                g.setNodeWeight(17, w);

                for (int i = 1; i <= 10; i++) {

                    SelectorResult<Traffic> sel = TrafficSelector.select(
                            TrafficSelector.TrafficMode.ALL_TO_ONE, g,
                            new String[]{}
                    );

                    List<TrafficPair> pairs = sel.getResult().generate();
                    int targetSvr = -1;
                    for (TrafficPair p : pairs) {
                        if (targetSvr == -1) {
                            targetSvr = p.getTo();
                        }
                        assertFalse(p.getFrom() == p.getTo());
                        assertFalse(g.svrToSwitch(p.getFrom()) == g.svrToSwitch(p.getTo()));
                        assertEquals(p.getTo(), targetSvr);
                    }

                }

            }

        }

    }

}
