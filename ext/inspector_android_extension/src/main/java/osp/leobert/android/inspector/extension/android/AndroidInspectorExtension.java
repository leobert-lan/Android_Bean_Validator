package osp.leobert.android.inspector.extension.android;

import androidx.annotation.FloatRange;
import androidx.annotation.IntDef;
import androidx.annotation.IntRange;
import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.annotation.Size;
import androidx.annotation.StringDef;

import com.google.auto.service.AutoService;
import com.google.common.collect.Sets;
import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;
import javax.lang.model.element.AnnotationMirror;

import osp.leobert.android.inspector.Util;
import osp.leobert.android.inspector.ValidationException;
import osp.leobert.android.inspector.spi.InspectorExtension;
import osp.leobert.android.inspector.spi.Property;


@AutoService(InspectorExtension.class)
public final class AndroidInspectorExtension implements InspectorExtension {

    private static final Set<Class<? extends Annotation>> SUPPORTED_ANNOTATIONS =
            Sets.newLinkedHashSet(Arrays.asList(FloatRange.class, IntRange.class, Size.class, NonNull.class));

    private static final Set<Class<? extends Annotation>> SUPPORTED_ANNOTATIONS_OF_ANNOTATIONS =
            Sets.newLinkedHashSet(Arrays.asList(IntDef.class, LongDef.class, StringDef.class));

    @Override
    public boolean applicable(Property property) {
        for (Class<? extends Annotation> a : SUPPORTED_ANNOTATIONS) {
            if (property.element.getAnnotation(a) != null) {
                return true;
            }
        }
        for (Class<? extends Annotation> a : SUPPORTED_ANNOTATIONS_OF_ANNOTATIONS) {
            if (findAnnotationByAnnotation(property.element.getAnnotationMirrors(), a) != null) {
                return true;
            }
        }
        return !property.type.isPrimitive()
                && !property.type.equals(TypeName.VOID.box())
                && !property.annotations.contains("Nullable");
    }

    @Override
    public Set<String> applicableAnnotations() {
        return Collections.singleton("com.google.auto.value.AutoValue");
    }

    @Override
    public CodeBlock generateValidation(Property prop, String variableName, ParameterSpec value) {
        return addAndroidChecks(prop, variableName);
    }

    @Override
    public Priority priority() {
        return Priority.NONE;
    }

    @Override
    public String toString() {
        return AndroidInspectorExtension.class.getSimpleName();
    }

    private static CodeBlock addAndroidChecks(Property prop, String variableName) {
        CodeBlock.Builder validationBlock = CodeBlock.builder();
        IntRange intRange = prop.annotation(IntRange.class);
        if (intRange != null) {
            long from = intRange.from();
            long to = intRange.to();
            if (from != Long.MIN_VALUE) {
                validationBlock.beginControlFlow("if ($L < $L)", variableName, from)
                        .addStatement("throw new $T(\"$L must be greater than $L but is \" + $L)",
                                ValidationException.class,
                                prop.methodName,
                                from,
                                variableName)
                        .endControlFlow();
            }
            if (to != Long.MAX_VALUE) {
                validationBlock.beginControlFlow("else if ($L > $L)", variableName, to)
                        .addStatement("throw new $T(\"$L must be less than $L but is \" + $L)",
                                ValidationException.class,
                                prop.methodName,
                                to,
                                variableName)
                        .endControlFlow();
            }
        }
        FloatRange floatRange = prop.annotation(FloatRange.class);
        if (floatRange != null) {
            double from = floatRange.from();
            double to = floatRange.to();
            if (from != Double.NEGATIVE_INFINITY) {
                validationBlock.beginControlFlow("if ($L < $L)", variableName, from)
                        .addStatement("throw new $T(\"$L must be greater than $L but is \" + $L)",
                                ValidationException.class,
                                prop.methodName,
                                from,
                                variableName)
                        .endControlFlow();
            }
            if (to != Double.POSITIVE_INFINITY) {
                validationBlock.beginControlFlow("else if ($L > $L)", variableName, to)
                        .addStatement("throw new $T(\"$L must be less than $L but is \" + $L)",
                                ValidationException.class,
                                prop.methodName,
                                to,
                                variableName)
                        .endControlFlow();
            }
        }
        Size size = prop.annotation(Size.class);
        if (size != null) {
            String sizeVar = variableName + "Size";
            if (prop.type instanceof ArrayTypeName) {
                validationBlock.addStatement("int $L = $L.length", sizeVar, variableName);
            } else if (prop.type instanceof ParameterizedTypeName) {
                // Assume it's a collection or map
                validationBlock.addStatement("int $L = $L.size()", sizeVar, variableName);
            }
            long exact = size.value();
            long min = size.min();
            long max = size.max();
            long multiple = size.multiple();
            if (exact != -1) {
                validationBlock.beginControlFlow("if ($L != $L)", sizeVar, exact)
                        .addStatement("throw new $T(\"$L's size must be exactly $L but is \" + $L)",
                                ValidationException.class,
                                prop.methodName,
                                exact,
                                sizeVar)
                        .endControlFlow();
            }
            if (min != Long.MIN_VALUE) {
                validationBlock.beginControlFlow("if ($L < $L)", sizeVar, min)
                        .addStatement("throw new $T(\"$L's size must be greater than $L but is \" + $L)",
                                ValidationException.class,
                                prop.methodName,
                                min,
                                sizeVar)
                        .endControlFlow();
            }
            if (max != Long.MAX_VALUE) {
                validationBlock.beginControlFlow("if ($L > $L)", sizeVar, max)
                        .addStatement("throw new $T(\"$L's size must be less than $L but is \" + $L)",
                                ValidationException.class,
                                prop.methodName,
                                max,
                                sizeVar)
                        .endControlFlow();
            }
            if (multiple != 1) {
                validationBlock.beginControlFlow("if ($L % $L != 0)", sizeVar, multiple)
                        .addStatement("throw new $T(\"$L's size must be a multiple of $L but is \" + $L)",
                                ValidationException.class,
                                prop.methodName,
                                multiple,
                                sizeVar)
                        .endControlFlow();
            }
        }

        IntDef intDef = findAnnotationByAnnotation(prop.element.getAnnotationMirrors(), IntDef.class);
        if (intDef != null) {
            int[] values = intDef.value();

            //
//            Ints.asList(values)
//                    .stream()
//                    .map(l -> variableName + " != " + l)
//                    .collect(Collectors.toList()))
            List<String> tmp = new ArrayList<>();
            for (int i : values) {
                tmp.add(variableName + " != " + i);
            }

            validationBlock.beginControlFlow("if (!($L))",
                    Util.join(" && ", tmp))
                    .addStatement("throw new $T(\"$L's value must be within scope of its IntDef. Is \" + $L)",
                            ValidationException.class,
                            prop.methodName,
                            variableName)
                    .endControlFlow();
        }

        LongDef longDef = findAnnotationByAnnotation(prop.element.getAnnotationMirrors(), LongDef.class);
        if (longDef != null) {
            long[] values = longDef.value();

            //Longs.asList(values)
            //                                    .stream()
            //                                    .map(l -> variableName + " != " + l + "L")
            //                                    .collect(Collectors.toList()))
            List<String> tmp2 = new ArrayList<>();
            for (long l : values) {
                tmp2.add(variableName + " != " + l + "L");
            }

            validationBlock.beginControlFlow("if (!($L))",
                    Util.join(" && ", tmp2))
                    .addStatement("throw new $T(\"$L's value must be within scope of its LongDef. Is \" + $L)",
                            ValidationException.class,
                            prop.methodName,
                            variableName)
                    .endControlFlow();
        }
        StringDef stringDef =
                findAnnotationByAnnotation(prop.element.getAnnotationMirrors(), StringDef.class);
        if (stringDef != null) {
            String[] values = stringDef.value();
            //
//            Arrays.stream(values)
//                    .map(s -> "\"" + s + "\".equals(" + variableName + ")")
//                    .collect(Collectors.toList())
            List<String> tmp3 = new ArrayList<>();
            for (String s : values) {
                tmp3.add("\"" + s + "\".equals(" + variableName + ")");
            }
            validationBlock.beginControlFlow("if (!($L))",
                    Util.join(" && ", tmp3))
                    .addStatement(
                            "throw new $T(\"$L's value must be within scope of its StringDef. Is \" + $L)",
                            ValidationException.class,
                            prop.methodName,
                            variableName)
                    .endControlFlow();
        }

        NonNull nonNull = prop.annotation(NonNull.class);
        if (nonNull != null) {
            validationBlock
                    .beginControlFlow("if ($L == null)", variableName)
                    .addStatement("throw new $T($S)",
                            ValidationException.class,
                            prop.methodName + "() is not nullable but returns a null")
                    .endControlFlow();
        }

        return validationBlock.build();
    }

    @Nullable
    private static <T extends Annotation> T findAnnotationByAnnotation(Collection<? extends
            AnnotationMirror> annotations, Class<T> clazz) {
        if (annotations.isEmpty()) return null; // Save an iterator in the common case.
        for (AnnotationMirror mirror : annotations) {
            Annotation target = mirror.getAnnotationType()
                    .asElement()
                    .getAnnotation(clazz);
            if (target != null) {
                //noinspection unchecked
                return (T) target;
            }
        }
        return null;
    }

}