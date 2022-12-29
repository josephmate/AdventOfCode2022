package aoc;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <a href="https://adventofcode.com/2022/day/3>Day 3: Rucksack Reorganization</a>
 */
public class Day3 implements Solution {

    @Override
    public String part1(String input) {
        return String.valueOf(part1Impl(input));
    }

    @Override
    public String part2(String input) {
        return String.valueOf(part2Impl(input));
    }

    private static Set<Character> toSet(char[] chars) {
        Set<Character> result = new HashSet<>();
        for(char c : chars) {
            result.add(c);
        }
        return result;
    }

    /**
     *     Lowercase item types a through z have priorities 1 through 26.
     *     Uppercase item types A through Z have priorities 27 through 52.
     */
    private static int prioritize(char c) {
        if (c >= 'a' && c <= 'z') {
            return c - 'a' + 1;
        } else {
            return c - 'A' + 27;
        }
    }

    private static int part1Impl(String s) {
        return s.lines()
            .map(rucksack -> List.of(
                rucksack.substring(0, rucksack.length()/2),
                rucksack.substring(rucksack.length()/2)
            ))
            .map(compartments -> List.of(
                toSet(compartments.get(0).toCharArray()),
                toSet(compartments.get(1).toCharArray())
            ))
            .map(sets -> sets.get(0).stream().filter(sets.get(1)::contains).findFirst().orElseThrow())
            .mapToInt(Day3::prioritize)
            .sum();
    }


    private static int part2Impl(String s) {
        String[] rucksacks = s.split("\n");
        int i = 0;
        int result = 0;
        while (i < rucksacks.length) {
            Set<Character> common = toSet(rucksacks[i++].toCharArray());
            common.retainAll(toSet(rucksacks[i++].toCharArray()));
            common.retainAll(toSet(rucksacks[i++].toCharArray()));

            result += prioritize(common.iterator().next());
        }
        return result;
    }

}
