package aoc;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * --- Day 25: Full of Hot Air ---
 *
 * As the expedition finally reaches the extraction point, several large hot air balloons drift
 * down to meet you. Crews quickly start unloading the equipment the balloons brought: many hot
 * air balloon kits, some fuel tanks, and a fuel heating machine.
 *
 * The fuel heating machine is a new addition to the process. When this mountain was a volcano,
 * the ambient temperature was more reasonable; now, it's so cold that the fuel won't work at all
 * without being warmed up first.
 *
 * The Elves, seemingly in an attempt to make the new machine feel welcome, have already attached
 * a pair of googly eyes and started calling it "Bob".
 *
 * To heat the fuel, Bob needs to know the total amount of fuel that will be processed ahead of
 * time so it can correctly calibrate heat output and flow rate. This amount is simply the sum of
 * the fuel requirements of all of the hot air balloons, and those fuel requirements are even
 * listed clearly on the side of each hot air balloon's burner.
 *
 * You assume the Elves will have no trouble adding up some numbers and are about to go back to
 * figuring out which balloon is yours when you get a tap on the shoulder. Apparently, the fuel
 * requirements use numbers written in a format the Elves don't recognize; predictably, they'd
 * like your help deciphering them.
 *
 * You make a list of all of the fuel requirements (your puzzle input), but you don't recognize
 * the number format either. For example:
 *
 * 1=-0-2
 * 12111
 * 2=0=
 * 21
 * 2=01
 * 111
 * 20012
 * 112
 * 1=-1=
 * 1-12
 * 12
 * 1=
 * 122
 *
 * Fortunately, Bob is labeled with a support phone number. Not to be deterred, you call and ask
 * for help.
 *
 * "That's right, just supply the fuel amount to the-- oh, for more than one burner? No problem,
 * you just need to add together our Special Numeral-Analogue Fuel Units. Patent pending!
 * They're way better than normal numbers for--"
 *
 * You mention that it's quite cold up here and ask if they can skip ahead.
 *
 * "Okay, our Special Numeral-Analogue Fuel Units - SNAFU for short - are sort of like normal
 * numbers. You know how starting on the right, normal numbers have a ones place, a tens place,
 * a hundreds place, and so on, where the digit in each place tells you how many of that value you have?"
 *
 * "SNAFU works the same way, except it uses powers of five instead of ten. Starting from the right,
 * you have a ones place, a fives place, a twenty-fives place, a one-hundred-and-twenty-fives place,
 * and so on. It's that easy!"
 *
 * You ask why some of the digits look like - or = instead of "digits".
 *
 * "You know, I never did ask the engineers why they did that. Instead of using digits four
 * through zero, the digits are 2, 1, 0, minus (written -), and double-minus (written =).
 * Minus is worth -1, and double-minus is worth -2."
 *
 * "So, because ten (in normal numbers) is two fives and no ones, in SNAFU it is written 20.
 * Since eight (in normal numbers) is two fives minus two ones, it is written 2=."
 *
 * "You can do it the other direction, too. Say you have the SNAFU number 2=-01. That's 2 in the
 * 625s place, = (double-minus) in the 125s place, - (minus) in the 25s place, 0 in the 5s place,
 * and 1 in the 1s place. (2 times 625) plus (-2 times 125) plus (-1 times 25) plus (0 times 5)
 * plus (1 times 1). That's 1250 plus -250 plus -25 plus 0 plus 1. 976!"
 *
 * "I see here that you're connected via our premium uplink service, so I'll transmit our handy
 * SNAFU brochure to you now. Did you need anything else?"
 *
 * You ask if the fuel will even work in these temperatures.
 *
 * "Wait, it's how cold? There's no way the fuel - or any fuel - would work in those conditions!
 * There are only a few places in the-- where did you say you are again?"
 *
 * Just then, you notice one of the Elves pour a few drops from a snowflake-shaped container
 * into one of the fuel tanks, thank the support representative for their time, and disconnect the call.
 *
 * The SNAFU brochure contains a few more examples of decimal ("normal") numbers and their
 * SNAFU counterparts:
 *
 *   Decimal          SNAFU
 *         1              1
 *         2              2
 *         3             1=
 *         4             1-
 *         5             10
 *         6             11
 *         7             12
 *         8             2=
 *         9             2-
 *        10             20
 *        15            1=0
 *        20            1-0
 *      2022         1=11-2
 *     12345        1-0---0
 * 314159265  1121-1110-1=0
 *
 * Based on this process, the SNAFU numbers in the example above can be converted to decimal
 * numbers as follows:
 *
 *  SNAFU  Decimal
 * 1=-0-2     1747
 *  12111      906
 *   2=0=      198
 *     21       11
 *   2=01      201
 *    111       31
 *  20012     1257
 *    112       32
 *  1=-1=      353
 *   1-12      107
 *     12        7
 *     1=        3
 *    122       37
 *
 * In decimal, the sum of these numbers is 4890.
 *
 * As you go to input this number on Bob's console, you discover that some buttons you expected
 * are missing. Instead, you are met with buttons labeled =, -, 0, 1, and 2.
 * Bob needs the input value expressed as a SNAFU number, not in decimal.
 *
 * Reversing the process, you can determine that for the decimal number 4890, the SNAFU number
 * you need to supply to Bob's console is 2=-1=0.
 *
 * The Elves are starting to get cold. What SNAFU number do you supply to Bob's console?
 */
public class Day25 {

    private static boolean DEBUG = false;


    /**
     *   Decimal          SNAFU
     *         1              1
     *         2              2
     *         3             1=
     *         4             1-
     *         5             10
     *         6             11
     *         7             12
     *         8             2=
     *         9             2-
     *        10             20
     *        15            1=0
     *        20            1-0
     *      2022         1=11-2
     *     12345        1-0---0
     * 314159265  1121-1110-1=0
     *
     *  SNAFU  Decimal
     * 1=-0-2     1747
     *  12111      906
     *   2=0=      198
     *     21       11
     *   2=01      201
     *    111       31
     *  20012     1257
     *    112       32
     *  1=-1=      353
     *   1-12      107
     *     12        7
     *     1=        3
     *    122       37
     *
     * "You can do it the other direction, too. Say you have the SNAFU number 2=-01. That's 2 in the
     * 625s place, = (double-minus) in the 125s place, - (minus) in the 25s place, 0 in the 5s place,
     * and 1 in the 1s place. (2 times 625) plus (-2 times 125) plus (-1 times 25) plus (0 times 5)
     * plus (1 times 1). That's 1250 plus -250 plus -25 plus 0 plus 1. 976!"
     */
    private static long snafuToInt(String val) {
        long sum = 0;
        long pow = 1;
        for (int i = val.length()-1; i >= 0; i--) {
            char digit = val.charAt(i);
            if (digit >= '0' && digit <= '2') {
                sum += pow * (digit - '0');
            } else if (digit == '=') {
                sum -= pow * 2;
            } else if (digit == '-') {
                sum -= pow * 1;
            } else {
                throw new IllegalStateException(digit + " in " + val + " is not a recognized snafu digit");
            }
            pow = pow * 5;
        }

        return sum;
    }

    /**
     *   Decimal          SNAFU
     *         1              1
     *         2              2
     *         3             1=
     *         4             1-
     *         5             10
     *         6             11
     *         7             12
     *         8             2=
     *         9             2-
     *        10             20
     *        15            1=0
     *        20            1-0
     *      2022         1=11-2
     *     12345        1-0---0
     * 314159265  1121-1110-1=0
     *
     *  SNAFU  Decimal
     * 1=-0-2     1747
     *  12111      906
     *   2=0=      198
     *     21       11
     *   2=01      201
     *    111       31
     *  20012     1257
     *    112       32
     *  1=-1=      353
     *   1-12      107
     *     12        7
     *     1=        3
     *    122       37
     * "You know, I never did ask the engineers why they did that. Instead of using digits four
     * through zero, the digits are 2, 1, 0, minus (written -), and double-minus (written =).
     * Minus is worth -1, and double-minus is worth -2."
     *
     * "So, because ten (in normal numbers) is two fives and no ones, in SNAFU it is written 20.
     * Since eight (in normal numbers) is two fives minus two ones, it is written 2=."
     *
     *
     * 1 % 5 = 1
     * 1 / 5 = 0
     *      1
     *
     * 2 % 5 = 2
     * 2 / 5 = 0
     *      2
     *
     * 3 % 5 = 0
     *      5 - 2 = 3
     * 3 / 5 = 0
     *      1=
     *
     * 4 % 5 = 0
     *      5 - 2 = 3
     *      5 - 1 = 4
     * 4 / 5 = 0
     * 1-
     *
     * 5 % 5 = 0
     * 5 / 5 = 1
     *      1 % 5 = 1
     *      1 / 5 = 0
     * 10
     *
     * 6 % 5 = 1
     * 6 / 5 = 1
     *      1 % 5 = 1
     *      1 / 5 = 0
     * 11
     *
     * 7
     * 12
     *
     * 8 % 5 = 3
     *      10 - 2 = 8
     *      =
     * 10 / 5 = 2
     * 2 % 5 = 2
     *      2
     * 2 / 5 = 0
     * 2=
     *
     */
    private static String intToSnafu(final long val) {
        long remaining = val;

        Deque<Character> result = new ArrayDeque<>();

        while (remaining > 0) {
            // try to see if it's 0,1,2
            long remainder = remaining % 5;
            if (remainder >= 0 && remainder <= 2) {
                result.addFirst((char)(remainder + '0'));
            } else if (remainder == 3) {
                result.addFirst('=');
                remaining += 2;
//                remainder = remaining % 10;
//                if (remainder == 3) {
//                    remaining += 5;
//                } else if (remainder == 8) {
//                    remaining += 10;
//                } else {
//                    throw new IllegalStateException("shouldn't happen remaining=" + remaining + " remainder=" + remainder);
//                }
            } else if (remainder == 4) {
                result.addFirst('-');
                remaining += 1;
//                remainder = remaining % 10;
//                if (remainder == 4) {
//                    remaining += 5;
//                } else if (remainder == 9) {
//                    remaining += 10;
//                } else {
//                    throw new IllegalStateException("shouldn't happen remaining=" + remaining + " remainder=" + remainder);
//                }
            } else {
                throw new IllegalStateException("shouldn't happen remaining=" + remaining + " remainder=" + remainder);
            }

            remaining = remaining / 5;
        }

        StringBuilder sb = new StringBuilder();
        while (!result.isEmpty()) {
            sb.append(result.removeFirst());
        }
        return sb.toString();
    }

    private static String part1(String input) {
        return intToSnafu(
            input.lines()
            .mapToLong(Day25::snafuToInt)
            .sum()
        );
    }

    private static long part2(String input) {
        return 0;
    }

    private static void check(long expectedVal, String expectedSnafu) {
        long actualVal = snafuToInt(expectedSnafu);
        String actualSnafu = intToSnafu(expectedVal);

        if (expectedVal != actualVal) {
            System.out.println(
                "expectedVal=" + expectedVal
                + " actualVal=" + actualVal
            );
        }
        if (!expectedSnafu.equals(actualSnafu)) {
            System.out.println(
                    "expectedSnafu=" + expectedSnafu
                    + " actualSnafu=" + actualSnafu
                    + " (expectedVal=" + expectedVal + ")"
            );
        }
    }

    public static void main(String[] args) throws IOException {
        int day = 25;
        String sampleInput = Files.readString(java.nio.file.Path.of("input/day_"+day+"_sample.txt"));
        String realInput = Files.readString(java.nio.file.Path.of("input/day_"+day+".txt"));

        DEBUG = true;

        check(1, "1");
        check(2, "2");
        check(3, "1=");
        check(4, "1-");
        check(5, "10");
        check(6, "11");
        check(7, "12");
        check(8, "2=");
        check(9, "2-");
        check(10, "20");
        check(15, "1=0");
        check(20, "1-0");
        check(2022, "1=11-2");
        check(12345, "1-0---0");
        check(314159265, "1121-1110-1=0");


        check(1747, "1=-0-2");
        check(906, "12111");
        check(198, "2=0=");
        check(11, "21");
        check(201, "2=01");
        check(31, "111");
        check(1257, "20012");
        check(32, "112");
        check(353, "1=-1=");
        check(107, "1-12");
        check(7, "12");
        check(3, "1=");
        check(37, "122");


//        part1(sampleInput);

        DEBUG = false;
        System.out.println("Expected: "
            + Files.readString(java.nio.file.Path.of("input/day_"+day+"_sample_part1_expected.txt")));
        System.out.println("Actual:   "
            + part1(sampleInput));
        System.out.println("Solution: "
            + part1(realInput));

//        System.out.println("Expected: "
//            + Files.readString(java.nio.file.Path.of("input/day_"+day+"_sample_part2_expected.txt")));
//        System.out.println("Actual:   " +  part2(sampleInput));
//        System.out.println("Solution: " +  part2(realInput));

    }
}
