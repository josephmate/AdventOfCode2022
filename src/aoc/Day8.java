package aoc;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <a href="https://adventofcode.com/2022/day/8>Day 8: Treetop Tree House</a>
 */
public class Day8 implements Solution {

    @Override
    public String part1(String input) {
        return String.valueOf(part1Impl(input));
    }

    @Override
    public String part2(String input) {
        return String.valueOf(part2Impl(input));
    }

    private static int part1Impl(String s) {
        //System.out.println("s.length(): " + s.length()); 9900
        List<String> lines = s.lines().collect(Collectors.toList());
        int R = lines.size();
        int C = lines.get(0).length();
        boolean [][] visible = new boolean[R][C];

        for(int r = 0; r < R; r++) {
            // from left to right
            int maxSoFar = -1;
            for (int c = 0; c < C; c++) {
                int height = lines.get(r).charAt(c) - '0';
                if (height > maxSoFar) {
                    maxSoFar = height;
                    visible[r][c] = true;
                }
            }


            // from right to left
            maxSoFar = -1;
            for (int c = C-1; c >= 0; c--) {
                int height = lines.get(r).charAt(c) - '0';
                if (height > maxSoFar) {
                    maxSoFar = height;
                    visible[r][c] = true;
                }
            }
        }

        for(int c = 0; c < C; c++) {
            // from top to bottom
            int maxSoFar = -1;
            for (int r = 0; r < R; r++) {
                int height = lines.get(r).charAt(c) - '0';
                if (height > maxSoFar) {
                    maxSoFar = height;
                    visible[r][c] = true;
                }
            }


            // from bottom to top
            maxSoFar = -1;
            for (int r = R-1; r >= 0; r--) {
                int height = lines.get(r).charAt(c) - '0';
                if (height > maxSoFar) {
                    maxSoFar = height;
                    visible[r][c] = true;
                }
            }
        }


        int visibleCount = 0;
        for (boolean[] arr : visible) {
            for (boolean b : arr) {
                if (b) {
                    visibleCount++;
                }
            }
        }
        return visibleCount;
    }

    private static long part2Impl(String s) {
        List<String> lines = s.lines().collect(Collectors.toList());
        int R = lines.size();
        int C = lines.get(0).length();
        boolean [][] visible = new boolean[R][C];

        long maxScore = 0;
        for(int r = 0; r < R; r++) {
            for (int c = 0; c < C; c++) {
                long score = 1;

                // to the left
                int treeCount = 0;
                int currentHeight = lines.get(r).charAt(c) - '0';
                for (int i = c - 1; i >= 0; i--) {
                    treeCount++;
                    if (currentHeight <= lines.get(r).charAt(i) - '0') {
                        break;
                    }
                }
                score = score * treeCount;

                // to the right
                treeCount = 0;
                currentHeight = lines.get(r).charAt(c) - '0';
                for (int i = c + 1; i < C; i++) {
                    treeCount++;
                    if (currentHeight <= lines.get(r).charAt(i) - '0') {
                        break;
                    }
                }
                score = score * treeCount;

                // up
                treeCount = 0;
                currentHeight = lines.get(r).charAt(c) - '0';
                for (int i = r - 1; i >= 0; i--) {
                    treeCount++;
                    if (currentHeight <= lines.get(i).charAt(c) - '0') {
                        break;
                    }
                }
                score = score * treeCount;

                // down
                treeCount = 0;
                currentHeight = lines.get(r).charAt(c) - '0';
                for (int i = r + 1; i < R; i++) {
                    treeCount++;
                    if (currentHeight <= lines.get(i).charAt(c) - '0') {
                        break;
                    }
                }
                score = score * treeCount;


                if (score > maxScore) {
                    maxScore = score;
                }
            }


        }

        return maxScore;
    }

}
