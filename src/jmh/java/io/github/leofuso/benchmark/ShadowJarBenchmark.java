package io.github.leofuso.benchmark;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.leofuso.benchmark.support.CountingReporter;

import eu.infomas.annotation.AnnotationDetector;


@State(Scope.Thread)
@SuppressWarnings("unused")
public class ShadowJarBenchmark {

    private static final Logger logger = LoggerFactory.getLogger(ShadowJarBenchmark.class);

    private final CountingReporter reporter = new CountingReporter(
            Deprecated.class
    );

    private File jar;

    @Setup
    public void setUp() throws IOException {
        try (InputStream input = new FileInputStream("jmh.properties")) {

            Properties prop = new Properties();
            prop.load(input);

            final String path = prop.getProperty("jar.path");
            jar = new File(path);
        }
    }

    @Benchmark
    public void run() throws IOException {
        final AnnotationDetector detector = new AnnotationDetector(reporter);
        detector.detect(jar);
    }

    @TearDown(Level.Iteration)
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
