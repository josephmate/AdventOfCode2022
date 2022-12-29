package aoc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <a href="https://adventofcode.com/2022/day/14>Day 14: Regolith Reservoir</a>
 */
public class Day14 implements Solution {

    private static final boolean DEBUG = false;


    @Override
    public String part1(String input) {
        return String.valueOf(part1Impl(input));
    }

    @Override
    public String part2(String input) {
        return String.valueOf(part2Impl(input));
    }

    record Coord(int r, int c) {

    }

    private static Coord parseCoord(String s) {
        String[] cols = s.split(",");
        return new Coord(
            Integer.parseInt(cols[1]),
            Integer.parseInt(cols[0])
        );
    }

    private static Map<Coord, Character> parse(String s) {
        Map<Coord, Character> result = new HashMap<>();

        for(String line : s.lines().collect(Collectors.toList())) {
            /*
             * 498,4 -> 498,6 -> 496,6
             * 503,4 -> 502,4 -> 502,9 -> 494,9
             *
             *   4     5  5
             *   9     0  0
             *   4     0  3
             * 0 ......+...
             * 1 ..........
             * 2 ..........
             * 3 ..........
             * 4 ....#...##
             * 5 ....#...#.
             * 6 ..###...#.
             * 7 ........#.
             * 8 ........#.
             * 9 #########.
             */
            String[] coordStrings = line.split(" -> ");
            Coord prevCoord = parseCoord(coordStrings[0]);
            for (int i = 1; i < coordStrings.length; i++) {
                Coord nextCoord = parseCoord((coordStrings[i]));
                if (prevCoord.r == nextCoord.r) {
                    // horizontal
                    int startC = Math.min(prevCoord.c, nextCoord.c);
                    int endC = Math.max(prevCoord.c, nextCoord.c);
                    int r = prevCoord.r;
                    for(int c = startC; c <= endC; c++) {
                        result.put(new Coord(r,c), '#');
                    }
                } else if (prevCoord.c == nextCoord.c) {
                    // vertical
                    int startR = Math.min(prevCoord.r, nextCoord.r);
                    int endR = Math.max(prevCoord.r, nextCoord.r);
                    int c = prevCoord.c;
                    for(int r = startR; r <= endR; r++) {
                        result.put(new Coord(r,c), '#');
                    }
                } else {
                    throw new IllegalStateException(line + " has a diagonal line");
                }
                prevCoord = nextCoord;
            }
        }

        return result;
    }

    private static void printMap(Map<Coord,Character> map) {
        if (!DEBUG) {
            return;
        }
        int minR = 0;
        int maxR = map.keySet().stream()
            .mapToInt(Coord::r)
            .max()
            .orElseThrow();
        int minC = map.keySet().stream()
            .mapToInt(Coord::c)
            .min()
            .orElseThrow();
        int maxC = map.keySet().stream()
            .mapToInt(Coord::c)
            .max()
            .orElseThrow();

        System.out.println();
        for (int r = minR; r <= maxR; r++) {
            for (int c = minC; c <= maxC; c++) {
                Coord coord = new Coord(r,c);
                Character ch = map.get(coord);
                if (ch == null) {
                    if (r == 0 && c == 500) {
                        System.out.print("+");
                    } else {
                        System.out.print(".");
                    }
                } else {
                    System.out.print(ch);
                }
            }
            System.out.println();
        }
    }

    private static long part1Impl(String s) {
        Map<Coord, Character> map = parse(s);
        printMap(map);

        int maxR = map.keySet().stream()
            .mapToInt(Coord::r)
            .max()
            .orElseThrow();

        int amountOfSand = 0;
        boolean inbounds = true;
        while(inbounds) {
            /*
             * A unit of sand always falls down one step if possible. If the tile immediately below is blocked
             * (by rock or sand), the unit of sand attempts to instead move diagonally one step down and to the
             * left. If that tile is blocked, the unit of sand attempts to instead move diagonally one step down
             * and to the right. Sand keeps moving as long as it is able to do so, at each step trying to move
             * down, then down-left, then down-right. If all three possible destinations are blocked, the unit
             * of sand comes to rest and no longer moves, at which point the next unit of sand is created back
             * at the source.
             */
            Coord sand = new Coord(0, 500);
            while (true) {
                Coord down = new Coord(sand.r + 1, sand.c);
                Coord downLeft = new Coord(sand.r + 1, sand.c - 1);
                Coord downRight = new Coord(sand.r + 1, sand.c + 1);
                if (down.r > maxR) {
                    // fell off
                    inbounds = false;
                    break;
                } else if (!map.containsKey(down)) {
                    sand = down;
                } else if (!map.containsKey(downLeft)) {
                    sand = downLeft;
                } else if (!map.containsKey(downRight)) {
                    sand = downRight;
                } else {
                    // at rest
                    map.put(sand, 'o');
                    break;
                }
            }
            if (inbounds) {
                amountOfSand++;
            }
        }

        printMap(map);

        return amountOfSand;
    }


    private static long part2Impl(String s) {
        Map<Coord, Character> map = parse(s);
        printMap(map);

        int maxR = map.keySet().stream()
            .mapToInt(Coord::r)
            .max()
            .orElseThrow();

        int amountOfSand = 0;
        boolean inbounds = true;
        while(inbounds) {
            /*
             * A unit of sand always falls down one step if possible. If the tile immediately below is blocked
             * (by rock or sand), the unit of sand attempts to instead move diagonally one step down and to the
             * left. If that tile is blocked, the unit of sand attempts to instead move diagonally one step down
             * and to the right. Sand keeps moving as long as it is able to do so, at each step trying to move
             * down, then down-left, then down-right. If all three possible destinations are blocked, the unit
             * of sand comes to rest and no longer moves, at which point the next unit of sand is created back
             * at the source.
             */
            Coord sand = new Coord(0, 500);
            while (true) {
                Coord down = new Coord(sand.r + 1, sand.c);
                Coord downLeft = new Coord(sand.r + 1, sand.c - 1);
                Coord downRight = new Coord(sand.r + 1, sand.c + 1);
                if (!map.containsKey(down) && down.r < maxR + 2) {
                    sand = down;
                } else if (!map.containsKey(downLeft) && down.r < maxR + 2) {
                    sand = downLeft;
                } else if (!map.containsKey(downRight) && down.r < maxR + 2) {
                    sand = downRight;
                } else {
                    // at rest
                    map.put(sand, 'o');
                    if (sand.r == 0 && sand.c == 500) {
                        inbounds = false;
                    }
                    break;
                }
            }
            amountOfSand++;
        }

        printMap(map);

        return amountOfSand;
    }


    public static void main(String[] args) throws IOException {

    }
}
