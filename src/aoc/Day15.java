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
 * --- Day 15: Beacon Exclusion Zone ---
 *
 * You feel the ground rumble again as the distress signal leads you to a large network of
 * subterranean tunnels. You don't have time to search them all, but you don't need to:
 * your pack contains a set of deployable sensors that you imagine were originally built to locate lost Elves.
 *
 * The sensors aren't very powerful, but that's okay; your handheld device indicates that you're
 * close enough to the source of the distress signal to use them. You pull the emergency sensor
 * system out of your pack, hit the big button on top, and the sensors zoom off down the tunnels.
 *
 * Once a sensor finds a spot it thinks will give it a good reading, it attaches itself to a hard
 * surface and begins monitoring for the nearest signal source beacon. Sensors and beacons always
 * exist at integer coordinates. Each sensor knows its own position and can determine the position
 * of a beacon precisely; however, sensors can only lock on to the one beacon closest to the sensor
 * as measured by the Manhattan distance. (There is never a tie where two beacons are the same
 * distance to a sensor.)
 *
 * It doesn't take long for the sensors to report back their positions and closest beacons
 * (your puzzle input). For example:
 *
 * Sensor at x=2, y=18: closest beacon is at x=-2, y=15
 * Sensor at x=9, y=16: closest beacon is at x=10, y=16
 * Sensor at x=13, y=2: closest beacon is at x=15, y=3
 * Sensor at x=12, y=14: closest beacon is at x=10, y=16
 * Sensor at x=10, y=20: closest beacon is at x=10, y=16
 * Sensor at x=14, y=17: closest beacon is at x=10, y=16
 * Sensor at x=8, y=7: closest beacon is at x=2, y=10
 * Sensor at x=2, y=0: closest beacon is at x=2, y=10
 * Sensor at x=0, y=11: closest beacon is at x=2, y=10
 * Sensor at x=20, y=14: closest beacon is at x=25, y=17
 * Sensor at x=17, y=20: closest beacon is at x=21, y=22
 * Sensor at x=16, y=7: closest beacon is at x=15, y=3
 * Sensor at x=14, y=3: closest beacon is at x=15, y=3
 * Sensor at x=20, y=1: closest beacon is at x=15, y=3
 *
 * So, consider the sensor at 2,18; the closest beacon to it is at -2,15. For the sensor at 9,16,
 * the closest beacon to it is at 10,16.
 *
 * Drawing sensors as S and beacons as B, the above arrangement of sensors and beacons looks like this:
 *
 *                1    1    2    2
 *      0    5    0    5    0    5
 *  0 ....S.......................
 *  1 ......................S.....
 *  2 ...............S............
 *  3 ................SB..........
 *  4 ............................
 *  5 ............................
 *  6 ............................
 *  7 ..........S.......S.........
 *  8 ............................
 *  9 ............................
 * 10 ....B.......................
 * 11 ..S.........................
 * 12 ............................
 * 13 ............................
 * 14 ..............S.......S.....
 * 15 B...........................
 * 16 ...........SB...............
 * 17 ................S..........B
 * 18 ....S.......................
 * 19 ............................
 * 20 ............S......S........
 * 21 ............................
 * 22 .......................B....
 *
 * This isn't necessarily a comprehensive map of all beacons in the area, though.
 * Because each sensor only identifies its closest beacon, if a sensor detects a beacon,
 * you know there are no other beacons that close or closer to that sensor.
 * There could still be beacons that just happen to not be the closest beacon to any sensor.
 * Consider the sensor at 8,7:
 *
 *                1    1    2    2
 *      0    5    0    5    0    5
 * -2 ..........#.................
 * -1 .........###................
 *  0 ....S...#####...............
 *  1 .......#######........S.....
 *  2 ......#########S............
 *  3 .....###########SB..........
 *  4 ....#############...........
 *  5 ...###############..........
 *  6 ..#################.........
 *  7 .#########S#######S#........
 *  8 ..#################.........
 *  9 ...###############..........
 * 10 ....B############...........
 * 11 ..S..###########............
 * 12 ......#########.............
 * 13 .......#######..............
 * 14 ........#####.S.......S.....
 * 15 B........###................
 * 16 ..........#SB...............
 * 17 ................S..........B
 * 18 ....S.......................
 * 19 ............................
 * 20 ............S......S........
 * 21 ............................
 * 22 .......................B....
 *
 * This sensor's closest beacon is at 2,10, and so you know there are no beacons that close
 * or closer (in any positions marked #).
 *
 * None of the detected beacons seem to be producing the distress signal,
 * so you'll need to work out where the distress beacon is by working out where it isn't. For now,
 * keep things simple by counting the positions where a beacon cannot possibly be along just a single row.
 *
 * So, suppose you have an arrangement of beacons and sensors like in the example above and,
 * just in the row where y=10, you'd like to count the number of positions a beacon cannot possibly exist.
 * The coverage from all sensors near that row looks like this:
 *
 *                  1    1    2    2
 *        0    5    0    5    0    5
 *  9 ...#########################...
 * 10 ..####B######################..
 * 11 .###S#############.###########.
 *
 * In this example, in the row where y=10, there are 26 positions where a beacon cannot be present.
 *
 * Consult the report from the sensors you just deployed. In the row where y=2000000,
 * how many positions cannot contain a beacon?
 *
 * --- Part Two ---
 *
 * Your handheld device indicates that the distress signal is coming from a beacon nearby.
 * The distress beacon is not detected by any sensor, but the distress beacon must have
 * x and y coordinates each no lower than 0 and no larger than 4000000.
 *
 * To isolate the distress beacon's signal, you need to determine its tuning frequency,
 * which can be found by multiplying its x coordinate by 4000000 and then adding its y coordinate.
 *
 * In the example above, the search space is smaller: instead, the x and y coordinates
 * can each be at most 20. With this reduced search area, there is only a single position
 * that could have a beacon: x=14, y=11. The tuning frequency for this distress beacon is 56000011.
 *
 * Find the only possible position for the distress beacon. What is its tuning frequency?
 *
 * That's not the right answer; your answer is too low. If you're stuck, make sure you're using the
 * full input data; there are also some general tips on the about page, or you can ask for hints on
 * the subreddit. Please wait one minute before trying again. (You guessed 1767503605.)
 * [Return to Day 15]
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
