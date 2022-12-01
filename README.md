I plan to use this advent of code contest to get familiar with the newer java features.

This is a checklist of features I hope to use sorted by my interest:

| Feature                                                                              | GA | Beta | Where it's used? |
|--------------------------------------------------------------------------------------|----|------|-------|
|  [Record type](https://openjdk.java.net/jeps/395)                                    | 17 |      |       |
|  [Pattern Matching for instanceof](https://openjdk.java.net/jeps/394)                | 16 | 14   |       |
|  [Text blocks](https://openjdk.java.net/jeps/378)                                    | 15 | 13   |       |
|  [var type allowed in Lambda Parameters:](http://openjdk.java.net/jeps/323)          | 11 |      |       |
|  [Switch expressions](https://openjdk.java.net/jeps/361)                             | 14 | 12   |       |
|  [var type allowed for local variables:](http://openjdk.java.net/jeps/286)           | 10 |      |       |
|  [Collection factory methods](https://openjdk.java.net/jeps/269)                     |  9 |      |       |
|  jlink       |   14   |      |       |
|  Method References       |   8  |      |       |
|  Optional class       |  8  |      |       |
|  Lambda expressions       | 8    |      |       |
|  Functional interfaces       |  8   |      |       |
|  Stream API       | 8    |      |       |
|  Record patterns       |   19 |      |       |
|  Pattern Matching for switch – like instanceof for switch; switch is an expression and can be assigned      |          |      |       |
|  Foreign Function & Memory API (an alternative to JNI)       |    16   |      |       |
|  Launching Java files as scripts       |  11  |      |       |
|  Flow API (reactive streams)                                                         |  9 |      |       |
|  [New HTTP client API](https://docs.oracle.com/en/java/javase/11/docs/api/java.net.http/java/net/http/HttpClient.html)       |          |      |       |
|  new methods in CompletableFuture API (delay, timeout)                               | 9 |      | If I end up using HttpClient, I should combine it with this |
|  [Simple Web Server](https://openjdk.java.net/jeps/408)                              | 18 |      |       |
|  Vector API       |   16      |       |
|  Virtual threads       |   19     |       |
|  Interface private methods                                                           | 8  |      |       |
|  Interface Default and Static Methods                                                | 8  |      |       |
|  [Sealed classes](https://openjdk.java.net/jeps/409)                                 | 16 | 14   |       |
|  [Modules](https://openjdk.java.net/jeps/261)                                        |  9 |      | the best i'll get with this is using jlink  |
|  [Static members in inner classes](https://openjdk.java.net/jeps/409)                | 16 | 16   |       |
|  Effectively Final Variables      |    8  |      |       |
|  Repeating Annotations       |   8     |      |       |
|  Structured concurrency      |  19  |      |       |
|  [CompactNumberFormat class](https://docs.oracle.com/en/java/javase/12/docs/api/java.base/java/text/CompactNumberFormat.html)       | 12 |      |       |
|  [Code Snippets in Java API Documentation](https://openjdk.java.net/jeps/413)        | 18 |      |       |
|  Stream API improvements:                                                            |    |      |       |
|  takeWhile                                                                           | 9  |      |       |
|  dropWhile                                                                           | 9  |      |       |
|  ofNullable                                                                          | 9  |      |       |
|  iterate with condition                                                              | 9  |      |       |
|  New String methods:                                                                 |    |      |       |
|  formatted                                                                           | 15 |      |       |
|  stripIndent                                                                         | 15 |      |       |
|  translateEscapes                                                                    | 15 |      |       |
|  indent                                                                              | 12 |      |       |
|  transform                                                                           | 12 |      |       |
|  repeat                                                                              | 12 |      |       |
|  isBlank                                                                             | 12 |      |       |
|  strip                                                                               | 12 |      |       |
|  lines                                                                               | 12 |      |       |
|  New Date Time API       | 8   |      |   SKIP: probably no date related problems    |
|  since and forRemoval in @Deprecated       |  9   |      |  SKIP: not planning on deprecating stuff during this contest ;) |
|  [Process API updates (detailed info about processes, e.g. ID, onExit, destroy)](https://openjdk.java.net/jeps/102)       |  9 |      | SKIP: I don't plan to do process management in this contest |
|  this.getClass().getPackageName()                                                    | 9 |      | SKIP: I don't expect to need reflection     |
|  [Stack-Walking API](https://openjdk.java.net/jeps/259)                              |  9 |      | SKIP: I don't expect to need to traverse the call stack |
|  [Multi-Resolution Image API](https://docs.oracle.com/javase/9/docs/api/java/awt/image/MultiResolutionImage.html)       |          |      |   SKIP: I don't expect the contest to have image processing   |
|  [TLS v1.3](https://openjdk.java.net/jeps/332)                                       | 11 |      | SKIP: although security is important, is doesn't seem applicable to this contest      |
|  [Context-Specific Deserialization Filters](https://openjdk.java.net/jeps/415)       | 17 |      | SKIP: I don't use serialization that often. usually using protobufs or json instead   |
|  [UTF-8 by Default](https://openjdk.java.net/jeps/400)                               | 18 |      | SKIP: As long as I use strings, this one is covered. not very interesting    |

References
[1]  [NEW FEATURES BETWEEN JAVA 8 AND JAVA 19 by Dávid Csákvári](https://ondro.inginea.eu/index.php/new-features-in-java-versions-since-java-8/)
