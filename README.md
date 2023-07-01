# Annotation Detector Benchmark

A single benchmark that, given a classpath with about 70 MB, will search for all `@Deprecated`
annotations present in that classpath.

## ShadowJar

| # | Benchmark                                          | Mode | Threads | Samples | Score       | Score Error (99.9%) | Unit  |
|---|----------------------------------------------------|------|---------|---------|-------------|---------------------|-------|
| 1 | io.github.leofuso.benchmark.ShadowJarBenchmark.run | avgt | 4       | 50      | 1111.727068 | 15.271216           | ms/op |
| 2 | io.github.leofuso.benchmark.ShadowJarBenchmark.run | ss   | 4       | 50      | 1108.973104 | 20.920028           | ms/op |
