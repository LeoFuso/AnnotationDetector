import me.champeau.jmh.JMHTask
import java.nio.file.Paths

plugins {
    idea
    java
    application
    id("me.champeau.jmh") version "0.7.1"
    id("com.github.johnrengelman.shadow") version "8.1.1"


    id("org.springframework.boot") version "3.1.1"
    id("io.spring.dependency-management") version "1.1.0"
}

group = "io.github.leofuso.benchmark"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {

    jmh("eu.infomas:annotation-detector:3.0.5")

    /* Possible dependencies for a... regular project? Builds up to 77MB. */
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("io.micrometer:micrometer-tracing-bridge-brave")
    implementation("org.springframework.kafka:spring-kafka")
    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-hateoas")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.flywaydb:flyway-core")
    implementation("org.projectlombok:lombok")
    implementation("io.micrometer:micrometer-registry-datadog")
    implementation("org.postgresql:postgresql")
    implementation("org.springframework.boot:spring-boot-configuration-processor")
    implementation("org.projectlombok:lombok")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}


java {
    withJavadocJar()
    withSourcesJar()
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(JavaVersion.VERSION_19.majorVersion))
    }
}

tasks {

    shadowJar {
        archiveBaseName.set("shadow")
        archiveClassifier.set("")
        archiveVersion.set("")

        val buildDir = File(project.buildDir.absolutePath)
        val properties = File(projectDir.absolutePath, "jmh.properties")

        doLast {
            properties.createNewFile()
            val jar = File(buildDir, "/libs/shadow.jar".asPlatformAgnosticPath())
            properties append "jar.path=${jar.path}"
        }
    }

    named<JMHTask>("jmh") {
        outputs.upToDateWhen { false }
        dependsOn(shadowJar)
        val properties = File(projectDir.absolutePath, "jmh.properties")
        doLast {
            if (properties.exists()) {
                properties.delete()
            }
        }
    }

    test {
        useJUnitPlatform()
    }
}

application {
    mainClass.set("io.github.leofuso.benchmark.Main")
}

jmh {
    iterations.set(10)
    benchmarkMode.set(
        listOf(
            "avgt",
            "ss",
            "sample"
        )
    )
    batchSize.set(1)
    fork.set(1)
    failOnError.set(true)
    forceGC.set(false)
    profilers.set(
        listOf(
            "perfnorm"
        )
    )
    humanOutputFile.set(project.file("${project.buildDir}/reports/jmh/human.txt"))
    resultsFile.set(project.file("${project.buildDir}/reports/jmh/results.txt"))
    timeOnIteration.set("1s")
    threads.set(1)
    timeUnit.set("ms")
    verbosity.set("EXTRA")
    warmup.set("1s")
    warmupForks.set(2)
    warmupIterations.set(1)
    warmupMode.set("INDI")
    resultFormat.set("TEXT")
}

fun String.asPlatformAgnosticPath() = this.replace("/", File.separator)
fun platformAgnosticPath(path: String) = Paths.get(path.asPlatformAgnosticPath())

infix fun File.append(content: String) = this.writeText(content)
    .let { this }

/*
jmh {
    includes = ['some regular expression'] -> include pattern (regular expression) for benchmarks to be executed
    excludes = ['some regular expression'] -> exclude pattern (regular expression) for benchmarks to be executed
    iterations = 10 -> Number of measurement iterations to do.
    benchmarkMode = ['thrpt','ss'] -> Benchmark mode. Available modes are: [Throughput/thrpt, AverageTime/avgt, SampleTime/sample, SingleShotTime/ss, All/all]
    batchSize = 1 -> Batch size: number of benchmark method calls per operation. (some benchmark modes can ignore this setting)
    fork = 2 -> How many times to fork a single benchmark. Use 0 to disable forking altogether
    failOnError = false -> Should JMH fail immediately if any benchmark had experienced the unrecoverable error?
    forceGC = false -> Should JMH force GC between iterations?
    jvm = 'some-jvm' -> Custom JVM to use when forking.
    jvmArgs = ['Custom JVM args to use when forking.']
    jvmArgsAppend = ['Custom JVM args to use when forking (append these)']
    jvmArgsPrepend =[ 'Custom JVM args to use when forking (prepend these)']
    humanOutputFile = project.file("${project.buildDir}/reports/jmh/human.txt") -> human-readable output file
    resultsFile = project.file("${project.buildDir}/reports/jmh/results.txt") -> results file
    operationsPerInvocation = 10 -> Operations per invocation.
    benchmarkParameters =  [:] -> Benchmark parameters.
    profilers = [] -> Use profilers to collect additional data. Supported profilers: [cl, comp, gc, stack, perf, perfnorm, perfasm, xperf, xperfasm, hs_cl, hs_comp, hs_gc, hs_rt, hs_thr, async]
    timeOnIteration = '1s' -> Time to spend at each measurement iteration.
    resultFormat = 'CSV' -> Result format type (one of CSV, JSON, NONE, SCSV, TEXT)
    synchronizeIterations = false -> Synchronize iterations?
    threads = 4 -> Number of worker threads to run with.
    threadGroups = [2,3,4] ->Override thread group distribution for asymmetric benchmarks.
    timeout = '1s' -> Timeout for benchmark iteration.
    timeUnit = 'ms' -> Output time unit. Available time units are: [m, s, ms, us, ns].
    verbosity = 'NORMAL' -> Verbosity mode. Available modes are: [SILENT, NORMAL, EXTRA]
    warmup = '1s' -> Time to spend at each warmup iteration.
    warmupBatchSize = 10 -> Warmup batch size: number of benchmark method calls per operation.
    warmupForks = 0 -> How many warmup forks to make for a single benchmark. 0 to disable warmup forks.
    warmupIterations = 1 -> Number of warmup iterations to do.
    warmupMode = 'INDI' -> Warmup mode for warming up selected benchmarks. Warmup modes are: [INDI, BULK, BULK_INDI].
    warmupBenchmarks = ['.*Warmup'] -> Warmup benchmarks to include in the run in addition to already select. JMH will not measure these benchmarks, but only use them for the warmup.

    zip64 = true -> Use ZIP64 format for bigger archives
    jmhVersion = '1.36' -> Specifies JMH version
    includeTests = true -> Allows to include test sources into generate JMH jar, i.e., use it when benchmarks depend on the test classes.
    duplicateClassesStrategy = DuplicatesStrategy.FAIL -> Strategy to apply when encountering duplicate classes during creation of the fat jar (i.e. while executing jmhJar task)
}*/
