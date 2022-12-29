package aoc;

/**
 * <a href="https://adventofcode.com/2022/day/2>Day 2: Rock Paper Scissors</a>
 */
public class Day2 implements Solution {

    @Override
    public String part1(String input) {
        return String.valueOf(part1Impl(input));
    }

    @Override
    public String part2(String input) {
        return String.valueOf(part2Impl(input));
    }

    private static final int WIN = 6;
    private static final int DRAW = 3;
    private static final int LOSE = 0;

    //  (1 for Rock, 2 for Paper, and 3 for Scissors)
    private static final int ROCK = 1;
    private static final int PAPER = 2;
    private static final int SCISSORS = 3;

    private static int scorePart1(String[] round) {
        // A for Rock, B for Paper, and C for Scissors.
        // X for Rock, Y for Paper, and Z for Scissors.
        //  (1 for Rock, 2 for Paper, and 3 for Scissors)
        //  plus the score for the outcome of the round
        //         (0 if you lost, 3 if the round was a draw, and 6 if you won)
        return switch (round[0]) {
            case "A" -> switch (round[1]) { // ROCK 1
                case "X" -> ROCK + DRAW; // ROCK 1
                case "Y" -> PAPER + WIN; // PAPER 2
                case "Z" -> SCISSORS + LOSE;  // SCISSORS 3
                default -> throw new IllegalStateException();
            };
            case "B" -> switch (round[1]) { // PAPER 2
                case "X" -> ROCK+ LOSE; // ROCK 1
                case "Y" -> PAPER + DRAW; // PAPER 2
                case "Z" -> SCISSORS + WIN;  // SCISSORS 3
                default -> throw new IllegalStateException();
            };
            case "C" -> switch (round[1]) { // SCISSORS 3
                case "X" -> ROCK + WIN; // ROCK 1
                case "Y" -> PAPER + LOSE; // PAPER 2
                case "Z" -> SCISSORS + DRAW;  // SCISSORS 3
                default -> throw new IllegalStateException();
            };
            default -> throw new IllegalStateException();
        };
    }

    private static int part1Impl(String s) {
        return s.lines()
            .map(round -> round.split(" "))
            .mapToInt(Day2::scorePart1)
            .sum();
    }

    private static int scorePart2(String[] round) {
        // A for Rock, B for Paper, and C for Scissors.
        // X means you need to lose, Y means you need to end the round in a draw, and Z means you need to win
        //  (1 for Rock, 2 for Paper, and 3 for Scissors)
        //  plus the score for the outcome of the round
        //         (0 if you lost, 3 if the round was a draw, and 6 if you won)
        return switch (round[0]) {
            case "A" -> switch (round[1]) { // ROCK 1
                case "X" -> LOSE + SCISSORS;
                case "Y" -> DRAW + ROCK;
                case "Z" -> WIN + PAPER;
                default -> throw new IllegalStateException();
            };
            case "B" -> switch (round[1]) { // PAPER 2
                case "X" -> LOSE + ROCK;
                case "Y" -> DRAW + PAPER;
                case "Z" -> WIN + SCISSORS;
                default -> throw new IllegalStateException();
            };
            case "C" -> switch (round[1]) { // SCISSORS 3
                case "X" -> LOSE + PAPER;
                case "Y" -> DRAW + SCISSORS;
                case "Z" -> WIN + ROCK;
                default -> throw new IllegalStateException();
            };
            default -> throw new IllegalStateException();
        };
    }

    private static int part2Impl(String s) {
        return s.lines()
            .map(round -> round.split(" "))
            .mapToInt(Day2::scorePart2)
            .sum();
    }

}
