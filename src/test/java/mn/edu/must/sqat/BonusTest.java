package mn.edu.must.sqat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests for DataFlowTargets.bonus: T1-T2 satisfy ALL-DEFS, T3-T7 extend to ALL-DU-PAIRS.
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

    // ---- Stage 2: extra tests to reach ALL-DU-PAIRS (IDs D1..D35 match README) ----

    @Test
    @DisplayName("T3 bonus(null,3) throws : attendance def 31 -> p-use 32 true (D1)")
    void t3_null_attendance_throws() {
        assertThrows(IllegalArgumentException.class, () -> DataFlowTargets.bonus(null, 3));
    }

    @Test
    @DisplayName("T4 bonus({1},-1) throws : threshold def 31 -> p-use 32 true (D5)")
    void t4_negative_threshold_throws() {
        assertThrows(IllegalArgumentException.class, () -> DataFlowTargets.bonus(new int[] {1}, -1));
    }

    @Test
    @DisplayName("T5 bonus({},0)=5 : present 35 -> p50 true (D12), best 37 -> p53 false (D26), score 51 -> 56 (D34)")
    void t5_empty_threshold_zero() {
        assertEquals(5, DataFlowTargets.bonus(new int[] {}, 0));
    }

    @Test
    @DisplayName("T6 bonus({},1)=0 : present 35 -> p50 false (D13)")
    void t6_empty_threshold_one() {
        assertEquals(0, DataFlowTargets.bonus(new int[] {}, 1));
    }

    @Test
    @DisplayName("T7 bonus({1,1,1,1,1},6)=3 : score 49 -> 54 (D31)")
    void t7_five_in_a_row_below_threshold() {
        assertEquals(3, DataFlowTargets.bonus(new int[] {1, 1, 1, 1, 1}, 6));
    }

    // ---- Part C: test that kills mutant M2 (best = present) ----

    @Test
    @DisplayName("T8 bonus({1,0,1,0,1,0,1,0,1,1},10)=0 : kills M2, last best update has present=6 but streak=2")
    void t8_broken_streaks_kill_m2() {
        assertEquals(0, DataFlowTargets.bonus(new int[] {1, 0, 1, 0, 1, 0, 1, 0, 1, 1}, 10));
    }
}
