package aoc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws IOException {
        var day = Integer.parseInt(args[0]);
        var numOfSamples = 1;
        if (args.length >= 2) {
            numOfSamples = Integer.parseInt(args[1]);
        }
        var samples = new ArrayList<String>();
        if (numOfSamples == 1) {
            samples.add(Files.readString(Path.of("input/day_"+day+"_sample.txt")));
        } else {
            for (int i = 1; i <= numOfSamples; i++) {
                samples.add(Files.readString(Path.of("input/day_"+day+"_sample" + (i) + ".txt")));
            }
        }
        var input = Files.readString(Path.of("input/day_"+day+".txt"));
        var samplesPart1Expected = new ArrayList<>();
        if (numOfSamples == 1) {
            samplesPart1Expected.add(Files.readString(Path.of("input/day_"+day+"_sample_part1_expected.txt")));
        } else {
            for (int i = 1; i <= numOfSamples; i++) {
                samplesPart1Expected.add(Files.readString(Path.of("input/day_"+day+"_sample" + i + "_part1_expected.txt")));
            }
        }

        var samplesPart2Expected = new ArrayList<>();
        if (numOfSamples == 1) {
            samplesPart2Expected.add(Files.readString(Path.of("input/day_"+day+"_sample_part2_expected.txt")));
        } else {
            for (int i = 1; i <= numOfSamples; i++) {
                samplesPart2Expected.add(Files.readString(Path.of("input/day_"+day+"_sample" + i + "_part2_expected.txt")));
            }
        }

        var solution = switch(day) {
            case 1 -> new Day1();
            case 2 -> new Day2();
            case 3 -> new Day3();
            case 4 -> new Day4();
            case 5 -> new Day5();
            case 6 -> new Day6();
            // run Day7 from Day's main
            case 8 -> new Day8();
            case 9 -> new Day9();
            case 10 -> new Day10();
            case 11 -> new Day11();
            case 12 -> new Day12();
            case 13 -> new Day13();
            case 14 -> new Day14();
            // run day15 from Day15's main
            default -> throw new IllegalStateException();
        };

        for (int i = 1; i <= numOfSamples; i++) {
            System.out.println("Sample  " + i);
            System.out.println("Expected: " + samplesPart1Expected.get(i-1));
            System.out.println("Actual:   " + solution.part1(samples.get(i-1)));
        }
        System.out.println("==============================");
        System.out.println(solution.part1(input));
        System.out.println("==============================");


        for (int i = 1; i <= numOfSamples; i++) {
            System.out.println("Sample  " + i);
            System.out.println("Expected: " + samplesPart2Expected.get(i-1));
            System.out.println("Actual:   " + solution.part2(samples.get(i-1)));
        }
        System.out.println(solution.part2(input));
    }
}