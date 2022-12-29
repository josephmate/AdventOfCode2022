package aoc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <a href="https://adventofcode.com/2022/day/15>Day 15: Beacon Exclusion Zone</a>
 */
public class Day15 {

    private static boolean DEBUG = false;


    record Path(Coord coord, int cost) {

    }

    record Coord(int x, int y) {

    }

    record SensorReading(Coord sensor, Coord beacon) {

    }

    private static int parseVal(String assignmentStr) {
        String[] cols = assignmentStr.split("=");
        String valueStr = cols[1];
        if (valueStr.endsWith(",") || valueStr.endsWith(":")) {
            return Integer.parseInt(valueStr.substring(0, valueStr.length()-1));
        } else {
            return Integer.parseInt(valueStr);
        }
    }

    private static SensorReading parseLine(String line) {
        //   0    1   2    3     4       5     6  7   8     9
        // Sensor at x=2, y=18: closest beacon is at x=-2, y=15

        String[] cols = line.split(" ");


        return new SensorReading(
            new Coord(parseVal(cols[2]), parseVal(cols[3])),
            new Coord(parseVal(cols[8]), parseVal(cols[9]))
        );
    }

    private static List<SensorReading> parseInput (String input) {
        return input.lines()
            .map(Day15::parseLine)
            .collect(Collectors.toList());
    }

    private static int calcManDist(Coord a, Coord b) {
        return Math.abs(a.x - b.x)
            + Math.abs(a.y - b.y);
    }

    private static Stream<Coord> generateMoves(
        Coord coord
    ) {
        // moving up (^), down (v), left (<), or right (>)

        return Stream.of(
            new Coord(coord.x-1, coord.y), // up
            new Coord(coord.x+1, coord.y), // down
            new Coord(coord.x, coord.y+1), // right
            new Coord(coord.x, coord.y-1) // left
        );
    }

    private static Set<Coord> fillWithBFS (
        SensorReading sensorReading
    ) {
        Set<Coord> visited = new HashSet<>();
        int maxManDist = calcManDist(sensorReading.sensor, sensorReading.beacon);
        Deque<Path> queue = new ArrayDeque<>();
        queue.add(new Path(sensorReading.sensor, 0));
        visited.add(sensorReading.sensor);

        while (!queue.isEmpty()) {
            Path path = queue.removeFirst();
            int newManDist = path.cost + 1;

            if (newManDist > maxManDist) {
                continue;
            }

            List<Path> newGoodPaths = generateMoves(path.coord)
                .filter(potentialCoord -> !visited.contains(potentialCoord))
                .map(goodCoord -> new Path(goodCoord, newManDist))
                .collect(Collectors.toList());

            for (Path newGoodPath : newGoodPaths) {
                visited.add(newGoodPath.coord);
                queue.addLast(newGoodPath);
            }
        }

        return visited;
    }

    private static long part1Slow(String input, int y) {
        List<SensorReading> sensorReadings = parseInput(input);

        // Fill in a map of coords
        Map<Coord, Character> map = new HashMap<>();
        sensorReadings.stream()
            .map(SensorReading::sensor)
            .forEach(sensor -> map.put(sensor, 'S'));
        sensorReadings.stream()
            .map(SensorReading::beacon)
            .forEach(sensor -> map.put(sensor, 'B'));

        int i = 0;
        for(SensorReading reading : sensorReadings) {
            fillWithBFS(reading)
                .stream()
                .filter(coord -> !map.containsKey(coord))
                .forEach(coord -> map.put(coord, '#'));
            i++;
            if (DEBUG) {
                System.out.println("Completed " + i + " out of " + sensorReadings.size());
            }
        }

        return map.entrySet().stream()
            .filter(entry -> entry.getKey().y == y)
            .filter(entry -> entry.getValue() == '#')
            .count();
    }

    private static boolean insideSensorRange(List<SensorReading> sensorReadings, Coord xy) {
        for (SensorReading sensorReading : sensorReadings) {
            int beaconManDist = calcManDist(sensorReading.sensor, sensorReading.beacon);
            int xyManDist = calcManDist(sensorReading.sensor, xy);
            if (xyManDist <= beaconManDist) {
                return true;
            }
        }

        return false;
    }

    private static long part1(String input, int y) {
        List<SensorReading> sensorReadings = parseInput(input);

        int minX = sensorReadings.stream()
            .map(SensorReading::sensor)
            .mapToInt(Coord::x)
            .min()
            .orElseThrow();
        int maxX = sensorReadings.stream()
            .map(SensorReading::sensor)
            .mapToInt(Coord::x)
            .max()
            .orElseThrow();
        int maxManDist = sensorReadings.stream()
            .mapToInt(sensorReading -> calcManDist(sensorReading.sensor, sensorReading.beacon))
            .max()
            .orElseThrow();

        if (DEBUG) {
            System.out.println(maxX - minX + 2 * maxManDist);
            System.out.println(Math.log(sensorReadings.size() * (maxX - minX + 2 * maxManDist)) / Math.log(2));
        }

        Set<Coord> notAvailable = new HashSet<>();
        sensorReadings.stream().map(SensorReading::sensor).forEach(notAvailable::add);
        sensorReadings.stream().map(SensorReading::beacon).forEach(notAvailable::add);

        /*
         *                  1    1    2    2
         *        0    5    0    5    0    5
         *  9 ...#########################...
         * 10 ..####B######################..
         * 11 .###S#############.###########.
         */

        if (DEBUG) {
            System.out.println(minX - maxManDist);
            System.out.println(maxX+maxManDist);
            for (int x = minX - maxManDist; x <= maxX+maxManDist; x++) {
                System.out.print(Math.abs(x % 10));
            }
            System.out.println();
        }

        int count = 0;
        for (int x = minX - maxManDist; x <= maxX+maxManDist; x++) {
            Coord xy = new Coord(x,y);
            if (notAvailable.contains(xy)) {
                if (DEBUG) {
                    System.out.print('.');
                }
                continue;
            }
            if (insideSensorRange(sensorReadings, xy)) {
                count++;
                if (DEBUG) {
                    System.out.print('#');
                }
            } else {
                if (DEBUG) {
                    System.out.print('.');
                }
            }
        }
        if (DEBUG) {
            System.out.println();
        }

        return count;
    }

    private static Stream<Coord> calcSensorBoundary(SensorReading sensorReading) {
        Set<Coord> result = new HashSet<>();
        int manDist = calcManDist(sensorReading.sensor, sensorReading.beacon);
        int sensorBoundaryDist = manDist + 1;

        Coord top = new Coord(sensorReading.sensor.x, sensorReading.sensor.y-sensorBoundaryDist);
        Coord bottom = new Coord(sensorReading.sensor.x, sensorReading.sensor.y+sensorBoundaryDist);
        Coord left = new Coord(sensorReading.sensor.x-sensorBoundaryDist, sensorReading.sensor.y);
        Coord right = new Coord(sensorReading.sensor.x+sensorBoundaryDist, sensorReading.sensor.y);
        result.add(top);
        result.add(bottom);
        result.add(left);
        result.add(right);

        // top to left
        Coord current = top;
        while (!current.equals(left)) {
            result.add(current);
            current = new Coord(current.x-1, current.y+1);
        }
        // top to right
        current = top;
        while (!current.equals(right)) {
            result.add(current);
            current = new Coord(current.x+1, current.y+1);
        }
        // bottom to left
        current = bottom;
        while (!current.equals(left)) {
            result.add(current);
            current = new Coord(current.x-1, current.y-1);
        }
        // bottom to right
        current = bottom;
        while (!current.equals(right)) {
            result.add(current);
            current = new Coord(current.x+1, current.y-1);
        }

        return result.stream();
    }


    private static void part2(String input, int maxValue) {
        List<SensorReading> sensorReadings = parseInput(input);
        List<Coord> solutions = sensorReadings.stream()
            .flatMap(Day15::calcSensorBoundary)
            .filter(potentialCoord -> potentialCoord.x >= 0)
            .filter(potentialCoord -> potentialCoord.y >= 0)
            .filter(potentialCoord -> potentialCoord.x <= maxValue)
            .filter(potentialCoord -> potentialCoord.y <= maxValue)
            .filter(potentialCoord -> !insideSensorRange(sensorReadings, potentialCoord))
            .collect(Collectors.toList());

        for (Coord solution: solutions) {
            // multiplying its x coordinate by 4000000 and then adding its y coordinate.
            System.out.println(solution + ": " + ((long)solution.x * 4000000L+ (long)solution.y));
        }
    }

    public static void main(String[] args) throws IOException {

        System.out.println(3303271L*4000000L+2906101L); // 1767503605 if using signed int 32. nice bug
        // 13213086906101

        System.out.println(Math.log(4000000L*4000000L)/Math.log(2));

        DEBUG = true;
        part1(Files.readString(java.nio.file.Path.of("input/day_15_sample.txt")),9);
        part1(Files.readString(java.nio.file.Path.of("input/day_15_sample.txt")),10);
        part1(Files.readString(java.nio.file.Path.of("input/day_15_sample.txt")),11);
        DEBUG = false;
        System.out.println("Expected: "
            + Files.readString(java.nio.file.Path.of("input/day_15_sample_part1_expected.txt")));
        System.out.println("Actual:   "
            + part1(Files.readString(java.nio.file.Path.of("input/day_15_sample.txt")),10));
        System.out.println("Solution: "
            + part1(Files.readString(java.nio.file.Path.of("input/day_15.txt")),2000000));

        System.out.println("Expected: "
            + Files.readString(java.nio.file.Path.of("input/day_15_sample_part2_expected.txt")));
        System.out.println("Actual:   ");
        part2(Files.readString(java.nio.file.Path.of("input/day_15_sample.txt")), 20);
        System.out.println("Solution: ");
        part2(Files.readString(java.nio.file.Path.of("input/day_15.txt")),4000000);

    }
}
