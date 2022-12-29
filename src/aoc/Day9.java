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
 * <a href="https://adventofcode.com/2022/day/9>Day 9: Rope Bridge</a>
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
