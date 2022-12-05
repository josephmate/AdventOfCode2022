package aoc;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.sun.net.httpserver.SimpleFileServer;
import jdk.incubator.concurrent.StructuredTaskScope;


public class MainUsingWebserver {

    record DayData(
        int day,
        String sample,
        String input,
        String samplePart1Expected,
        String samplePart2Expected
    ) {

    }

    private static String get(int port, String path) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder().build();
        return client.send(
            HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:" + port + "/" + path))
                .timeout(Duration.ofMinutes(2))
                .GET()
                .build(),
            HttpResponse.BodyHandlers.ofString()
        ).body();
    }

    private static DayData retrieveDayData(int port, int day) throws InterruptedException, ExecutionException {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            Future<String> sample               = scope.fork(() -> get(port, "day_" + day + "_sample.txt"));
            Future<String> input               = scope.fork(() -> get(port, "day_" + day + ".txt"));
            Future<String> samplePart1Expected = scope.fork(() -> get(port,  "day_" + day + "_sample_part1_expected.txt"));
            Future<String> samplePart2Expected = scope.fork(() -> get(port, "day_" + day + "_sample_part2_expected.txt"));

            scope.join();           // Join both forks
            scope.throwIfFailed();  // ... and propagate errors

            // Here, both forks have succeeded, so compose their results
            return new DayData(day, sample.get(), input.get(), samplePart1Expected.get(), samplePart2Expected.get());
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        var day = Integer.parseInt(args[0]);

        var solution = switch(day) {
            case 1 -> new Day1();
            case 2 -> new Day2();
            case 3 -> new Day3();
            case 4 -> new Day4();
            default -> null;
        };

        // SimpleHTTP Server from JDK18
        InetSocketAddress serverAddress = new InetSocketAddress(0);
        var fileServer = SimpleFileServer.createFileServer(
            serverAddress,
            Path.of("input/").toAbsolutePath(),
            SimpleFileServer.OutputLevel.INFO);

        final DayData dayData;
        try {
            fileServer.start();
            final int port = fileServer.getAddress().getPort();
            System.out.println("FilerServer running on port: " + port);
            dayData = retrieveDayData(port, day);
        } finally {
            fileServer.stop(0);
        }

        System.out.println("Expected: " + dayData.samplePart1Expected);
        System.out.println("Actual:   " + solution.part1(dayData.sample));
        System.out.println(solution.part1(dayData.input));

        System.out.println("Expected: " + dayData.samplePart2Expected);
        System.out.println("Actual:   " + solution.part2(dayData.sample));
        System.out.println(solution.part2(dayData.input));
    }
}