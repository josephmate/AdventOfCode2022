package aoc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * --- Day 9: Rope Bridge ---
 *
 * This rope bridge creaks as you walk along it. You aren't sure how old it is, or whether it can
 * even support your weight.
 *
 * It seems to support the Elves just fine, though. The bridge spans a gorge which was carved out
 * by the massive river far below you.
 *
 * You step carefully; as you do, the ropes stretch and twist. You decide to distract yourself by
 * modeling rope physics; maybe you can even figure out where not to step.
 *
 * Consider a rope with a knot at each end; these knots mark the head and the tail of the rope.
 * If the head moves far enough away from the tail, the tail is pulled toward the head.
 *
 * Due to nebulous reasoning involving Planck lengths, you should be able to model the positions
 * of the knots on a two-dimensional grid. Then, by following a hypothetical series of motions
 * (your puzzle input) for the head, you can determine how the tail will move.
 *
 * Due to the aforementioned Planck lengths, the rope must be quite short; in fact,
 * the head (H) and tail (T) must always be touching (diagonally adjacent and even overlapping
 * both count as touching):
 *
 * ....
 * .TH.
 * ....
 *
 * ....
 * .H..
 * ..T.
 * ....
 *
 * ...
 * .H. (H covers T)
 * ...
 *
 * If the head is ever two steps directly up, down, left, or right from the tail, the tail
 * must also move one step in that direction so it remains close enough:
 *
 * .....    .....    .....
 * .TH.. -> .T.H. -> ..TH.
 * .....    .....    .....
 *
 * ...    ...    ...
 * .T.    .T.    ...
 * .H. -> ... -> .T.
 * ...    .H.    .H.
 * ...    ...    ...
 *
 * Otherwise, if the head and tail aren't touching and aren't in the same row or column,
 * the tail always moves one step diagonally to keep up:
 *
 * .....    .....    .....
 * .....    ..H..    ..H..
 * ..H.. -> ..... -> ..T..
 * .T...    .T...    .....
 * .....    .....    .....
 *
 * .....    .....    .....
 * .....    .....    .....
 * ..H.. -> ...H. -> ..TH.
 * .T...    .T...    .....
 * .....    .....    .....
 *
 * You just need to work out where the tail goes as the head follows a series of motions.
 * Assume the head and the tail both start at the same position, overlapping.
 *
 * For example:
 *
 * R 4
 * U 4
 * L 3
 * D 1
 * R 4
 * D 1
 * L 5
 * R 2
 *
 * This series of motions moves the head right four steps, then up four steps,
 * then left three steps, then down one step, and so on.
 * After each step, you'll need to update the position of the tail if the step means
 * the head is no longer adjacent to the tail. Visually, these motions occur as follows
 * (s marks the starting position as a reference point):
 *
 * == Initial State ==
 *
 * ......
 * ......
 * ......
 * ......
 * H.....  (H covers T, s)
 *
 * == R 4 ==
 *
 * ......
 * ......
 * ......
 * ......
 * TH....  (T covers s)
 *
 * ......
 * ......
 * ......
 * ......
 * sTH...
 *
 * ......
 * ......
 * ......
 * ......
 * s.TH..
 *
 * ......
 * ......
 * ......
 * ......
 * s..TH.
 *
 * == U 4 ==
 *
 * ......
 * ......
 * ......
 * ....H.
 * s..T..
 *
 * ......
 * ......
 * ....H.
 * ....T.
 * s.....
 *
 * ......
 * ....H.
 * ....T.
 * ......
 * s.....
 *
 * ....H.
 * ....T.
 * ......
 * ......
 * s.....
 *
 * == L 3 ==
 *
 * ...H..
 * ....T.
 * ......
 * ......
 * s.....
 *
 * ..HT..
 * ......
 * ......
 * ......
 * s.....
 *
 * .HT...
 * ......
 * ......
 * ......
 * s.....
 *
 * == D 1 ==
 *
 * ..T...
 * .H....
 * ......
 * ......
 * s.....
 *
 * == R 4 ==
 *
 * ..T...
 * ..H...
 * ......
 * ......
 * s.....
 *
 * ..T...
 * ...H..
 * ......
 * ......
 * s.....
 *
 * ......
 * ...TH.
 * ......
 * ......
 * s.....
 *
 * ......
 * ....TH
 * ......
 * ......
 * s.....
 *
 * == D 1 ==
 *
 * ......
 * ....T.
 * .....H
 * ......
 * s.....
 *
 * == L 5 ==
 *
 * ......
 * ....T.
 * ....H.
 * ......
 * s.....
 *
 * ......
 * ....T.
 * ...H..
 * ......
 * s.....
 *
 * ......
 * ......
 * ..HT..
 * ......
 * s.....
 *
 * ......
 * ......
 * .HT...
 * ......
 * s.....
 *
 * ......
 * ......
 * HT....
 * ......
 * s.....
 *
 * == R 2 ==
 *
 * ......
 * ......
 * .H....  (H covers T)
 * ......
 * s.....
 *
 * ......
 * ......
 * .TH...
 * ......
 * s.....
 *
 * After simulating the rope, you can count up all of the positions the tail visited at least once.
 * In this diagram, s again marks the starting position (which the tail also visited)
 * and # marks other positions the tail visited:
 *
 * ..##..
 * ...##.
 * .####.
 * ....#.
 * s###..
 *
 * So, there are 13 positions the tail visited at least once.
 *
 * Simulate your complete hypothetical series of motions. How many positions does the tail of the
 * rope visit at least once?
 *
 * That's not the right answer; your answer is too low. If you're stuck, make sure you're using
 * the full input data; there are also some general tips on the about page, or you can ask for
 * hints on the subreddit. Please wait one minute before trying again. (You guessed 5390.)
 *
 * --- Part Two ---
 *
 * A rope snaps! Suddenly, the river is getting a lot closer than you remember. The bridge is still
 * there, but some of the ropes that broke are now whipping toward you as you fall through the air!
 *
 * The ropes are moving too quickly to grab; you only have a few seconds to choose how to arch your
 * body to avoid being hit. Fortunately, your simulation can be extended to support longer ropes.
 *
 * Rather than two knots, you now must simulate a rope consisting of ten knots. One knot is still
 * the head of the rope and moves according to the series of motions. Each knot further down the
 * rope follows the knot in front of it using the same rules as before.
 *
 * Using the same series of motions as the above example, but with the knots marked H, 1, 2, ..., 9,
 * the motions now occur as follows:
 *
 * == Initial State ==
 *
 * ......
 * ......
 * ......
 * ......
 * H.....  (H covers 1, 2, 3, 4, 5, 6, 7, 8, 9, s)
 *
 * == R 4 ==
 *
 * ......
 * ......
 * ......
 * ......
 * 1H....  (1 covers 2, 3, 4, 5, 6, 7, 8, 9, s)
 *
 * ......
 * ......
 * ......
 * ......
 * 21H...  (2 covers 3, 4, 5, 6, 7, 8, 9, s)
 *
 * ......
 * ......
 * ......
 * ......
 * 321H..  (3 covers 4, 5, 6, 7, 8, 9, s)
 *
 * ......
 * ......
 * ......
 * ......
 * 4321H.  (4 covers 5, 6, 7, 8, 9, s)
 *
 * == U 4 ==
 *
 * ......
 * ......
 * ......
 * ....H.
 * 4321..  (4 covers 5, 6, 7, 8, 9, s)
 *
 * ......
 * ......
 * ....H.
 * .4321.
 * 5.....  (5 covers 6, 7, 8, 9, s)
 *
 * ......
 * ....H.
 * ....1.
 * .432..
 * 5.....  (5 covers 6, 7, 8, 9, s)
 *
 * ....H.
 * ....1.
 * ..432.
 * .5....
 * 6.....  (6 covers 7, 8, 9, s)
 *
 * == L 3 ==
 *
 * ...H..
 * ....1.
 * ..432.
 * .5....
 * 6.....  (6 covers 7, 8, 9, s)
 *
 * ..H1..
 * ...2..
 * ..43..
 * .5....
 * 6.....  (6 covers 7, 8, 9, s)
 *
 * .H1...
 * ...2..
 * ..43..
 * .5....
 * 6.....  (6 covers 7, 8, 9, s)
 *
 * == D 1 ==
 *
 * ..1...
 * .H.2..
 * ..43..
 * .5....
 * 6.....  (6 covers 7, 8, 9, s)
 *
 * == R 4 ==
 *
 * ..1...
 * ..H2..
 * ..43..
 * .5....
 * 6.....  (6 covers 7, 8, 9, s)
 *
 * ..1...
 * ...H..  (H covers 2)
 * ..43..
 * .5....
 * 6.....  (6 covers 7, 8, 9, s)
 *
 * ......
 * ...1H.  (1 covers 2)
 * ..43..
 * .5....
 * 6.....  (6 covers 7, 8, 9, s)
 *
 * ......
 * ...21H
 * ..43..
 * .5....
 * 6.....  (6 covers 7, 8, 9, s)
 *
 * == D 1 ==
 *
 * ......
 * ...21.
 * ..43.H
 * .5....
 * 6.....  (6 covers 7, 8, 9, s)
 *
 * == L 5 ==
 *
 * ......
 * ...21.
 * ..43H.
 * .5....
 * 6.....  (6 covers 7, 8, 9, s)
 *
 * ......
 * ...21.
 * ..4H..  (H covers 3)
 * .5....
 * 6.....  (6 covers 7, 8, 9, s)
 *
 * ......
 * ...2..
 * ..H1..  (H covers 4; 1 covers 3)
 * .5....
 * 6.....  (6 covers 7, 8, 9, s)
 *
 * ......
 * ...2..
 * .H13..  (1 covers 4)
 * .5....
 * 6.....  (6 covers 7, 8, 9, s)
 *
 * ......
 * ......
 * H123..  (2 covers 4)
 * .5....
 * 6.....  (6 covers 7, 8, 9, s)
 *
 * == R 2 ==
 *
 * ......
 * ......
 * .H23..  (H covers 1; 2 covers 4)
 * .5....
 * 6.....  (6 covers 7, 8, 9, s)
 *
 * ......
 * ......
 * .1H3..  (H covers 2, 4)
 * .5....
 * 6.....  (6 covers 7, 8, 9, s)
 *
 * Now, you need to keep track of the positions the new tail, 9, visits. In this example, the tail
 * never moves, and so it only visits 1 position. However, be careful: more types of motion are
 * possible than before, so you might want to visually compare your simulated rope to the one above.
 *
 * Here's a larger example:
 *
 * R 5
 * U 8
 * L 8
 * D 3
 * R 17
 * D 10
 * L 25
 * U 20
 *
 * These motions occur as follows (individual steps are not shown):
 *
 * == Initial State ==
 *
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ...........H..............  (H covers 1, 2, 3, 4, 5, 6, 7, 8, 9, s)
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 *
 * == R 5 ==
 *
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ...........54321H.........  (5 covers 6, 7, 8, 9, s)
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 *
 * == U 8 ==
 *
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ................H.........
 * ................1.........
 * ................2.........
 * ................3.........
 * ...............54.........
 * ..............6...........
 * .............7............
 * ............8.............
 * ...........9..............  (9 covers s)
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 *
 * == L 8 ==
 *
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ........H1234.............
 * ............5.............
 * ............6.............
 * ............7.............
 * ............8.............
 * ............9.............
 * ..........................
 * ..........................
 * ...........s..............
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 *
 * == D 3 ==
 *
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * .........2345.............
 * ........1...6.............
 * ........H...7.............
 * ............8.............
 * ............9.............
 * ..........................
 * ..........................
 * ...........s..............
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 *
 * == R 17 ==
 *
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ................987654321H
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ...........s..............
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 *
 * == D 10 ==
 *
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ...........s.........98765
 * .........................4
 * .........................3
 * .........................2
 * .........................1
 * .........................H
 *
 * == L 25 ==
 *
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ...........s..............
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * H123456789................
 *
 * == U 20 ==
 *
 * H.........................
 * 1.........................
 * 2.........................
 * 3.........................
 * 4.........................
 * 5.........................
 * 6.........................
 * 7.........................
 * 8.........................
 * 9.........................
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ...........s..............
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 *
 * Now, the tail (9) visits 36 positions (including s) at least once:
 *
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * ..........................
 * #.........................
 * #.............###.........
 * #............#...#........
 * .#..........#.....#.......
 * ..#..........#.....#......
 * ...#........#.......#.....
 * ....#......s.........#....
 * .....#..............#.....
 * ......#............#......
 * .......#..........#.......
 * ........#........#........
 * .........########.........
 *
 * Simulate your complete series of motions on a larger rope with ten knots. How many positions does the tail of the rope visit at least once?
 *
 */
public class Day9 implements Solution {

    private static final boolean DEBUG = false;

    record Instruction(char direction, int numPositions) {

    }

    record Coord(int r, int c) {

    }

    @Override
    public String part1(String input) {
        return String.valueOf(part1Impl(input));
    }

    @Override
    public String part2(String input) {
        return String.valueOf(part2Impl(input));
    }

    private static Instruction parse(String line) {
        String[] cols = line.split(" ");
        return new Instruction(cols[0].charAt(0), Integer.parseInt(cols[1]));
    }

    private static boolean withinOne(
        Coord head,
        Coord tail
    ) {
        int diffR = head.r - tail.r;
        int diffC = head.c - tail.c;

        return -1 <= diffR && diffR <= 1
            && -1 <= diffC && diffC <= 1;
    }

    private static int manDistance(
        Coord head,
        Coord tail) {

        int diffR = Math.abs(head.r - tail.r);
        int diffC = Math.abs(head.c - tail.c);

        return diffR + diffC;
    }

    private static boolean shouldMoveDiagonal(
        Coord head,
        Coord tail) {
        return manDistance(head, tail) >= 3;
    }

    private static void printBoardState(List<Coord> knotPositions, int ropeLength) {
        Map<Coord, Character> toPrint = new HashMap<>();
        toPrint.put(knotPositions.get(0), 'H');
        for (int i = 1; i <= ropeLength -2; i++) {
            toPrint.put(knotPositions.get(i), (char)(i + '0'));
        }
        toPrint.put(knotPositions.get(ropeLength-1), 'T');
        toPrint.put(new Coord(0,0), 's');
        printMap(toPrint);
    }

    private static void printMap(Map<Coord, Character> toPrint) {
        if (!DEBUG) {
            return;
        }
        int minR = Integer.MAX_VALUE;
        int minC = Integer.MAX_VALUE;
        int maxR = Integer.MIN_VALUE;
        int maxC = Integer.MIN_VALUE;
        for (Coord coord : toPrint.keySet()) {
            if (coord.r < minR) {
                minR = coord.r;
            }
            if (coord.r > maxR) {
                maxR = coord.r;
            }
            if (coord.c < minC) {
                minC = coord.c;
            }
            if (coord.c > maxC) {
                maxC = coord.c;
            }
        }

        for (int r = minR; r <= maxR; r++) {
            for(int c = minC; c <= maxC; c++) {
                Coord coord = new Coord(r,c);
                if (toPrint.containsKey(coord)) {
                    System.out.print(toPrint.get(coord));
                } else {
                    System.out.print('·');
                }
            }
            System.out.println();
        }
    }

    private static void printVisited(Set<Coord> visited) {
        Map<Coord, Character> toPrint = new HashMap<>();
        for (Coord coord : visited) {
            toPrint.put(coord, '#');
        }
        toPrint.put(new Coord(0,0), 's');
        printMap(toPrint);
    }

    private static int simulate(String s, int ropeLength) {
        List<Coord> knotPositions = new ArrayList<>(10);
        for (int i = 0; i < ropeLength; i++) {
            knotPositions.add(new Coord(0,0));
        }
        Set<Coord> visited = new HashSet<>();
        visited.add(new Coord(0,0));

        Iterator<Instruction> instructions = s.lines()
            .map(Day9::parse)
            .iterator();
        while (instructions.hasNext()) {
            Instruction instruction = instructions.next();
            if (DEBUG) {
                System.out.println("===" + instruction + "===");
            }
            for(int i = 0; i < instruction.numPositions; i++) {
                int headR = knotPositions.get(0).r;
                int headC = knotPositions.get(0).c;
                // move the head based on the instruction
                //  * R 4
                // * U 4
                // * L 3
                // * D 1
                switch (instruction.direction) {
                    case 'R':
                        headC++;
                        break;
                    case 'L':
                        headC--;
                        break;
                    case 'U':
                        headR--;
                        break;
                    case 'D':
                        headR++;
                        break;
                }
                knotPositions.set(0, new Coord(headR, headC));

                for(int knot = 1; knot < ropeLength; knot++) {
                    Coord prev = knotPositions.get(knot-1);
                    Coord current = knotPositions.get(knot);
                    if (withinOne(prev, current)) {
                        break; // no movement for this knot and the rest
                    }

                    // move the tail to approach the head if necessary
                    if (shouldMoveDiagonal(prev, current)) {
                        /*   T1T
                            T121T
                              H
                            T121T
                             T1T
                         */
                        int minDist = Integer.MAX_VALUE;
                        Coord minDistCoord = null;
                        int[] vals = {-1, 1};
                        for (int j : vals) {
                            for (int k : vals) {
                                var potentialCoord = new Coord(current.r + j, current.c + k);
                                var dist = manDistance(prev, potentialCoord);
                                if (dist < minDist) {
                                    minDist = dist;
                                    minDistCoord = potentialCoord;
                                }
                            }
                        }
                        knotPositions.set(knot, minDistCoord);
                    } else {
                        int minDist = Integer.MAX_VALUE;
                        Coord minDistCoord = null;
                        List<Coord> potentialCoords = List.of(
                            new Coord(current.r, current.c + 1), // right
                            new Coord(current.r, current.c - 1), // left
                            new Coord(current.r- 1, current.c),  // up
                            new Coord(current.r + 1, current.c)  // down
                        );
                        for (Coord potentialCoord : potentialCoords) {
                            var dist = manDistance(prev, potentialCoord);
                            if (dist < minDist) {
                                minDist = dist;
                                minDistCoord = potentialCoord;
                            }
                        }


                        knotPositions.set(knot, minDistCoord);
                    }
                }
                visited.add(new Coord(
                    knotPositions.get(ropeLength - 1).r,
                    knotPositions.get(ropeLength - 1).c));
                printBoardState(knotPositions, ropeLength);
                if (DEBUG) {
                    System.out.println("=======");
                }
            }
        }

        if (DEBUG) {
            System.out.println("=======");
        }
        printVisited(visited);

        return visited.size();
    }

    private static int part1Impl(String s) {
        return simulate(s, 2);
    }

    private static long part2Impl(String s) {
        return simulate(s, 10);
    }

}
