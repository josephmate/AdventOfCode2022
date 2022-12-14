package aoc;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * --- Day 12: Hill Climbing Algorithm ---
 *
 * You try contacting the Elves using your handheld device, but the river you're following must be
 * too low to get a decent signal.
 *
 * You ask the device for a heightmap of the surrounding area (your puzzle input). The heightmap
 * shows the local area from above broken into a grid; the elevation of each square of the grid is
 * given by a single lowercase letter, where a is the lowest elevation, b is the next-lowest, and
 * so on up to the highest elevation, z.
 *
 * Also included on the heightmap are marks for your current position (S) and the location that
 * should get the best signal (E). Your current position (S) has elevation a, and the location that
 * should get the best signal (E) has elevation z.
 *
 * You'd like to reach E, but to save energy, you should do it in as few steps as possible.
 * During each step, you can move exactly one square up, down, left, or right. To avoid needing
 * to get out your climbing gear, the elevation of the destination square can be at most one higher
 * than the elevation of your current square; that is, if your current elevation is m, you could step
 * to elevation n, but not to elevation o. (This also means that the elevation of the destination
 * square can be much lower than the elevation of your current square.)
 *
 * For example:
 *
 * Sabqponm
 * abcryxxl
 * accszExk
 * acctuvwj
 * abdefghi
 *
 * Here, you start in the top-left corner; your goal is near the middle. You could start by moving
 * down or right, but eventually you'll need to head toward the e at the bottom. From there, you
 * can spiral around to the goal:
 *
 * v..v<<<<
 * >v.vv<<^
 * .>vv>E^^
 * ..v>>>^^
 * ..>>>>>^
 *
 * In the above diagram, the symbols indicate whether the path exits each square
 * moving up (^), down (v), left (<), or right (>).
 * The location that should get the best signal is still E, and . marks unvisited squares.
 *
 * This path reaches the goal in 31 steps, the fewest possible.
 *
 * What is the fewest steps required to move from your current position to the location that
 * should get the best signal?
 *
 * --- Part Two ---
 *
 * As you walk up the hill, you suspect that the Elves will want to turn this into a hiking trail.
 * The beginning isn't very scenic, though; perhaps you can find a better starting point.
 *
 * To maximize exercise while hiking, the trail should start as low as possible: elevation a.
 * The goal is still the square marked E. However, the trail should still be direct,
 * taking the fewest steps to reach its goal. So, you'll need to find the shortest path from
 * any square at elevation a to the square marked E.
 *
 * Again consider the example from above:
 *
 * Sabqponm
 * abcryxxl
 * accszExk
 * acctuvwj
 * abdefghi
 *
 * Now, there are six choices for starting position (five marked a, plus the square marked S
 * that counts as being at elevation a). If you start at the bottom-left square, you can reach
 * the goal most quickly:
 *
 * ...v<<<<
 * ...vv<<^
 * ...v>E^^
 * .>v>>>^^
 * >^>>>>>^
 *
 * This path reaches the goal in only 29 steps, the fewest possible.
 *
 * What is the fewest steps required to move starting from any square with elevation a to the
 * location that should get the best signal?
 */
public class Day12 implements Solution {

    private static final boolean DEBUG = false;


    @Override
    public String part1(String input) {
        return String.valueOf(part1Impl(input));
    }

    @Override
    public String part2(String input) {
        return String.valueOf(part2Impl(input));
    }

    record Path(Coord cord, int cost, Path prev) {

    }

    record Coord(int r, int c) {

    }

    private static int toHeight(char c) {
        return switch (c) {
            case 'S' -> 0;
            case 'E' -> 'z' - 'a';
            default -> c -'a';
        };
    }

    private static char[][] toMap(String input) {
        String[] map = input.trim().split("\n");
        int R = map.length;
        int C = map[0].length();
        char[][] result = new char[R][C];
        for (int r= 0; r < R; r++) {
            for (int c= 0; c < C; c++) {
                result[r][c] = map[r].charAt(c);
            }
        }
        return result;
    }

    private static Coord findStart(char[][] map) {
        int R = map.length;
        int C = map[0].length;
        for (int r= 0; r < R; r++) {
            for (int c= 0; c < C; c++) {
                if (map[r][c] == 'S') {
                    return new Coord(r,c);
                }
            }
        }
        throw new IllegalStateException("Did not find S");
    }

    private static Stream<Coord> generateMoves(
        Coord coord
    ) {
        // moving up (^), down (v), left (<), or right (>)

        return Stream.of(
            new Coord(coord.r-1, coord.c), // up
            new Coord(coord.r+1, coord.c), // down
            new Coord(coord.r, coord.c+1), // right
            new Coord(coord.r, coord.c-1) // left
            );
    }

    private static void printPath(Path path, char[][] map) {
        if (path == null) {
            return;
        }
        printPath(path.prev, map);
        System.out.println(path.cord + " " + path.cost + " -> " + map[path.cord.r][path.cord.c]);
    }

    private static int solve (
        char[][] map,
        Deque<Path> queue,
        Set<Coord> visited
    ) {
        int R = map.length;
        int C = map[0].length;

        Path lastPathProcessed = null;

        while (!queue.isEmpty()) {
            Path path = queue.removeFirst();
            lastPathProcessed = path;
            int newCost = path.cost + 1;
            // check height;
            final int currentHeight = toHeight(map[path.cord.r][path.cord.c]);

            List<Path> newGoodPaths = generateMoves(path.cord)
                .filter(potentialCoord -> potentialCoord.r >= 0)
                .filter(potentialCoord -> potentialCoord.c >= 0)
                .filter(potentialCoord -> potentialCoord.r < R)
                .filter(potentialCoord -> potentialCoord.c < C)
                .filter(potentialCoord -> !visited.contains(potentialCoord))
                .filter(potentialCoord -> currentHeight - toHeight(map[potentialCoord.r][potentialCoord.c]) >= -1 )
                .map(goodCoord -> new Path(goodCoord, newCost, path))
                .collect(Collectors.toList());

            for (Path newGoodPath : newGoodPaths) {
                if (map[newGoodPath.cord.r][newGoodPath.cord.c] == 'E') {
                    return newGoodPath.cost;
                }
                visited.add(newGoodPath.cord);
                queue.addLast(newGoodPath);
            }
        }

        printPath(lastPathProcessed, map);

        throw new IllegalStateException("not found");
    }

    private static int part1Impl(String s) {
        char[][] map = toMap(s);
        Coord start = findStart(map);
        Set<Coord> visited = new HashSet<>();
        visited.add(start);
        Deque<Path> queue = new ArrayDeque<>();
        queue.addLast(new Path(start, 0, null));

        return solve(map, queue, visited);
    }


    private static long part2Impl(String s) {

        char[][] map = toMap(s);
        int R = map.length;
        int C = map[0].length;
        Set<Coord> visited = new HashSet<>();
        Deque<Path> queue = new ArrayDeque<>();
        for(int r = 0; r < R; r++) {
            for (int c = 0; c < C; c++) {
                if (map[r][c] == 'a' || map[r][c] == 'S') {
                    Coord start = new Coord(r,c);
                    visited.add(start);
                    queue.addLast(new Path(start, 0, null));
                }
            }
        }

        return solve(map, queue, visited);
    }


    public static void main(String[] args) throws IOException {
        // to the right
        System.out.println(part1Impl("SbcdefghijklmnopqrstuvwxyE"));
        // same height
        System.out.println(part1Impl("SbcdefghijklmnopqrstuvwxyyE"));
        System.out.println(part1Impl("SbcdefghijklmnopqrstuvwxyzE"));
        // to the left
        System.out.println(part1Impl("EyxwvutsrqponmlkjihgfedcbS"));
        // down
        System.out.println(part1Impl("""
            S
            b
            c
            d
            e
            f
            g
            h
            i
            j
            k
            l
            m
            n
            o
            p
            q
            r
            s
            t
            u
            v
            w
            x
            y
            E
            """));
        // up
        System.out.println(part1Impl("""
            E
            y
            x
            w
            v
            u
            t
            s
            r
            q
            p
            o
            n
            m
            l
            k
            j
            i
            h
            g
            f
            e
            d
            c
            b
            S
            """));
        // multiple paths
        System.out.println(part1Impl("""
            Sbcdefghijklmnopqrstuvwxy
            bcdefghijklmnopqrstuvwxyE
            """));
        // down then up again
        System.out.println(part1Impl("""
            SbcdefghijklmnopqrstutuvwxyE
            """));
    }
}
