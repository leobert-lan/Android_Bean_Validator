package osp.leobert.android.inspector.spi;

import com.google.common.collect.ImmutableSet;
import com.squareup.javapoet.TypeName;
import com.sun.istack.internal.Nullable;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;

import osp.leobert.android.inspector.notations.InspectorIgnored;
import osp.leobert.android.inspector.notations.ValidatedBy;

/**
 * <p><b>Package:</b> osp.leobert.android.inspector.spi </p>
 * <p><b>Project:</b> Jsr380 </p>
 * <p><b>Classname:</b> Property </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2018/11/9.
 */
public class Property {
    public final String methodName;
    public final String humanName;
    public final ExecutableElement element;
    public final TypeName type;
    public final ImmutableSet<String> annotations;

    public Property(String humanName, ExecutableElement element) {
        this.methodName = element.getSimpleName()
                .toString();
        this.humanName = humanName;
        this.element = element;

        type = TypeName.get(element.getReturnType());
        annotations = buildAnnotations(element);
    }

    @Nullable
    static TypeMirror getAnnotationValue(Element foo, Class<?> annotation) {
        AnnotationMirror am = getAnnotationMirror(foo, annotation);
        if (am == null) {
            return null;
        }
        AnnotationValue av = getAnnotationValue(am, "value");
        return av == null ? null : (TypeMirror) av.getValue();
    }

    @Nullable
    private static AnnotationMirror getAnnotationMirror(Element typeElement, Class<?> clazz) {
        String clazzName = clazz.getName();
        for (AnnotationMirror m : typeElement.getAnnotationMirrors()) {
            if (m.getAnnotationType()
                    .toString()
                    .equals(clazzName)) {
                return m;
            }
        }
        return null;
    }

    @Nullable
    private static AnnotationValue getAnnotationValue(AnnotationMirror annotationMirror, String key) {
        Map<? extends ExecutableElement, ? extends AnnotationValue> values =
                annotationMirror.getElementValues();
        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : values
                .entrySet()) {
            if (entry.getKey()
                    .getSimpleName()
                    .toString()
                    .equals(key)) {
                return entry.getValue();
            }
        }
        return null;
    }

    @Nullable
    public <T extends Annotation> T annotation(Class<T> annotation) {
        return element.getAnnotation(annotation);
    }

    @Nullable
    public ValidatedBy validatedBy() {
        return element.getAnnotation(ValidatedBy.class);
    }

    @Nullable
    public AnnotationMirror validatedByMirror() {
        return getAnnotationMirror(element, ValidatedBy.class);
    }

    public boolean shouldValidate() {
        return element.getAnnotation(InspectorIgnored.class) == null && validatedBy() == null;
    }

    private ImmutableSet<String> buildAnnotations(ExecutableElement element) {
        ImmutableSet.Builder<String> builder = ImmutableSet.builder();

        List<? extends AnnotationMirror> annotations = element.getAnnotationMirrors();
        for (AnnotationMirror annotation : annotations) {
            builder.add(annotation.getAnnotationType()
                    .asElement()
                    .getSimpleName()
                    .toString());
        }

        return builder.build();
    }


    public String getMethodName() {
        return methodName;
    }

    public String getHumanName() {
        return humanName;
    }

    public ExecutableElement getElement() {
        return element;
    }

    public TypeName getType() {
        return type;
    }

    public ImmutableSet<String> getAnnotations() {
        return annotations;
    }

    @javax.annotation.Nullable
    public <T extends Annotation> T findAnnotationByAnnotation(Class<T> clazz) {
        Collection<? extends AnnotationMirror> annotations = element.getAnnotationMirrors();

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

    public boolean isElementHasAnnotation(Class<? extends Annotation> a) {
        return getElement().getAnnotation(a) != null;
    }
}
