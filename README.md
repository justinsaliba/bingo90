# Introduction

This is an application that is used to generate Bingo 90 cards according to the requirements stipulated hereunder:

(Rephrased from the [challenge's requirements](https://github.com/lindar-open/ticket-generator-challenge/).)

* A strip contains 6 tickets
* A strip contains the numbers 1 to 90, no duplicates are allowed.
* A single ticket consists of 9 columns and 3 rows, with 5 numbers and 4 blank spaces in each row.
  * This means that for each ticket, there can be exactly 15 numbers and 12 blank spaces.
* Given the requirements above, a strip guarantees that a player is able to mark off a number whenever it is called out.
* Each ticket column consists of one, two or three numbers and never three blanks.
    - The first column contains numbers from 1 to 9 (only nine),
    - The second column numbers from 10 to 19 (ten), the third, 20 to 29 and so on up until
    - The last column, which contains numbers from 80 to 90 (eleven).
* Numbers in the ticket columns are ordered from top to bottom (ASC).

# Running the Project

To run the project, simply build the application via Maven, and execute the JAR's entry point:

```bash
$ ./mvnw clean package

$ java -jar target/bingo90-1.0.jar
```

By default, the app will generate 10000 strips and emit the time taken to do so.
Due to classloading, and just-in-time compilation typical of Java programs.

Keeping things simple, this app exposes a number of flags to manipulate the program's output, as follows:

- `--verbose` - Emits each ticket strip in the console. Best avoided when generating large amoutns of strip. Default, `false`.
- `--strips` - Determines the number of strips to generate per generation. Defaults to `10000`.
- `--generations` - Determines how many groups of strips are generated. This is best used with `--verbose=false`
  (or simply not specified at all), along with the desired number of strips to observe the average time taken by 
  the application to generate the specified number of strips. Defaults to `1` by default.

Example usage:

```bash
$ java -jar target/bingo90-1.0.jar --generations=10000 --strips=10000 
```

The command above will generate 10000 strips, 10000 times emitting, the average time taken to generate each step of 10000 strips.

> <span style="color:cornflowerblue">🛈 Note</span>
>
> Please ensure that you specify the `=` between the flag and its respective value. Failing to do so will lead to very ugly stack traces (:.

Example output:

```
2024-11-14 22:46:10 | main | INFO  | com.lindar.challenges.jsaliba.TicketGenerator | Started TicketGenerator in 0.671 seconds (process running for 0.929)
2024-11-14 22:46:11 | main | INFO  | com.lindar.challenges.jsaliba.TicketGenerator | Took 205 ms to generate 10000 ticket strips
2024-11-14 22:46:11 | main | INFO  | com.lindar.challenges.jsaliba.TicketGenerator | Took 98 ms to generate 10000 ticket strips
2024-11-14 22:46:11 | main | INFO  | com.lindar.challenges.jsaliba.TicketGenerator | Took 69 ms to generate 10000 ticket strips
2024-11-14 22:46:11 | main | INFO  | com.lindar.challenges.jsaliba.TicketGenerator | Took 69 ms to generate 10000 ticket strips
2024-11-14 22:46:11 | main | INFO  | com.lindar.challenges.jsaliba.TicketGenerator | Took 66 ms to generate 10000 ticket strips
2024-11-14 22:46:11 | main | INFO  | com.lindar.challenges.jsaliba.TicketGenerator | Took 66 ms to generate 10000 ticket strips
2024-11-14 22:46:11 | main | INFO  | com.lindar.challenges.jsaliba.TicketGenerator | Took 83 ms to generate 10000 ticket strips
2024-11-14 22:46:11 | main | INFO  | com.lindar.challenges.jsaliba.TicketGenerator | Took 93 ms to generate 10000 ticket strips
2024-11-14 22:46:11 | main | INFO  | com.lindar.challenges.jsaliba.TicketGenerator | Took 69 ms to generate 10000 ticket strips
2024-11-14 22:46:11 | main | INFO  | com.lindar.challenges.jsaliba.TicketGenerator | Took 63 ms to generate 10000 ticket strips
2024-11-14 22:46:11 | main | INFO  | com.lindar.challenges.jsaliba.TicketGenerator | Took 66 ms to generate 10000 ticket strips
2024-11-14 22:46:12 | main | INFO  | com.lindar.challenges.jsaliba.TicketGenerator | Took 67 ms to generate 10000 ticket strips
2024-11-14 22:46:12 | main | INFO  | com.lindar.challenges.jsaliba.TicketGenerator | Took 62 ms to generate 10000 ticket strips
2024-11-14 22:46:12 | main | INFO  | com.lindar.challenges.jsaliba.TicketGenerator | Took 92 ms to generate 10000 ticket strips
```

To generate a single ticket strip:

```bash
$ java -jar target/bingo90-1.0.jar --verbose --strips=1 
```

You will see an output similar to the below:

```
 -------------------------------------
| --  --  23  --  41  54  --  71  83  |
|  7  --  --  33  43  --  64  77  --  |
| --  16  --  39  --  55  69  --  89  |
 -------------------------------------

 -------------------------------------
|  2  11  --  38  --  --  63  76  --  |
|  4  --  27  --  40  --  65  --  81  |
| --  19  28  --  --  52  --  79  88  |
 -------------------------------------

 -------------------------------------
|  1  --  21  34  --  50  --  72  --  |
| --  14  24  --  47  56  --  --  84  |
|  9  15  --  35  48  --  60  --  --  |
 -------------------------------------

 -------------------------------------
|  3  --  --  30  44  --  67  70  --  |
| --  10  --  36  --  58  68  --  80  |
| --  --  20  --  49  59  --  78  90  |
 -------------------------------------

 -------------------------------------
|  5  --  25  --  46  --  61  --  86  |
| --  13  29  --  --  53  --  73  87  |
|  8  18  --  37  --  --  62  75  --  |
 -------------------------------------

 -------------------------------------
| --  12  22  --  42  51  --  --  82  |
| --  17  --  31  45  --  66  --  85  |
|  6  --  26  32  --  57  --  74  --  |
 -------------------------------------
```

# The Algorithm

The algorithm is split into four phases:

1. **Phase 1: Generation of the Number Pool**

   The number pool is effectively 9 groups of numbers as per the requirements.
   The application starts off by creating a `java.util.Map` of 9 keys, each
   representing a "number group". The key represents the column index (`0`
   for the first set of numbers, `1` the second set of numbers from 10-19,
   and so on until `8` the last set of numbers from 80-90).

   Each value pair represents a `LinkedList`, even though numbers are globally
   unique within the number pool, so arguably, a Set would do just as well.

   Once the Map is prefilled with unique numbers, each `LinkedList` is shuffled
   (see the `CollectionShuffler` class).
    
2. **Phase 2: Place numbers in every column, of every ticket**
   
   _Numbers in pool: 90_

   The application goes through every number group in the number pool and pops the
   first element from the linked list (i.e. the head).

   By the end of this phase, every column of every ticket will possess exactly one number.
   One should remember that the number-pool was pre-shuffled, so the head of the list is
   expected to be different for each ticket strip.

3. **Phase 3: Assign numbers to random tickets from the number pool in descending column order**

   _Numbers in pool: 36_

   At this stage, the pool possesses 36 numbers:
     - 3 numbers in the first column
     - 5 numbers in the last column
     - 4 numbers in the 7 middle columns

   The application goes through the columns in the number pool in descending order (column 8, column
   7 ... column 0), and then recurses back to column 8. In each iteration, a number is popped from the
   list, and a "valid" ticket is chosen that can support the popped number. A "valid" ticket is one which:

     - Is not complete (i.e. has less than 15 numbers)
     - Has lesss than 3 numbers in its column.

   The application assumes that there will always be at least one ticket that can house the popped number
   (this is given from the requirements).

   The end result of this phase is that all numbers are spread evenly amongst all the tickets, and the 
   number pool is fully consumed.

4. **Phase 4: Adding empty spaces to a ticket**

   Once all tickets are assigned 15 numbers, the application calculates where it should add blank spaces to
   the ticket. Unlike previous phases, this can be done in isolation of the other tickets, making this a bit
   simpler.

   Each ticket is assigned a "starting point", indicating the row index of where it can place the first number
   of the first column. The first number of the subsequent column is placed one row after the last number of
   the current column. For example: A column houses 2 numbers, placing the first number in row 0, and the second
   in row 1, The subsequent column will:
     1. Place its only number in row 3, if it only houses 1 number
     2. Place its numbers in row 0 and row 2, if it houses 2 numbers.
     3. Place its numbers in all rows if it houses 3 numbers.

   The application then sorts the column's numbers in ascending order before moving on to the next column.

# JMH Microbenchmarking Tests

The application houses two JMH benchmarks in the test class `JMHBenchmarkTests`, namely:
1. The time taken for one thread to generate 100 strips; and
2. The time taken for four threads to generate 10000 strips

Either test emits the average time taken to perform each time. Since this can be somewhat annoying when wanting
to execute the app, you may simply skip these tests using `./mvnw package -DskipTests` to generate the JAR and skipping
over JMH. Note that this will also skip over other JUnit tests in the project.

These are the test results obtained on a Lenovo Legion 15 Pro equipped with an AMD Ryzen 9 7945X (16 physical cores,
32 logical cores), and 32GB of RAM.

## Four Threads - 10000 Strips

Approximately 70ms on a warm JVM:

```bash
# JMH version: 1.37
# VM version: JDK 17.0.11, OpenJDK 64-Bit Server VM, 17.0.11+9
# VM invoker: C:\Program Files\Eclipse Adoptium\jdk-17.0.11.9-hotspot\bin\java.exe
# VM options: -Xms2048m -Xmx2048m
# Blackhole mode: compiler (auto-detected, use -Djmh.blackhole.autoDetect=false to disable)
# Warmup: 5 iterations, 10 s each
# Measurement: 5 iterations, 10 s each
# Timeout: 10 min per iteration
# Threads: 4 threads, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: com.lindar.challenges.jsaliba.unit.perf.JMHBenchmarkTests.fourThreadsGenerating10000Strips

# Run progress: 0.00% complete, ETA 00:03:20
# Fork: 1 of 1
# Warmup Iteration   1: 66.498 ±(99.9%) 6.247 ms/op
# Warmup Iteration   2: 67.834 ±(99.9%) 1.302 ms/op
# Warmup Iteration   3: 74.180 ±(99.9%) 3.540 ms/op
# Warmup Iteration   4: 73.812 ±(99.9%) 2.822 ms/op
# Warmup Iteration   5: 73.811 ±(99.9%) 0.733 ms/op
Iteration   1: 73.647 ±(99.9%) 2.156 ms/op
Iteration   2: 74.429 ±(99.9%) 1.346 ms/op
Iteration   3: 74.240 ±(99.9%) 2.211 ms/op
Iteration   4: 74.480 ±(99.9%) 3.033 ms/op
Iteration   5: 74.165 ±(99.9%) 2.844 ms/op


Result "com.lindar.challenges.jsaliba.unit.perf.JMHBenchmarkTests.fourThreadsGenerating10000Strips":
  74.192 ±(99.9%) 1.277 ms/op [Average]
  (min, avg, max) = (73.647, 74.192, 74.480), stdev = 0.332
  CI (99.9%): [72.916, 75.469] (assumes normal distribution)
```

## One thread - 100 Strips

Approximately 0.5ms on a warm JVM:

```bash
# JMH version: 1.37
# VM version: JDK 17.0.11, OpenJDK 64-Bit Server VM, 17.0.11+9
# VM invoker: C:\Program Files\Eclipse Adoptium\jdk-17.0.11.9-hotspot\bin\java.exe
# VM options: -Xms2048m -Xmx2048m
# Blackhole mode: compiler (auto-detected, use -Djmh.blackhole.autoDetect=false to disable)
# Warmup: 5 iterations, 10 s each
# Measurement: 5 iterations, 10 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: com.lindar.challenges.jsaliba.unit.perf.JMHBenchmarkTests.oneThreadGenerating100Strips

# Run progress: 50.00% complete, ETA 00:01:41
# Fork: 1 of 1
# Warmup Iteration   1: 0.596 ms/op
# Warmup Iteration   2: 0.589 ms/op
# Warmup Iteration   3: 0.583 ms/op
# Warmup Iteration   4: 0.582 ms/op
# Warmup Iteration   5: 0.585 ms/op
Iteration   1: 0.584 ms/op
Iteration   2: 0.585 ms/op
Iteration   3: 0.588 ms/op
Iteration   4: 0.578 ms/op
Iteration   5: 0.557 ms/op


Result "com.lindar.challenges.jsaliba.unit.perf.JMHBenchmarkTests.oneThreadGenerating100Strips":
  0.579 ±(99.9%) 0.049 ms/op [Average]
  (min, avg, max) = (0.557, 0.579, 0.588), stdev = 0.013
  CI (99.9%): [0.530, 0.627] (assumes normal distribution)
```

# Continuous Integration (or lack thereof...)

GitHub Actions workflows has not been implemented because of JMH - shared runners are likely to
reject intensive workloads and hence would require dedicated agents to run this application's tests.