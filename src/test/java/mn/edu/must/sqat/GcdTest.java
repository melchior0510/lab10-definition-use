package mn.edu.must.sqat;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * all-DU-pairs coverage for DataFlowTargets.gcd.
 * Line numbers refer to DataFlowTargets.java:
 *   13 def x,y | 15 while (p-use y) | 16 tmp = x % y | 17 x = y | 18 y = tmp | 20 return x
 *
 * Notation in DisplayName:  var<defLine>-<useLine>
 *   p15T = p-use of y at line 15, true branch  (enter loop, edge 15->16)
 *   p15F = p-use of y at line 15, false branch (exit loop,  edge 15->20)
 *
 * Pair IDs (P1..P13) match the table in README.md.
 */
class GcdTest {

    @Test
    @DisplayName("T1 gcd(12,8)=4 : x13-16 x17-16 x17-20 y13-p15T y13-16 y13-17 y18-p15T y18-p15F y18-16 y18-17 tmp16-18")
    void t1_gcd_12_8_twoIterations() {
        // iteration 1: x=12,y=8 -> tmp=4, x=8, y=4
        // iteration 2: x=8, y=4 -> tmp=0, x=4, y=0 -> exit
        assertEquals(4, DataFlowTargets.gcd(12, 8));
    }

    @Test
    @DisplayName("T2 gcd(7,0)=7 : x13-20 y13-p15F (zero iterations, ONLY these two pairs)")
    void t2_gcd_7_0_zeroIterations() {
        assertEquals(7, DataFlowTargets.gcd(7, 0));
    }

    // ---- extra tests (not required for all-DU-pairs, but useful) ----

    @Test
    @DisplayName("T3 gcd(0,5)=5 : one iteration, x13-16 y13-p15T y13-16 y13-17 tmp16-18 y18-p15F x17-20")
    void t3_gcd_0_5_oneIteration() {
        assertEquals(5, DataFlowTargets.gcd(0, 5));
    }

    @Test
    @DisplayName("T4 gcd(48,18)=6 : three iterations")
    void t4_gcd_48_18_threeIterations() {
        assertEquals(6, DataFlowTargets.gcd(48, 18));
    }
}
