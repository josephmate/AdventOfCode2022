import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) throws IOException {
        var day = Integer.parseInt(args[0]);
        var sample = Files.readString(Path.of("input/day_"+day+"_sample.txt"));
        var input = Files.readString(Path.of("input/day_"+day+".txt"));
        var samplePart1Expected = Files.readString(Path.of("input/day_"+day+"_sample_part1_expected.txt"));

        var solution = switch(day) {
            case 1 -> new Day1();
            case 2 -> new Day2();
            case 3 -> new Day3();
            default -> null;
        };

        System.out.println("Expected: " + samplePart1Expected);
        System.out.println("Actual:   " + solution.part1(sample));
        System.out.println(solution.part1(input));

        var samplePart2ExpectedPath = Path.of("input/day_"+day+"_sample_part2_expected.txt");
        if (samplePart2ExpectedPath.toFile().exists()) {
            var samplePart2Expected = Files.readString(samplePart2ExpectedPath);
            System.out.println("Expected: " + samplePart2Expected);
            System.out.println("Actual:   " + solution.part2(sample));
            System.out.println(solution.part2(input));
        }
    }
}