package osp.leobert.android.inspector;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import osp.leobert.android.inspector.notations.ValidationQualifier;

public final class Util {

    public static final Set<Annotation> NO_ANNOTATIONS = Collections.emptySet();

    private Util() {
    }

    public static boolean typesMatch(Type pattern, Type candidate) {
        // TODO: permit raw types (like Set.class) to match non-raw candidates (like Set<Long>).
        return pattern.equals(candidate);
    }

    public static Set<? extends Annotation> validationAnnotations(AnnotatedElement annotatedElement) {
        return validationAnnotations(annotatedElement.getAnnotations());
    }

    public static Set<? extends Annotation> validationAnnotations(Annotation[] annotations) {
        Set<Annotation> result = null;
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().isAnnotationPresent(ValidationQualifier.class)) {
                if (result == null) result = new LinkedHashSet<>();
                result.add(annotation);
            }
        }
        return result != null ? Collections.unmodifiableSet(result) : Util.NO_ANNOTATIONS;
    }

    public static boolean isAnnotationPresent(
            Set<? extends Annotation> annotations, Class<? extends Annotation> annotationClass) {
        if (annotations.isEmpty()) return false; // Save an iterator in the common case.
        for (Annotation annotation : annotations) {
            if (annotation.annotationType() == annotationClass) return true;
        }
        return false;
    }

    /**
     * Returns true if {@code annotations} has any annotation whose simple name is Nullable.
     */
    public static boolean hasNullable(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().getSimpleName().equals("Nullable")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if {@code annotations} has any annotation whose simple name is Nullable.
     */
    public static boolean hasNullable(Set<? extends Annotation> annotations) {
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().getSimpleName().equals("Nullable")) {
                return true;
            }
        }
        return false;
    }

    /**
     * delimiter 分隔符
     * elements 需要连接的字符数组
     */
    public static String join(CharSequence delimiter, Collection<? extends CharSequence> elements) {
        // 空指针判断
        Objects.requireNonNull(delimiter);
        Objects.requireNonNull(elements);

        StringBuilder stringBuilder = new StringBuilder();

        for (CharSequence cs : elements) {
            // 拼接字符
            stringBuilder
                    .append(delimiter)
                    .append(cs);
        }
        int index = delimiter != null ? delimiter.length() : 0;
        if (stringBuilder.length() > 0)
            return stringBuilder.substring(index);
        return "";
    }
}
