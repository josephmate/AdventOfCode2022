package aoc;

import java.util.*;
import java.io.*;
import java.nio.file.*;
import java.nio.*;
import java.util.stream.*;

public class Day20 {

    private static class LL {
        int val;
        LL next;
        LL prev;

        LL(int val, LL next, LL prev) {

        }
    }


    private static int part1(String input) {
        List<Integer> values = input.lines()
            .map(Integer::parseInt)
            .collect(Collectors.toList());

        LL head = new LL(values.get(0), null, null);
        LL tail = head;

        for (int i = 1; i < values.size(); i++) {
            LL oldTail = tail;
            tail = new LL(values.get(i), null, oldTail);
            oldTail.next = tail;
        }


        return 0;
    }

    public static void main(String[] args) throws Exception {
        String sample = Files.readString(Path.of("sample.txt"));
        String input = Files.readString(Path.of("input.txt"));
        //System.out.println(sample);
        System.out.println("expected: 3");
        System.out.println("actual: " + part1(sample));
        System.out.println("solution " + part1(input));
    }
}