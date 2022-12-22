package aoc;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * --- Day 21: Monkey Math ---
 *
 * The monkeys are back! You're worried they're going to try to steal your stuff again, but it
 * seems like they're just holding their ground and making various monkey noises at you.
 *
 * Eventually, one of the elephants realizes you don't speak monkey and comes over to interpret.
 * As it turns out, they overheard you talking about trying to find the grove; they can show you
 * a shortcut if you answer their riddle.
 *
 * Each monkey is given a job: either to yell a specific number or to yell the result of a math
 * operation. All of the number-yelling monkeys know their number from the start; however, the
 * math operation monkeys need to wait for two other monkeys to yell a number, and those two
 * other monkeys might also be waiting on other monkeys.
 *
 * Your job is to work out the number the monkey named root will yell before the monkeys figure
 * it out themselves.
 *
 * For example:
 *
 * root: pppw + sjmn
 * dbpl: 5
 * cczh: sllz + lgvd
 * zczc: 2
 * ptdq: humn - dvpt
 * dvpt: 3
 * lfqf: 4
 * humn: 5
 * ljgn: 2
 * sjmn: drzm * dbpl
 * sllz: 4
 * pppw: cczh / lfqf
 * lgvd: ljgn * ptdq
 * drzm: hmdt - zczc
 * hmdt: 32
 *
 * Each line contains the name of a monkey, a colon, and then the job of that monkey:
 *
 *     A lone number means the monkey's job is simply to yell that number.
 *     A job like aaaa + bbbb means the monkey waits for monkeys aaaa and bbbb to yell each
 *          of their numbers; the monkey then yells the sum of those two numbers.
 *     aaaa - bbbb means the monkey yells aaaa's number minus bbbb's number.
 *     Job aaaa * bbbb will yell aaaa's number multiplied by bbbb's number.
 *     Job aaaa / bbbb will yell aaaa's number divided by bbbb's number.
 *
 * So, in the above example, monkey drzm has to wait for monkeys hmdt and zczc to yell their
 * numbers. Fortunately, both hmdt and zczc have jobs that involve simply yelling a single number,
 * so they do this immediately: 32 and 2. Monkey drzm can then yell its
 * number by finding 32 minus 2: 30.
 *
 * Then, monkey sjmn has one of its numbers (30, from monkey drzm),
 * and already has its other number, 5, from dbpl.
 * This allows it to yell its own number by finding 30 multiplied by 5: 150.
 *
 * This process continues until root yells a number: 152.
 *
 * However, your actual situation involves considerably more monkeys. What number will the monkey
 * named root yell?
 *
 * --- Part Two ---
 *
 * Due to some kind of monkey-elephant-human mistranslation, you seem to have misunderstood a few
 * key details about the riddle.
 *
 * First, you got the wrong job for the monkey named root; specifically, you got the wrong math
 * operation. The correct operation for monkey root should be =, which means that it still listens
 * for two numbers (from the same two monkeys as before), but now checks that the two numbers match.
 *
 * Second, you got the wrong monkey for the job starting with humn:. It isn't a monkey - it's you.
 * Actually, you got the job wrong, too: you need to figure out what number you need to yell
 * so that root's equality check passes. (The number that appears after humn: in your input is
 * now irrelevant.)
 *
 * In the above example, the number you need to yell to pass root's equality test is 301.
 * (This causes root to get the same number, 150, from both of its monkeys.)
 *
 * That's not the right answer. If you're stuck, make sure you're using the full input data; there
 * are also some general tips on the about page, or you can ask for hints on the subreddit. Please
 * wait one minute before trying again. (You guessed -2138749060055070668.)
 * > had overflows
 *
 * That's not the right answer; your answer is too high. If you're stuck, make sure you're using
 * the full input data; there are also some general tips on the about page, or you can ask for
 * hints on the subreddit. Please wait one minute before trying again. (You guessed 3876027196186.)
 * > not handling the remainders properly
 */
public class Day21 {

    private static boolean DEBUG = false;

    interface Monkey {
        long calcValue(Map<String, Monkey> monkeys);
        BigInteger calcBigInt(Map<String, Monkey> monkeys);

        boolean hasRemainder(Map<String, Monkey> monkeys);
    }

    record ConstMonkey(long val, BigInteger bigint) implements Monkey {

        @Override
        public long calcValue(Map<String, Monkey> monkeys) {
            return val;
        }

        @Override
        public BigInteger calcBigInt(Map<String, Monkey> monkeys) {
            return bigint;
        }

        @Override
        public boolean hasRemainder(Map<String, Monkey> monkeys) {
            return false;
        }

    }

    record OpMonkey(String lhs, char op, String rhs) implements Monkey {

        @Override
        public long calcValue(Map<String, Monkey> monkeys) {
            long lhsVal = monkeys.get(lhs).calcValue(monkeys);
            long rhsVal = monkeys.get(rhs).calcValue(monkeys);
            return switch (op) {
                case '*' -> lhsVal * rhsVal;
                case '/' -> lhsVal / rhsVal;
                case '+' -> lhsVal + rhsVal;
                case '-' -> lhsVal - rhsVal;
                default -> throw new IllegalStateException("Did not expect op: " + op);
            };
        }

        @Override
        public BigInteger calcBigInt(Map<String, Monkey> monkeys) {
            BigInteger lhsVal = monkeys.get(lhs).calcBigInt(monkeys);
            BigInteger rhsVal = monkeys.get(rhs).calcBigInt(monkeys);
            return switch (op) {
                case '*' -> lhsVal.multiply(rhsVal);
                case '/' -> lhsVal.divide(rhsVal);
                case '+' -> lhsVal.add(rhsVal);
                case '-' -> lhsVal.subtract(rhsVal);
                default -> throw new IllegalStateException("Did not expect op: " + op);
            };
        }

        @Override
        public boolean hasRemainder(Map<String, Monkey> monkeys) {
            BigInteger lhsVal = monkeys.get(lhs).calcBigInt(monkeys);
            BigInteger rhsVal = monkeys.get(rhs).calcBigInt(monkeys);
            return switch (op) {
                case '*' -> monkeys.get(lhs).hasRemainder(monkeys) ||  monkeys.get(rhs).hasRemainder(monkeys);
                case '/' -> !lhsVal.remainder(rhsVal).equals(BigInteger.ZERO) || monkeys.get(lhs).hasRemainder(monkeys) ||  monkeys.get(rhs).hasRemainder(monkeys);
                case '+' -> monkeys.get(lhs).hasRemainder(monkeys) ||  monkeys.get(rhs).hasRemainder(monkeys);
                case '-' -> monkeys.get(lhs).hasRemainder(monkeys) ||  monkeys.get(rhs).hasRemainder(monkeys);
                default -> throw new IllegalStateException("Did not expect op: " + op);
            };
        }
    }

    record Human() implements Monkey {

        @Override
        public long calcValue(Map<String, Monkey> monkeys) {
            throw new IllegalStateException("Human does not have a value");
        }
        @Override
        public BigInteger calcBigInt(Map<String, Monkey> monkeys) {
            throw new IllegalStateException("Human does not have a value");
        }
        @Override
        public boolean hasRemainder(Map<String, Monkey> monkeys) {
            throw new IllegalStateException("Human does not have a value");
        }
    }

    private static Map<String, Monkey> parseInput(String input) {
        Iterator<String> lines = input.lines().iterator();
        Map<String, Monkey> monkeys = new HashMap<>();
        while(lines.hasNext()) {
            String line = lines.next();
            // root: pppw + sjmn
            // dbpl: 5
            String[] cols = line.split(": ");
            String monkeyId = cols[0];
            final Monkey monkey;
            String[] operationCols = cols[1].split(" ");
            if (operationCols.length == 1) {
                monkey = new ConstMonkey(Integer.parseInt(operationCols[0]), new BigInteger(String.valueOf(Integer.parseInt(operationCols[0]))));
            } else if (operationCols.length == 3) {
                if (operationCols[1].length() != 1) {
                    throw new IllegalStateException("unexpected operation on line " + line);
                }
                monkey = new OpMonkey(operationCols[0], operationCols[1].charAt(0), operationCols[2]);
            } else {
                throw new IllegalStateException("unexpected num of columns on line " + line);
            }

            monkeys.put(monkeyId, monkey);
        }

        return monkeys;
    }

    private static long part1(String input) {
        Map<String, Monkey> monkeys = parseInput(input);

        return monkeys.get("root").calcValue(monkeys);
    }

    private static String expand(OpMonkey rootMonkey, Map<String, Monkey> monkeys) {
        Monkey lhsMonkey = monkeys.get(rootMonkey.lhs);
        Monkey rhsMonkey = monkeys.get(rootMonkey.rhs);
        return switch (lhsMonkey) {
            case ConstMonkey(var val1, var ignore1) -> {
                switch (rhsMonkey) {
                    case ConstMonkey(var val2, var ignore2) -> throw new IllegalStateException("please simplify first");
                    case OpMonkey(var lhs2, var op2, var rhs2) -> {
                        yield "(" + val1 + rootMonkey.op + "(" + expand(new OpMonkey(lhs2, op2, rhs2), monkeys) + ")" + ")";
                    }
                    case Human() -> {
                        yield "(" + val1 + rootMonkey.op + "humn" + ")";
                    }
                    default -> throw new IllegalStateException();
                }
            }
            case OpMonkey(var lhs1, var op1, var rhs1) -> {
                switch (rhsMonkey) {
                    case ConstMonkey(var val2, var ignore2) -> {
                        yield  "(" + "(" + expand(new OpMonkey(lhs1, op1, rhs1), monkeys) + ")" + rootMonkey.op + val2 + ")";
                    }
                    case Human() -> {
                        yield  "(" + "(" + expand(new OpMonkey(lhs1, op1, rhs1), monkeys) + ")" + rootMonkey.op + "humn" + ")";
                    }
                    case OpMonkey(var lhs2, var op2, var rhs2) -> {
                        yield "(" + "(" + expand(new OpMonkey(lhs1, op1, rhs1), monkeys) + ")"
                            + rootMonkey.op
                            + "(" + expand(new OpMonkey(lhs2, op2, rhs2), monkeys) + ")" + ")";
                    }
                    default -> throw new IllegalStateException();
                }
            } case Human() -> {
                switch (rhsMonkey) {
                    case ConstMonkey(var val2, var ignore2) -> {
                        yield  "(" + "humn" + rootMonkey.op + val2 + ")";
                    }
                    case Human() -> {
                        yield  "(" + "humn" + rootMonkey.op + "humn" + ")";
                    }
                    case OpMonkey(var lhs2, var op2, var rhs2) -> {
                        yield "(" + "humn"
                            + rootMonkey.op
                            + "(" + expand(new OpMonkey(lhs2, op2, rhs2), monkeys) + ")" + ")";
                    }
                    default -> throw new IllegalStateException();
                }
            }
            default -> throw new IllegalStateException();
        };
    }

    record Solution (BigInteger distance, BigInteger result, BigInteger midpoint, BigInteger lowerbound, BigInteger upperbound) {
    }

    private static long part2(String input) {
        Map<String, Monkey> monkeys = parseInput(input);
        OpMonkey rootMonkey = (OpMonkey) monkeys.remove("root");
        rootMonkey = new OpMonkey(rootMonkey.lhs, '=', rootMonkey.rhs);
        monkeys.remove("humn");

        if(DEBUG) {
            System.out.println("const count before");
            System.out.println(monkeys.values().stream().filter(monkey -> monkey instanceof ConstMonkey).count()
             + " out of " + monkeys.size());
        }

        boolean canSimplify = true;
        while (canSimplify) {
            canSimplify = false;
            for (var entry : monkeys.entrySet()) {
                String monkeyId = entry.getKey();
                Monkey monkey = entry.getValue();

                switch (monkey) {
                    case OpMonkey(String lhs, char op, String rhs) -> {
                        Monkey lhsMonkey = monkeys.get(lhs);
                        Monkey rhsMonkey = monkeys.get(rhs);
                        if (lhsMonkey instanceof ConstMonkey && rhsMonkey instanceof ConstMonkey) {
                            long lhsVal = ((ConstMonkey) lhsMonkey).val;
                            long rhsVal = ((ConstMonkey) rhsMonkey).val;
                            long result = switch (op) {
                                case '*' -> lhsVal * rhsVal;
                                case '/' -> lhsVal / rhsVal;
                                case '+' -> lhsVal + rhsVal;
                                case '-' -> lhsVal - rhsVal;
                                default -> throw new IllegalStateException("Did not expect op: " + op);
                            };

                            monkeys.put(monkeyId, new ConstMonkey(result, BigInteger.valueOf(result)));
                            canSimplify = true;
                        }
                    }
                    default ->  {}
                }
            }
        }


        if(DEBUG) {
            System.out.println("const count after");
            System.out.println(monkeys.values().stream().filter(monkey -> monkey instanceof ConstMonkey).count()
                + " out of " + monkeys.size());
        }

        System.out.println("expanded");
        monkeys.put("humn", new Human());
        System.out.println(expand(rootMonkey, monkeys));

        // A* search for the solution
        monkeys.remove("humn");
        Monkey lhsMonkey = monkeys.get(rootMonkey.lhs);
        BigInteger target = ((ConstMonkey) monkeys.get(rootMonkey.rhs)).bigint;
        PriorityQueue<Solution> priorityQueue = new PriorityQueue<>(
            Comparator.comparing(a -> a.distance)
        );

        BigInteger lower = new BigInteger(String.valueOf(Long.MIN_VALUE));
        BigInteger upper = new BigInteger(String.valueOf(Long.MAX_VALUE));
        BigInteger midpoint = lower.add(upper).divide(BigInteger.TWO);
        monkeys.put("humn", new ConstMonkey( midpoint.longValue(), midpoint ));
        BigInteger currentResult = lhsMonkey.calcBigInt(monkeys);
        priorityQueue.add(new Solution(
            target.subtract(currentResult).abs(),
            currentResult,
            midpoint,
            lower,
            upper
        ));

        BigInteger potentialSolution = null;
        while (!priorityQueue.isEmpty()) {
            Solution currentSolution = priorityQueue.remove();
            if (currentSolution.distance.equals(BigInteger.ZERO)) {
                potentialSolution = currentSolution.midpoint;
                break;
            }
            // left side of search space
            lower = currentSolution.lowerbound;
            upper = currentSolution.midpoint.subtract(BigInteger.ONE);
            midpoint = lower.add(upper).divide(BigInteger.TWO);
            monkeys.put("humn", new ConstMonkey( midpoint.longValue(), midpoint ));
            currentResult = lhsMonkey.calcBigInt(monkeys);
            priorityQueue.add(new Solution(
                target.subtract(currentResult).abs(),
                currentResult,
                midpoint,
                lower,
                upper
            ));
            // right size of search space
            lower = currentSolution.midpoint.add(BigInteger.ONE);
            upper = currentSolution.upperbound;
            midpoint = lower.add(upper).divide(BigInteger.TWO);
            monkeys.put("humn", new ConstMonkey( midpoint.longValue(), midpoint ));
            currentResult = lhsMonkey.calcBigInt(monkeys);
            priorityQueue.add(new Solution(
                target.subtract(currentResult).abs(),
                currentResult,
                midpoint,
                lower,
                upper
            ));
        }

        BigInteger i = BigInteger.ZERO;
        while (true) {
            BigInteger above = potentialSolution.add(i);
            monkeys.put("humn", new ConstMonkey( above.longValue(), above ));
            BigInteger aboveSolution = lhsMonkey.calcBigInt(monkeys);
            if (aboveSolution.equals(target) && !lhsMonkey.hasRemainder(monkeys)) {
                return above.longValue();
            }

            BigInteger below = potentialSolution.subtract(i);
            monkeys.put("humn", new ConstMonkey( above.longValue(), below ));
            BigInteger belowSolution = lhsMonkey.calcBigInt(monkeys);
            if (belowSolution.equals(target) && !lhsMonkey.hasRemainder(monkeys)) {
                return below.longValue();
            }

            i = i.add(BigInteger.ONE);
        }
    }

    public static void main(String[] args) throws IOException {
        int day = 21;

        DEBUG = true;
        String sampleInput = Files.readString(java.nio.file.Path.of("input/day_"+day+"_sample.txt"));
        //String sample2Input = Files.readString(java.nio.file.Path.of("input/day_"+day+"_sample2.txt"));
        String realInput = Files.readString(java.nio.file.Path.of("input/day_"+day+".txt"));
//        System.out.println("sample:");
//        part2(sampleInput);
//        System.out.println("real:");
//        part2(realInput);

        DEBUG = false;
        System.out.println("Expected: "
            + Files.readString(java.nio.file.Path.of("input/day_"+day+"_sample_part1_expected.txt")));
        System.out.println("Actual:   "
            + part1(sampleInput));
//        System.out.println("Expected: "
//            + Files.readString(java.nio.file.Path.of("input/day_"+day+"_sample2_part1_expected.txt")));
//        System.out.println("Actual:   "
//            + part1(sample2Input));
        System.out.println("Solution: "
            + part1(realInput));

        System.out.println("Expected: "
            + Files.readString(java.nio.file.Path.of("input/day_"+day+"_sample_part2_expected.txt")));
        System.out.println("Actual:   " +  part2(sampleInput));
        System.out.println("Solution: " +  part2(realInput));

    }
}
