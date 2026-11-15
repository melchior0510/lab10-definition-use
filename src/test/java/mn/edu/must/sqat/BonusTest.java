package mn.edu.must.sqat;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Stage 1: minimum test set for ALL-DEFS on DataFlowTargets.bonus.
 * Line numbers refer to DataFlowTargets.java.
 *
 * Two tests are needed: the defs of score at line 49 and line 51 can never
 * reach a use in the same run (51 kills 49; the false branch of line 50 skips 51).
 */
class BonusTest {

    @Test
    @DisplayName("T1 bonus({1,0,1},5)=0 : present<threshold (50 false), best<5 (53 false), score def 49 -> use 56, streak def 46 -> use 41")
    void t1_mixed_streak_below_threshold() {
        assertEquals(0, DataFlowTargets.bonus(new int[] {1, 0, 1}, 5));
    }

    @Test
    @DisplayName("T2 bonus({1,1,1,1,1},5)=8 : present>=threshold (50 true), best>=5 (53 true), score def 51 -> use 54, def 54 -> use 56")
    void t2_five_in_a_row_exact_threshold() {
        assertEquals(8, DataFlowTargets.bonus(new int[] {1, 1, 1, 1, 1}, 5));
    }
}
