import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.SimpleFileServer;


public class MainUsingWebserver {

    public static void main(String[] args) throws IOException, InterruptedException {
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
        fileServer.start();
        int port = fileServer.getAddress().getPort();
        System.out.println("FilerServer running on port: " + port);
        try {
            HttpClient client = HttpClient.newBuilder().build();
            var sample = client.send(
                HttpRequest.newBuilder()
                    .uri(URI.create("http://127.0.0.1:" + port + "/day_" + day + "_sample.txt"))
                    .timeout(Duration.ofMinutes(2))
                    .GET()
                    .build(),
                HttpResponse.BodyHandlers.ofString()
            ).body();
            var input = client.send(
                HttpRequest.newBuilder()
                    .uri(URI.create("http://127.0.0.1:" + port + "/day_" + day + ".txt"))
                    .timeout(Duration.ofMinutes(2))
                    .GET()
                    .build(),
                HttpResponse.BodyHandlers.ofString()
            ).body();
            var samplePart1Expected = client.send(
                HttpRequest.newBuilder()
                    .uri(URI.create("http://127.0.0.1:" + port + "/day_" + day + "_sample_part1_expected.txt"))
                    .timeout(Duration.ofMinutes(2))
                    .GET()
                    .build(),
                HttpResponse.BodyHandlers.ofString()
            ).body();
            var samplePart2Expected = client.send(
                HttpRequest.newBuilder()
                    .uri(URI.create("http://127.0.0.1:" + port + "/day_" + day + "_sample_part2_expected.txt"))
                    .timeout(Duration.ofMinutes(2))
                    .GET()
                    .build(),
                HttpResponse.BodyHandlers.ofString()
            ).body();

            System.out.println("Expected: " + samplePart1Expected);
            System.out.println("Actual:   " + solution.part1(sample));
            System.out.println(solution.part1(input));

            System.out.println("Expected: " + samplePart2Expected);
            System.out.println("Actual:   " + solution.part2(sample));
            System.out.println(solution.part2(input));
        } finally {
            fileServer.stop(0);
        }
    }
}