package aoc;

import java.util.*;
import java.io.*;
import java.nio.file.*;
import java.nio.*;
import java.util.function.Function;
import java.util.stream.*;

/**
 * <a href="https://adventofcode.com/2022/day/20>Day 20: Grove Positioning System</a>
 *
 * bug 1:
 * That's not the right answer; your answer is too low. (You guessed 94043877.) [Return to Day 20]
 * bug was integer overflow again :(
 */
public class Day20 {

    private static class LL {
        int val;
        LL next;
        LL prev;

        LL(int val, LL next, LL prev) {

        }
    }


    record Wrapper(long v) {

        @Override
        public boolean equals(Object other) {
            return this == other;
        }
    }

    private static long solve(
        String input,
        int numMixes,
        long multiplier
    ) {
        List<Wrapper> values = input.lines()
            .map(Integer::parseInt)
            .map(integer -> integer * multiplier)
            .map(Wrapper::new)
            .collect(Collectors.toList());

        List<Wrapper> prev = new ArrayList<>(values);
        for (int mixNum = 1; mixNum<= numMixes; mixNum++) {
            List<Wrapper> next = new ArrayList<>(prev);
            for(Wrapper wrapper : values) {
                // find the element
                int idx = next.indexOf(wrapper);

                // 0 1 2 3 4
                // 1 2 3 4 5
                // remove 3
                // idx 2
                // 1 2 4 5
                // 0 1 2 3
                // remove the element
                next.remove(idx);

                // move the element
                long newIndex = idx + wrapper.v;
                if (newIndex < 0) {
                    long adder = (((newIndex*-1)/next.size())+1) * (next.size());
                    newIndex = newIndex + adder;
                }
                if (newIndex >= next.size()) {
                    newIndex = newIndex % next.size();
                }

                next.add((int)newIndex, wrapper);
            }

            prev = next;
        }

        int zeroIdx = -1;
        for (int i = 0; i < prev.size(); i++) {
            if (prev.get(i).v == 0) {
                zeroIdx = i;
                break;
            }
        }

        long sum = 0;
        for (int i = 1; i <= 3; i++) {
            int thousandthIdx  = (zeroIdx + i*1000) % prev.size();
            long val = prev.get(thousandthIdx).v;
            System.out.println(i*1000 + " -> " + thousandthIdx + " -> " + val);
            sum += val;
        }

        /*
        Then, the grove coordinates can be found by looking at the 1000th, 2000th, and 3000th numbers after the value 0,
        wrapping around the list as necessary. In the above example, the 1000th number after 0 is 4, the 2000th is -3, and the 3000th is 2; adding these together produces 3.
        Mix your encrypted file exactly once. What is the sum of the three numbers that form the grove coordinates?
         */
        return sum;
    }

    private static long part1(String input) {
        return solve(input, 1, 1);
    }

    private static long part2(String input) {
        return solve(input, 10, 811589153);
    }

    public static void main(String[] args) throws Exception {
        String sample = Files.readString(Path.of("input/day_20_sample.txt"));
        String input = Files.readString(Path.of("input/day_20.txt"));

//        Map<Integer, Long> counts = input.lines()
//            .map(Integer::parseInt)
//            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
//        counts.forEach((k,v) -> {
//            if (v > 1) {
//                System.out.println(k + " " + v);
//            }
//        });

        Wrapper a = new Wrapper(0);
        Wrapper b = new Wrapper(0);
        System.out.println("a==b " + (a == b));
        System.out.println("a==a " + (a == a));

        System.out.println("Expected: 3");
        System.out.println("Actual:   " + part1(sample));
        System.out.println("Solution  " + part1(input));

        System.out.println("Expected: 1623178306");
        System.out.println("Actual:   " + part2(sample));
        System.out.println("Solution  " + part2(input));
    }
}