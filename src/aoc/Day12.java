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
 * <a href="https://adventofcode.com/2022/day/12>Day 12: Hill Climbing Algorithm</a>
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
