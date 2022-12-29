package aoc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <a href="https://adventofcode.com/2022/day/10>Day 10: Cathode-Ray Tube</a>
 */
public class Day10 implements Solution {

    private static final boolean DEBUG = false;


    @Override
    public String part1(String input) {
        return String.valueOf(part1Impl(input));
    }

    @Override
    public String part2(String input) {
        return String.valueOf(part2Impl(input));
    }

    record AddX(int val) {

    }

    record Noop() {

    }

    private static Object parse(String line) {
        if (line.startsWith("addx")) {
            String [] cols = line.split(" ");
            return new AddX(Integer.parseInt(cols[1]));
        } else {
            return new Noop();
        }
    }

    private static int part1Impl(String s) {

        int register = 1;
        int cycle = 1;
        int result = 0;

        Iterator<Object> instructions = s.lines()
            .map(Day10::parse)
            .iterator();
        while (instructions.hasNext()) {
            switch (instructions.next()) {
                case AddX(int val):
                    if (DEBUG) {
                        System.out.println("addx " + val);
                    }

                    if (DEBUG) {
                        System.out.println(cycle + ": " + cycle + "*" + register + "=" + (cycle * register));
                    }
                    if (cycle % 40 == 20 && cycle <= 220) {
                        result += cycle * register;
                    }
                    cycle++;
                    if (cycle % 40 == 20 && cycle <= 220) {
                        result += cycle * register;
                    }
                    if (DEBUG) {
                        System.out.println(cycle + ": " + cycle + "*" + register + "=" + (cycle * register));
                    }
                    cycle++;

                    register+= val;

                    break;
                case Noop():
                    if (DEBUG) {
                        System.out.println("noop");
                    }
                    if (DEBUG) {
                        System.out.println(cycle + ": " + cycle + "*" + register + "=" + (cycle * register));
                    }
                    if (cycle % 40 == 20 && cycle <= 220) {
                        result += cycle * register;
                    }
                    cycle++;
                    break;
                default:
                    throw new IllegalStateException();
            }
        }

        return result;
    }

    private static String part2Impl(String s) {
        int register = 1;
        int cycle = 0;
        StringBuilder result = new StringBuilder();
        result.append("\n");
        Iterator<Object> instructions = s.lines()
            .map(Day10::parse)
            .iterator();
        while (instructions.hasNext()) {
            switch (instructions.next()) {
                case AddX(int val):
                    if (DEBUG) {
                        System.out.println("addx " + val);
                    }

                    if (register-1 <= (cycle % 40) && (cycle % 40) <= register + 1) {
                        result.append("#");
                    } else {
                        result.append(".");
                    }
                    if (cycle % 40 == 39) {
                        result.append("\n");
                    }
                    cycle++;
                    if (register-1 <= (cycle % 40) && (cycle % 40) <= register + 1) {
                        result.append("#");
                    } else {
                        result.append(".");
                    }
                    if (cycle % 40 == 39) {
                        result.append("\n");
                    }
                    cycle++;

                    register+= val;

                    break;
                case Noop():
                    if (DEBUG) {
                        System.out.println("noop");
                    }

                    if (register-1 <= (cycle % 40) && (cycle % 40) <= register + 1) {
                        result.append("#");
                    } else {
                        result.append(".");
                    }
                    if (cycle % 40 == 39) {
                        result.append("\n");
                    }
                    cycle++;
                    break;
                default:
                    throw new IllegalStateException();
            }
        }

        return result.toString(); // FECZELHE
    }

}
