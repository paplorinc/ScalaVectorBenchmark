package scala.collection.immutable;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class VectorBenchmark {
    public static void main(String... args) throws RunnerException {
        System.out.println("With UseParallelOldGC");
        new Runner(new OptionsBuilder().include(VectorBenchmark.class.getSimpleName())
                .jvmArgsAppend("-XX:+UseParallelOldGC")
                .build()).run();

        System.out.println("With UseConcMarkSweepGC");
        new Runner(new OptionsBuilder().include(VectorBenchmark.class.getSimpleName())
                .jvmArgsAppend("-XX:+UseConcMarkSweepGC")
                .build()).run();

        System.out.println("With UseG1GC");
        new Runner(new OptionsBuilder().include(VectorBenchmark.class.getSimpleName())
                .jvmArgsAppend("-XX:+UseG1GC")
                .build()).run();
    }

    @Fork(1)
    @Warmup(iterations = 10)
    @Measurement(iterations = 10)
    @State(Scope.Benchmark)
    public static class Base {
        Vector<Integer> scalaPersistent;

        @Setup
        public void setup() {
            scalaPersistent = Vector$.MODULE$.empty();
            for (int i = 0; i < 1000; i++) {
                scalaPersistent = scalaPersistent.appendBack(i);
            }
        }
    }

    /** Consume the vector one-by-one, from the front and back */
    public static class Slice extends Base {
        @Benchmark
        public void scala_persistent(Blackhole bh) {
            Vector<Integer> values = scalaPersistent;
            while (!values.isEmpty()) {
                values = values.slice(1, values.size());
                values = values.slice(0, values.size() - 1);
                bh.consume(values);
            }
        }
    }
}