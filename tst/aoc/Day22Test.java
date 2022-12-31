package aoc;

import static org.junit.Assert.*;

import java.nio.file.Files;

import org.junit.Test;

public class Day22Test {

    @Test
    public void test() throws Exception {
        String sampleInput = Files.readString(java.nio.file.Path.of("input/day_22_sample.txt"));
        assertEquals(
            5031,
            Day22.part2(sampleInput, Day22.sampleCubeDescription)
        );
    }
}