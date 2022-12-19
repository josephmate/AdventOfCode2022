package aoc;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * --- Day 18: Boiling Boulders ---
 *
 * You and the elephants finally reach fresh air. You've emerged near the base of a large volcano
 * that seems to be actively erupting! Fortunately, the lava seems to be flowing away from you and
 * toward the ocean.
 *
 * Bits of lava are still being ejected toward you, so you're sheltering in the cavern exit a
 * little longer. Outside the cave, you can see the lava landing in a pond and hear it loudly
 * hissing as it solidifies.
 *
 * Depending on the specific compounds in the lava and speed at which it cools, it might be forming
 * obsidian! The cooling rate should be based on the surface area of the lava droplets, so you take
 * a quick scan of a droplet as it flies past you (your puzzle input).
 *
 * Because of how quickly the lava is moving, the scan isn't very good; its resolution is quite low
 * and, as a result, it approximates the shape of the lava droplet with 1x1x1 cubes on a 3D grid,
 * each given as its x,y,z position.
 *
 * To approximate the surface area, count the number of sides of each cube that are not immediately
 * connected to another cube. So, if your scan were only two adjacent cubes like 1,1,1 and 2,1,1,
 * each cube would have a single side covered and five sides exposed, a total surface area of 10
 * sides.
 *
 * Here's a larger example:
 *
 * 2,2,2
 * 1,2,2
 * 3,2,2
 * 2,1,2
 * 2,3,2
 * 2,2,1
 * 2,2,3
 * 2,2,4
 * 2,2,6
 * 1,2,5
 * 3,2,5
 * 2,1,5
 * 2,3,5
 *
 * In the above example, after counting up all the sides that aren't connected to another cube,
 * the total surface area is 64.
 *
 * What is the surface area of your scanned lava droplet?
 *
 * --- Part Two ---
 *
 * Something seems off about your calculation. The cooling rate depends on exterior surface area,
 * but your calculation also included the surface area of air pockets trapped in the lava droplet.
 *
 * Instead, consider only cube sides that could be reached by the water and steam as the lava
 * droplet tumbles into the pond. The steam will expand to reach as much as possible,
 * completely displacing any air on the outside of the lava droplet but never expanding diagonally.
 *
 * In the larger example above, exactly one cube of air is trapped within the lava droplet
 * (at 2,2,5), so the exterior surface area of the lava droplet is 58.
 *
 * What is the exterior surface area of your scanned lava droplet?
 */
public class Day18 {

    private static boolean DEBUG = false;

    record Cube (int x, int y, int z) {

    }

    private static Cube parseLine(String line) {
        String [] cols = line.split(",");
        return new Cube(
            Integer.parseInt(cols[0]),
            Integer.parseInt(cols[1]),
            Integer.parseInt(cols[2])
        );
    }

    private static List<Cube> parse(String input) {
        return input.lines()
            .map(Day18::parseLine)
            .collect(Collectors.toList());
    }

    private static List<Cube> generateAdjacentCubes(Cube cube) {
        return List.of(
            new Cube(cube.x + 1, cube.y + 0, cube.z + 0),
            new Cube(cube.x - 1, cube.y + 0, cube.z + 0),
            new Cube(cube.x + 0, cube.y + 1, cube.z + 0),
            new Cube(cube.x + 0, cube.y - 1, cube.z + 0),
            new Cube(cube.x + 0, cube.y + 0, cube.z + 1),
            new Cube(cube.x + 0, cube.y + 0, cube.z - 1)
        );
    }

    private static long part1(String input) {
        Set<Cube> cubes = new HashSet<>(parse(input));

        int count = 0;
        for (Cube cube : cubes) {
            List<Cube> adjacentCubes = generateAdjacentCubes(cube);
            for (Cube adjacentCube : adjacentCubes) {
                if (!cubes.contains(adjacentCube)) {
                    count++;
                }
            }
        }


        return count;
    }

    private static boolean canReachBoarder(
        Cube cube,
        Set<Cube> cubes,
        int minX,
        int maxX,
        int minY,
        int maxY,
        int minZ,
        int maxZ
    ) {
        Set<Cube> visited = new HashSet<>();
        Deque<Cube> queue = new ArrayDeque<>();
        visited.add(cube);
        queue.addLast(cube);

        while(!queue.isEmpty()) {
            Cube currentCube = queue.removeFirst();
            if (
                currentCube.x > maxX
                || currentCube.x < minY
                || currentCube.y > maxY
                || currentCube.y < minY
                || currentCube.z > maxZ
                || currentCube.z < minZ
            ) {
                return true; // we've reached outside the most conservative boundary
            }

            generateAdjacentCubes(currentCube).stream()
                .filter(adjCube -> !cubes.contains(adjCube))
                .filter(adjCube -> !visited.contains(adjCube))
                .forEach(adjCube -> {
                    visited.add(adjCube);
                    queue.addLast(adjCube);
                });
        }

        return false;
    }

    /*
    2,2,2
    1,2,2
    3,2,2
    2,1,2
    2,3,2
    2,2,1
    2,2,3
    2,2,4
    2,2,6
    1,2,5
    3,2,5
    2,1,5
    2,3,5

    has airpocket at 2,2,5
     */
    private static long part2(String input) {
        Set<Cube> cubes = new HashSet<>(parse(input));

        int minX = cubes.stream().mapToInt(Cube::x).min().orElseThrow();
        int maxX = cubes.stream().mapToInt(Cube::x).max().orElseThrow();
        int minY = cubes.stream().mapToInt(Cube::y).min().orElseThrow();
        int maxY = cubes.stream().mapToInt(Cube::y).max().orElseThrow();
        int minZ = cubes.stream().mapToInt(Cube::z).min().orElseThrow();
        int maxZ = cubes.stream().mapToInt(Cube::z).max().orElseThrow();


        int count = 0;
        for (Cube cube : cubes) {
            List<Cube> adjacentCubes = generateAdjacentCubes(cube);
            for (Cube adjacentCube : adjacentCubes) {
                if (
                    !cubes.contains(adjacentCube)
                    && canReachBoarder(
                        adjacentCube,
                        cubes,
                        minX,
                        maxX,
                        minY,
                        maxY,
                        minZ,
                        maxZ
                        )
                ) {
                    count++;
                }
            }
        }


        return count;
    }

    public static void main(String[] args) throws IOException {

        DEBUG = true;
        String sampleInput = Files.readString(java.nio.file.Path.of("input/day_18_sample.txt"));
        String sample2Input = Files.readString(java.nio.file.Path.of("input/day_18_sample2.txt"));
        String realInput = Files.readString(java.nio.file.Path.of("input/day_18.txt"));
        part1(sampleInput);

        DEBUG = false;
        System.out.println("Expected: "
            + Files.readString(java.nio.file.Path.of("input/day_18_sample_part1_expected.txt")));
        System.out.println("Actual:   "
            + part1(sampleInput));
        System.out.println("Expected: "
            + Files.readString(java.nio.file.Path.of("input/day_18_sample2_part1_expected.txt")));
        System.out.println("Actual:   "
            + part1(sample2Input));
        System.out.println("Solution: "
            + part1(realInput));

        System.out.println("Expected: "
            + Files.readString(java.nio.file.Path.of("input/day_18_sample2_part2_expected.txt")));
        System.out.println("Actual:   " +  part2(sample2Input));
        System.out.println("Solution: " +  part2(realInput));

    }
}
