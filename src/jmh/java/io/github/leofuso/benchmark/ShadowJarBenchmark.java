package io.github.leofuso.benchmark;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.leofuso.benchmark.support.CountingReporter;

import eu.infomas.annotation.AnnotationDetector;


@State(Scope.Thread)
@SuppressWarnings("unused")
public class ShadowJarBenchmark {

    private static final Logger logger = LoggerFactory.getLogger(ShadowJarBenchmark.class);

    private AnnotationDetector detector;
    private CountingReporter reporter;
    private File jar;

    @Setup
    public void jarSetup() throws IOException {
        try (InputStream input = new FileInputStream("jmh.properties")) {

            Properties prop = new Properties();
            prop.load(input);

            final String path = prop.getProperty("jar.path");
            jar = new File(path);
        }
    }

    @Setup(Level.Iteration)
    public void detectorSetup() {
        reporter = new CountingReporter(Deprecated.class);
        detector = new AnnotationDetector(reporter);
    }


    @Benchmark
    @Threads(4)
    @Warmup(iterations = 5, time = 1)
    @Measurement(iterations = 10, time = 1)
    @BenchmarkMode({ Mode.SingleShotTime, Mode.AverageTime })
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void run() throws IOException {
        detector.detect(jar);
    }

    @TearDown(Level.Invocation)
    public void tearDown() {
        logger.info(
                " { Types: {}, Fields : {}, Methods: {} } ",
                reporter.getTypeCount(),
                reporter.getFieldCount(),
                reporter.getMethodCount()
        );
        reporter.reset();
    }
}
