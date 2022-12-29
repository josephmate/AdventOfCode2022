package aoc;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

/**
 * <a href="https://adventofcode.com/2022/day/4>Day 5: Supply Stacks</a>
 */
public class Day5 implements Solution {

    @Override
    public String part1(String input) {
        return String.valueOf(part1Impl(input));
    }

    @Override
    public String part2(String input) {
        return String.valueOf(part2Impl(input));
    }

    record MoveInstruction(
        int howMany,
        int from,
        int to){ }

    record PuzzleInput (
        List<Deque<Character>> stacks,
        List<MoveInstruction> moveInstructions) { }


    /*
     *     [D]
     * [N] [C]
     * [Z] [M] [P]
     *  1   2   3
     *
     * move 1 from 2 to 1
     * move 3 from 1 to 3
     * move 2 from 2 to 1
     * move 1 from 1 to 2
     *
                        [B]     [L]     [S]
                [Q] [J] [C]     [W]     [F]
            [F] [T] [B] [D]     [P]     [P]
            [S] [J] [Z] [T]     [B] [C] [H]
            [L] [H] [H] [Z] [G] [Z] [G] [R]
        [R] [H] [D] [R] [F] [C] [V] [Q] [T]
        [C] [J] [M] [G] [P] [H] [N] [J] [D]
        [H] [B] [R] [S] [R] [T] [S] [R] [L]
         1   2   3   4   5   6   7   8   9
     */
    private static PuzzleInput parse(String input) {
        Iterator<String> lines = input.lines().iterator();

        final int numStacks;
        final List<String> stackBuffer = new ArrayList<>();
        while (true) {
            String line = lines.next();
            if (line.contains("1")) {
                numStacks = Arrays.stream(line.split(" "))
                    .filter(column -> !column.isBlank())
                    .mapToInt(Integer::parseInt)
                    .max()
                    .orElseThrow();
                break;
            }
            stackBuffer.add(line);
        }

        List<Deque<Character>> stacks = new ArrayList<>();
        for (int i = 0; i < numStacks; i++) {
            stacks.add(new ArrayDeque<>());
        }

        for (int i = stackBuffer.size()-1; i >= 0; i--) {
            String line = stackBuffer.get(i);
            for (int stackId = 0; stackId < numStacks; stackId++) {
                // 01234567890123456789012345678901234
                // [H] [B] [R] [S] [R] [T] [S] [R] [L]
                //  1   5   9   13 17
                int characterPosn = stackId*4 + 1;
                if (characterPosn < line.length()) {
                    char c = line.charAt(characterPosn);
                    if (c != ' ') {
                        stacks.get(stackId).addLast(c);
                    }
                }
            }
        }

        // ignore the empty line
        lines.next();

        List<MoveInstruction> moveInstructions = new ArrayList<>();
        while (lines.hasNext()) {
            String line = lines.next();
            try {
                // move 1 from 1 to 2
                //   0  1   2  3 4  5
                String[] columns = line.split(" ");
                int howMany = Integer.parseInt(columns[1]);
                int from = Integer.parseInt(columns[3]);
                int to = Integer.parseInt(columns[5]);
                moveInstructions.add(new MoveInstruction(howMany, from, to));
            } catch (Exception e) {
                throw new RuntimeException("failed on line: " + line, e);
            }
        }

        return new PuzzleInput(stacks, moveInstructions);
    }

    private static String part1Impl(String s) {
        PuzzleInput puzzleInput = parse(s);
        //System.out.println(puzzleInput);

        for(MoveInstruction moveInstruction : puzzleInput.moveInstructions) {
            for (int i = 0; i < moveInstruction.howMany; i++) {
                puzzleInput.stacks.get(moveInstruction.to-1)
                    .addLast(puzzleInput.stacks.get(moveInstruction.from-1).removeLast());
            }
            //System.out.println(puzzleInput);
        }

        StringBuilder result = new StringBuilder();
        for (Deque<Character> stack : puzzleInput.stacks) {
            // result.append(stack.peek()); // BUG
            result.append(stack.getLast());
        }
        return result.toString();
    }

    private static String part2Impl(String s) {
        PuzzleInput puzzleInput = parse(s);
        //System.out.println(puzzleInput);

        for(MoveInstruction moveInstruction : puzzleInput.moveInstructions) {
            List<Character> crateBuffer = new ArrayList<>();
            for (int i = 0; i < moveInstruction.howMany; i++) {
                crateBuffer.add(puzzleInput.stacks.get(moveInstruction.from-1).removeLast());
            };
            Collections.reverse(crateBuffer);
            for (char c : crateBuffer) {
                puzzleInput.stacks.get(moveInstruction.to-1).addLast(c);
            }

            //System.out.println(puzzleInput);
        }

        StringBuilder result = new StringBuilder();
        for (Deque<Character> stack : puzzleInput.stacks) {
            // result.append(stack.peek()); // BUG
            result.append(stack.getLast());
        }
        return result.toString();
    }

}
