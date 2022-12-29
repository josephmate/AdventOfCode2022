package aoc;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

/**
 * <a href="https://adventofcode.com/2022/day/6>Day 6: Tuning Trouble</a>
 */
public class Day6 implements Solution {

    @Override
    public String part1(String input) {
        return String.valueOf(part1Impl(input));
    }

    @Override
    public String part2(String input) {
        return String.valueOf(part2Impl(input));
    }

    private static int solve(String s, int numChars) {
        int[] counts = new int['z' -'a' + 1];
        int oneCount = 0;

        Deque<Integer> buffer = new ArrayDeque<>(numChars+1);
        for (int i = 0; i < s.length(); i++) {
            int idx = s.charAt(i) - 'a';
            counts[idx]++;
            if (counts[idx] == 1) {
                oneCount++;
            } else if (counts[idx] == 2) {
                oneCount--;
            }

            buffer.addLast(idx);

            if (buffer.size() >= numChars+1) {
                int remove = buffer.removeFirst();
                counts[remove]--;
                if (counts[remove] == 1) {
                    oneCount++;
                } else if (counts[remove] == 0) {
                    oneCount--;
                }
            }

            if (oneCount == numChars) {
                return i + 1;
            }
        }

        // not found
        throw new IllegalStateException("Excepted input to have a start sequence");
    }

    private static int part1Impl(String s) {
        return solve(s, 4);
    }

    private static int part2Impl(String s) {
        return solve(s, 14);
    }

}
