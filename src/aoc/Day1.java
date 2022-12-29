package aoc;

import java.util.Arrays;

/**
 * <a href="https://adventofcode.com/2022/day/1>Day 1: Calorie Counting</a>
 */
public class Day1 implements Solution {

    @Override
    public String part1(String input) {
        return String.valueOf(part1Impl(input));
    }

    @Override
    public String part2(String input) {
        return String.valueOf(part2Impl(input));
    }

    private static int part1Impl(String s) {
        var elves = s.split("\n\n");
        return Arrays.stream(elves)
            .mapToInt((var elf) -> elf.lines()
                .mapToInt(Integer::parseInt)
                .sum()
            )
            .max()
            .orElseThrow();
    }

    private static int part2Impl(String s) {
        final var elves = s.split("\n\n");
        return Arrays.stream(elves)
            .mapToInt((var elf) -> elf.lines()
                .mapToInt(Integer::parseInt)
                .sum()
            )
            .sorted()
            .skip(elves.length-3)
            .sum();
    }

}
