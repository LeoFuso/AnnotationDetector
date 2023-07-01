package io.github.leofuso.benchmark.support;

import java.lang.annotation.Annotation;

import static eu.infomas.annotation.AnnotationDetector.FieldReporter;
import static eu.infomas.annotation.AnnotationDetector.MethodReporter;
import static eu.infomas.annotation.AnnotationDetector.TypeReporter;

public final class CountingReporter implements TypeReporter, MethodReporter, FieldReporter {

    private final Class<? extends Annotation>[] annotations;

    private int typeCount;
    private int fieldCount;
    private int methodCount;

    @SafeVarargs
    public CountingReporter(Class<? extends Annotation>... annotations) {
        this.annotations = annotations;
    }

    public Class<? extends Annotation>[] annotations() {
        return annotations;
    }

    public void reportTypeAnnotation(Class<? extends Annotation> annotation, String className) {
        ++typeCount;
    }

    @Override
    public void reportFieldAnnotation(Class<? extends Annotation> annotation, String className, String fieldName) {
        ++fieldCount;
    }

    public void reportMethodAnnotation(Class<? extends Annotation> annotation, String className, String methodName) {
        ++methodCount;
    }

    public int getTypeCount() {
        return typeCount;
    }

    public int getFieldCount() {
        return fieldCount;
    }

    public int getMethodCount() {
        return methodCount;
    }

    public void reset() {
        typeCount = 0;
        fieldCount = 0;
        methodCount = 0;
    }

}