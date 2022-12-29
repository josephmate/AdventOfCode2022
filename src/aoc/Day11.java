package aoc;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <a href="https://adventofcode.com/2022/day/11>Day 11: Monkey in the Middle</a>
 */
public class Day11 implements Solution {

    private static final boolean DEBUG = false;


    @Override
    public String part1(String input) {
        return String.valueOf(part1Impl(input));
    }

    @Override
    public String part2(String input) {
        return String.valueOf(part2Impl(input));
    }

    enum Operator{
        ADD,
        MULT
    }

    /**
     * Monkey 0:
     *   Starting items: 79, 98
     *   Operation: new = old * 19
     *   Test: divisible by 23
     *     If true: throw to monkey 2
     *     If false: throw to monkey 3
     */

    record ConstantOperand (int val) {

    }

    record OldOperand() {

    }

    record Monkey(
        int id,
        List<Long> worryLevels,
        Record leftOperand,
        Operator operator,
        Record rightOperand,
        int divisibleTest,
        int trueMonkey,
        int falseMonkey
    ) {

    }

    private static List<Monkey> parse(String input) {
        /*
Monkey 0:
  Starting items: 79, 98
  Operation: new = old * 19
  Test: divisible by 23
    If true: throw to monkey 2
    If false: throw to monkey 3
         */
        List<Monkey> result = new ArrayList<>();

        String [] monkeyStrings = input.trim().split("\n\n");
        for (String monkeyString : monkeyStrings) {
            String[] lines = monkeyString.split("\n");
            String[] operatorCols = lines[2].trim().split(" ");

            final Record leftOperand;
            if ("old".equals(operatorCols[3])) {
                leftOperand = new OldOperand();
            } else {
                leftOperand = new ConstantOperand(Integer.parseInt(operatorCols[3]));
            }

            final Operator operator;
            if ("*".equals(operatorCols[4])) {
                operator = Operator.MULT;
            } else if ("+".equals(operatorCols[4])) {
                operator = Operator.ADD;
            } else {
                throw new IllegalStateException();
            }

            final Record rightOperand;
            if ("old".equals(operatorCols[5])) {
                rightOperand = new OldOperand();
            } else {
                rightOperand = new ConstantOperand(Integer.parseInt(operatorCols[5]));
            }

            result.add(new Monkey(
                result.size(),
                Arrays.stream(lines[1].trim().split(": ")[1].split(", "))
                    .map(Long::parseLong)
                    .collect(Collectors.toList()),
                leftOperand,
                operator,
                rightOperand,
                Integer.parseInt(lines[3].trim().split(" ")[3]),
                Integer.parseInt(lines[4].trim().split(" ")[5]),
                Integer.parseInt(lines[5].trim().split(" ")[5])
            ));
        }

        return result;
    }

    private static int part1Impl(String s) {
        List<Monkey> monkeys = parse(s);

        int[] monkeyBusiness = new int[monkeys.size()];
        printMonkeys(monkeys);

        for (int round = 1; round <= 20; round++) {
//            List<List<Integer>> destinationWorryLevels = new ArrayList<>(monkeys.size());
//            for (int monkeyId = 0; monkeyId < monkeys.size(); monkeyId++) {
//                destinationWorryLevels.add(new ArrayList<>());
//            }

            // calculate the new monkey levels
            // figure out the destination
            for (int monkeyId = 0; monkeyId < monkeys.size(); monkeyId++) {
                Monkey monkey = monkeys.get(monkeyId);
                monkeyBusiness[monkeyId] += monkey.worryLevels.size();
                List<Long> worryLevels = new ArrayList<>(monkey.worryLevels);
                monkey.worryLevels.clear();

                for(long worryLevel : worryLevels) {
                    final long leftOperand = switch (monkey.leftOperand) {
                        case OldOperand() -> worryLevel;
                        case ConstantOperand(int val) -> val;
                        default -> throw new IllegalStateException();
                    };
                    final long rightOperand = switch (monkey.rightOperand) {
                        case OldOperand() -> worryLevel;
                        case ConstantOperand(int val) -> val;
                        default -> throw new IllegalStateException();
                    };

                    final long newWorry = switch (monkey.operator) {
                        case ADD -> (leftOperand + rightOperand)/3;
                        case MULT -> (leftOperand * rightOperand)/3;
                    };

                    if (newWorry % monkey.divisibleTest == 0) {
                        //destinationWorryLevels.get(monkey.trueMonkey).add(newWorry);
                        monkeys.get(monkey.trueMonkey).worryLevels.add(newWorry);
                    } else {
                        //destinationWorryLevels.get(monkey.falseMonkey).add(newWorry);
                        monkeys.get(monkey.falseMonkey).worryLevels.add(newWorry);
                    }
                }
            }

            // send the items
//            for (int monkeyId = 0; monkeyId < monkeys.size(); monkeyId++) {
//                monkeys.get(monkeyId).worryLevels.clear();
//                monkeys.get(monkeyId).worryLevels.addAll(destinationWorryLevels.get(monkeyId));
//            }

            if (DEBUG) {
                System.out.println("=====" + round + "=====");
            }
            printMonkeys(monkeys);
        }

        Arrays.sort(monkeyBusiness);

        return monkeyBusiness[monkeyBusiness.length-1] * monkeyBusiness[monkeyBusiness.length-2];
    }

    private static void printMonkeys(List<Monkey> monkeys) {
        if (DEBUG) {
            for (Monkey monkey : monkeys) {
                System.out.println(monkey);
            }
        }
    }

    private static long part2Impl(String s) {
        List<Monkey> monkeys = parse(s);
        int modulo = monkeys.stream()
            .mapToInt(Monkey::divisibleTest)
            .reduce(1, (a,b) -> a*b);

        long[] monkeyBusiness = new long[monkeys.size()];
        printMonkeys(monkeys);

        for (int round = 1; round <= 10000; round++) {

            // calculate the new monkey levels
            // figure out the destination
            for (int monkeyId = 0; monkeyId < monkeys.size(); monkeyId++) {
                Monkey monkey = monkeys.get(monkeyId);
                monkeyBusiness[monkeyId] += monkey.worryLevels.size();
                List<Long> worryLevels = new ArrayList<>(monkey.worryLevels);
                monkey.worryLevels.clear();

                for(long worryLevel : worryLevels) {
                    final long leftOperand = switch (monkey.leftOperand) {
                        case OldOperand() -> worryLevel;
                        case ConstantOperand(int val) -> val;
                        default -> throw new IllegalStateException();
                    };
                    final long rightOperand = switch (monkey.rightOperand) {
                        case OldOperand() -> worryLevel;
                        case ConstantOperand(int val) -> val;
                        default -> throw new IllegalStateException();
                    };

                    final long newWorry = switch (monkey.operator) {
                        case ADD -> (leftOperand + rightOperand) % modulo;
                        case MULT -> (leftOperand * rightOperand) % modulo;
                    };

                    if (newWorry % monkey.divisibleTest == 0) {
                        //destinationWorryLevels.get(monkey.trueMonkey).add(newWorry);
                        monkeys.get(monkey.trueMonkey).worryLevels.add(newWorry);
                    } else {
                        //destinationWorryLevels.get(monkey.falseMonkey).add(newWorry);
                        monkeys.get(monkey.falseMonkey).worryLevels.add(newWorry);
                    }
                }
            }

            if (DEBUG) {
                if (round == 1
                    || round == 20
                    || round % 1000 == 0
                ) {
                    System.out.println("=====" + round + "=====");
                    //printMonkeys(monkeys);
                    System.out.println(Arrays.stream(monkeyBusiness).boxed().collect(Collectors.toList()));
                }
            }
        }

        Arrays.sort(monkeyBusiness);

        return monkeyBusiness[monkeyBusiness.length-1] * monkeyBusiness[monkeyBusiness.length-2];
    }

    private static long usingBigInt(String s) {
        List<Monkey> monkeys = parse(s);
        List<List<BigInteger>> monkeysWorryLevels = new ArrayList<>(monkeys.size());
        for (int i = 0; i < monkeys.size(); i++) {
            List<BigInteger> bints = new ArrayList<>();
            for (long worryLevel : monkeys.get(i).worryLevels) {
                bints.add(new BigInteger(String.valueOf(worryLevel)));
            }
            monkeys.get(i).worryLevels.clear();
            monkeysWorryLevels.add(bints);
        }
        int[] monkeyBusiness = new int[monkeys.size()];
        printMonkeys(monkeys);

        for (int round = 1; round <= 10000; round++) {

            // calculate the new monkey levels
            // figure out the destination
            for (int monkeyId = 0; monkeyId < monkeys.size(); monkeyId++) {
                Monkey monkey = monkeys.get(monkeyId);
//                monkeyBusiness[monkeyId] += monkey.worryLevels.size();
//                List<Integer> worryLevels = new ArrayList<>(monkey.worryLevels);
//                monkey.worryLevels.clear();
//                for(int worryLevel : worryLevels) {
                monkeyBusiness[monkeyId] += monkeysWorryLevels.get(monkeyId).size();
                List<BigInteger> worryLevels = new ArrayList<>(monkeysWorryLevels.get(monkeyId));
                monkeysWorryLevels.get(monkeyId).clear();
//                for(int worryLevel : worryLevels) {
//                    final int leftOperand = switch (monkey.leftOperand) {
//                        case OldOperand() -> worryLevel;
//                        case ConstantOperand(int val) -> val;
//                        default -> throw new IllegalStateException();
//                    };
//                    final int rightOperand = switch (monkey.rightOperand) {
//                        case OldOperand() -> worryLevel;
//                        case ConstantOperand(int val) -> val;
//                        default -> throw new IllegalStateException();
//                    };
//
//                    final int newWorry = switch (monkey.operator) {
//                        case ADD -> (leftOperand + rightOperand)/3;
//                        case MULT -> (leftOperand * rightOperand)/3;
//                    };
//
//                    if (newWorry % monkey.divisibleTest == 0) {
//                        monkeys.get(monkey.trueMonkey).worryLevels.add(newWorry);
//                    } else {
//                        monkeys.get(monkey.falseMonkey).worryLevels.add(newWorry);
//                    }
//                }
                for(BigInteger worryLevel : worryLevels) {
                    final BigInteger leftOperand = switch (monkey.leftOperand) {
                        case OldOperand() -> worryLevel;
                        case ConstantOperand(int val) -> new BigInteger(String.valueOf(val));
                        default -> throw new IllegalStateException();
                    };
                    final BigInteger rightOperand = switch (monkey.rightOperand) {
                        case OldOperand() -> worryLevel;
                        case ConstantOperand(int val) -> new BigInteger(String.valueOf(val));
                        default -> throw new IllegalStateException();
                    };

                    final BigInteger newWorry = switch (monkey.operator) {
                        case ADD -> leftOperand.add(rightOperand);
                        case MULT -> leftOperand.multiply(rightOperand);
                    };

                    if (newWorry.remainder(new BigInteger(String.valueOf(monkey.divisibleTest))).equals(BigInteger.ZERO)) {
                        monkeysWorryLevels.get(monkey.trueMonkey).add(newWorry);
                    } else {
                        monkeysWorryLevels.get(monkey.falseMonkey).add(newWorry);
                    }
                }
            }

            if (DEBUG) {
                System.out.println("=====" + round + "=====");
            }
            printMonkeys(monkeys);
        }

        Arrays.sort(monkeyBusiness);

        return monkeyBusiness[monkeyBusiness.length-1] * monkeyBusiness[monkeyBusiness.length-2];
    }

    public static void main(String[] args) throws IOException {

        System.out.println(parse(Files.readString(Path.of("input/day_11.txt")))
            .stream()
            .mapToInt(Monkey::divisibleTest)
            .reduce(1, (a,b) -> a*b)); // 9699690


        System.out.println(parse(Files.readString(Path.of("input/day_11_sample.txt")))
            .stream()
            .mapToInt(Monkey::divisibleTest)
            .reduce(1, (a,b) -> a*b)); // 96577
    }
}
