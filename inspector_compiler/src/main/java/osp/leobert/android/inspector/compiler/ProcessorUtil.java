package osp.leobert.android.inspector.compiler;

import com.google.auto.common.MoreElements;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import java.util.Map;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ErrorType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.SimpleTypeVisitor6;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * <p><b>Package:</b> osp.leobert.android.inspector.compiler </p>
 * <p><b>Project:</b> Jsr380 </p>
 * <p><b>Classname:</b> ProcessorUtil </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2018/11/21.
 */
class ProcessorUtil {
    static AnnotationValue getAnnotationValue(
            AnnotationMirror annotationMirror, String elementName) {
        return getAnnotationElementAndValue(annotationMirror, elementName).getValue();
    }

    static Map.Entry<ExecutableElement, AnnotationValue> getAnnotationElementAndValue(
            AnnotationMirror annotationMirror, final String elementName) {
        checkNotNull(annotationMirror);
        checkNotNull(elementName);
        for (Map.Entry<ExecutableElement, AnnotationValue> entry :
                getAnnotationValuesWithDefaults(annotationMirror).entrySet()) {
            if (entry.getKey().getSimpleName().contentEquals(elementName)) {
                return entry;
            }
        }
        throw new IllegalArgumentException(String.format("@%s does not define an element %s()",
                MoreElements.asType(annotationMirror.getAnnotationType().asElement()).getQualifiedName(),
                elementName));
    }

    static Map<ExecutableElement, AnnotationValue> getAnnotationValuesWithDefaults(
            AnnotationMirror annotation) {
        Map<ExecutableElement, AnnotationValue> values = Maps.newLinkedHashMap();
        Map<? extends ExecutableElement, ? extends AnnotationValue> declaredValues =
                annotation.getElementValues();
        for (ExecutableElement method :
                ElementFilter.methodsIn(annotation.getAnnotationType().asElement().getEnclosedElements())) {
            // Must iterate and put in this order, to ensure consistency in generated code.
            if (declaredValues.containsKey(method)) {
                values.put(method, declaredValues.get(method));
            } else if (method.getDefaultValue() != null) {
                values.put(method, method.getDefaultValue());
            } else {
                throw new IllegalStateException(
                        "Unset annotation value without default should never happen: "
                                + MoreElements.asType(method.getEnclosingElement()).getQualifiedName()
                                + '.' + method.getSimpleName() + "()");
            }
        }
        return values;
    }

    private static final class AsElementVisitor extends SimpleTypeVisitor6<Element, Void> {
        private static final AsElementVisitor INSTANCE = new AsElementVisitor();

        @Override
        protected Element defaultAction(TypeMirror e, Void p) {
            throw new IllegalArgumentException(e + " cannot be converted to an Element");
        }

        @Override
        public Element visitDeclared(DeclaredType t, Void p) {
            return t.asElement();
        }

        @Override
        public Element visitError(ErrorType t, Void p) {
            return t.asElement();
        }

        @Override
        public Element visitTypeVariable(TypeVariable t, Void p) {
            return t.asElement();
        }
    }


      static Element asElement(TypeMirror typeMirror) {
        return typeMirror.accept(AsElementVisitor.INSTANCE, null);
    }

      static TypeElement asTypeElement(TypeMirror mirror) {
        return MoreElements.asType(asElement(mirror));
    }

//    public static ImmutableSet<TypeElement> asTypeElements(Iterable<? extends TypeMirror> mirrors) {
//        checkNotNull(mirrors);
//        ImmutableSet.Builder<TypeElement> builder = ImmutableSet.builder();
//        for (TypeMirror mirror : mirrors) {
//            builder.add(asTypeElement(mirror));
//        }
//        return builder.build();
//    }
}
