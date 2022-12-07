package aoc;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

/**
 * --- Day 5: Supply Stacks ---
 *
 * The expedition can depart as soon as the final supplies have been unloaded from the ships.
 * Supplies are stored in stacks of marked crates, but because the needed supplies are buried under
 * many other crates, the crates need to be rearranged.
 *
 * The ship has a giant cargo crane capable of moving crates between stacks. To ensure none of the
 * crates get crushed or fall over, the crane operator will rearrange them in a series of
 * carefully-planned steps. After the crates are rearranged, the desired crates will be at the top
 * of each stack.
 *
 * The Elves don't want to interrupt the crane operator during this delicate procedure, but they
 * forgot to ask her which crate will end up where, and they want to be ready to unload them as soon
 * as possible so they can embark.
 *
 * They do, however, have a drawing of the starting stacks of crates and the rearrangement
 * procedure (your puzzle input). For example:
 *
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
 * In this example, there are three stacks of crates. Stack 1 contains two crates: crate Z is on
 * the bottom, and crate N is on top. Stack 2 contains three crates; from bottom to top, they are
 * crates M, C, and D. Finally, stack 3 contains a single crate, P.
 *
 * Then, the rearrangement procedure is given. In each step of the procedure, a quantity of crates
 * is moved from one stack to a different stack. In the first step of the above rearrangement
 * procedure, one crate is moved from stack 2 to stack 1, resulting in this configuration:
 *
 * [D]
 * [N] [C]
 * [Z] [M] [P]
 *  1   2   3
 *
 * In the second step, three crates are moved from stack 1 to stack 3. Crates are moved one at a
 * time, so the first crate to be moved (D) ends up below the second and third crates:
 *
 *         [Z]
 *         [N]
 *     [C] [D]
 *     [M] [P]
 *  1   2   3
 *
 * Then, both crates are moved from stack 2 to stack 1. Again, because crates are moved one at a
 * time, crate C ends up below crate M:
 *
 *         [Z]
 *         [N]
 * [M]     [D]
 * [C]     [P]
 *  1   2   3
 *
 * Finally, one crate is moved from stack 1 to stack 2:
 *
 *         [Z]
 *         [N]
 *         [D]
 * [C] [M] [P]
 *  1   2   3
 *
 * The Elves just need to know which crate will end up on top of each stack; in this example,
 * the top crates are C in stack 1, M in stack 2, and Z in stack 3, so you should combine these
 * together and give the Elves the message CMZ.
 *
 * After the rearrangement procedure completes, what crate ends up on top of each stack?
 *
 * --- Part Two ---
 *
 * As you watch the crane operator expertly rearrange the crates, you notice the process isn't
 * following your prediction.
 *
 * Some mud was covering the writing on the side of the crane, and you quickly wipe it away. The
 * crane isn't a CrateMover 9000 - it's a CrateMover 9001.
 *
 * The CrateMover 9001 is notable for many new and exciting features: air conditioning, leather
 * seats, an extra cup holder, and the ability to pick up and move multiple crates at once.
 *
 * Again considering the example above, the crates begin in the same configuration:
 *
 *     [D]
 * [N] [C]
 * [Z] [M] [P]
 *  1   2   3
 *
 * Moving a single crate from stack 2 to stack 1 behaves the same as before:
 *
 * [D]
 * [N] [C]
 * [Z] [M] [P]
 *  1   2   3
 *
 * However, the action of moving three crates from stack 1 to stack 3 means that those three moved
 * crates stay in the same order, resulting in this new configuration:
 *
 *         [D]
 *         [N]
 *     [C] [Z]
 *     [M] [P]
 *  1   2   3
 *
 * Next, as both crates are moved from stack 2 to stack 1, they retain their order as well:
 *
 *         [D]
 *         [N]
 * [C]     [Z]
 * [M]     [P]
 *  1   2   3
 *
 * Finally, a single crate is still moved from stack 1 to stack 2, but now it's crate C that gets
 * moved:
 *
 *         [D]
 *         [N]
 *         [Z]
 * [M] [C] [P]
 *  1   2   3
 *
 * In this example, the CrateMover 9001 has put the crates in a totally different order: MCD.
 *
 * Before the rearrangement process finishes, update your simulation so that the Elves know where
 * they should stand to be ready to unload the final supplies. After the rearrangement procedure
 * completes, what crate ends up on top of each stack?
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
