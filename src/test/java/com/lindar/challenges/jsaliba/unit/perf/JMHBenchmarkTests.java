package com.lindar.challenges.jsaliba.unit.perf;

import com.lindar.challenges.jsaliba.beans.TicketStrip;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

public class JMHBenchmarkTests {

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @GroupThreads(1)
    public void oneThreadGenerating100Strips() {
        for (int i = 0; i < 100; i++) { new TicketStrip(); }
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @GroupThreads(4)
    public void fourThreadsGenerating10000Strips() {
        for (int i = 0; i < 10000; i++) { new TicketStrip(); }
    }

    @Test
    public void executeJMHRunner() throws RunnerException {
        final Options opt = new OptionsBuilder()
          .include(JMHBenchmarkTests.class.getSimpleName())
          // can configure garbage collector to have larger NewGen given that
          // TicketStrips are short-lived, and it wouldn't make sense to have
          // these go into the OldGen portion of memory.
          .jvmArgs("-Xms2048m", "-Xmx2048m")
          .forks(1)
          .build();

        new Runner(opt).run();
    }

}
