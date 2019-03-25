package org.dzw.cc.processor;

import org.dzw.cc.annotation.Sub;
import org.dzw.cc.annotation.ThreadMode;
import org.dzw.cc.processor.meta.SubscriberMethodInfo;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description: 注解扫描器
 * @author: dzw
 * @date: 2019/03/22 09:35
 **/
//@SupportedAnnotationTypes("com.self.Store")
//@AutoService(Processor.class)
@SupportedAnnotationTypes("org.dzw.cc.annotation.Sub")
//@SupportedOptions(value = {"eventBusIndex", "verbose"})
public class AnnotationProcessor extends AbstractProcessor {


    private final ConcurrentHashMap<TypeElement, List<ExecutableElement>> methodsByClass = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, List<ExecutableElement>> keyByMethod = new ConcurrentHashMap<>();

    private final Set<TypeElement> classesToSkip = new HashSet<>();

    private final String index = "org.dzw.cc.processor.SubMethodInfoIndexs1";

    //org.greenrobot.eventbus.EventBusTestsIndex
//    private final String index = "Indexs";

    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        Boolean flag = false;
        Messager messager = processingEnv.getMessager();
        System.out.println("==============================");
        if (annotations.isEmpty()) {
            return false;
        }
        try {
            collectSubscribers(annotations, env, messager);
            if (!methodsByClass.isEmpty()) {
                createInfoIndexFile();
                flag = true;
            } else {
                messager.printMessage(Diagnostic.Kind.WARNING, "No @Sub annotations found");
            }
        } catch (RuntimeException e) {
            messager.printMessage(Diagnostic.Kind.ERROR, "Sub collect error");
        }
        return flag;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }


    private void collectSubscribers(Set<? extends TypeElement> annotations, RoundEnvironment env, Messager messager) {
        Set<? extends Element> elements = env.getElementsAnnotatedWith(Sub.class);
        for (Element element : elements) {
            if (element instanceof ExecutableElement) {
                ExecutableElement method = (ExecutableElement) element;
                if (checkHasNoErrors(method, messager)) {
                    TypeElement classElement = (TypeElement) method.getEnclosingElement();
                    Sub annotation = element.getAnnotation(Sub.class);
                    String key = annotation.key();
                    System.out.println("=========================" + key);
                    List<ExecutableElement> list = new ArrayList<ExecutableElement>();
                    if (keyByMethod.get(key) != null && keyByMethod.get(key).size() > 0) {
                        list = keyByMethod.get(key);
                    }
                    list.add(method);
                    keyByMethod.put(key, list);

                    if (methodsByClass.get(classElement) != null && methodsByClass.get(classElement).size() > 0) {
                        list = methodsByClass.get(classElement);
                    } else {
                        list = new ArrayList<ExecutableElement>();
                    }
                    list.add(method);
                    methodsByClass.put(classElement, list);
                }
            } else {
                messager.printMessage(Diagnostic.Kind.ERROR, "@Subscribe is only valid for methods", element);
            }
        }
    }

    /**
     * @param executableElement
     * @param messager
     * @return boolean
     * @description 判断是否有定义错误
     * @author dzw
     * @date 2019/3/23 17:44
     **/
    private boolean checkHasNoErrors(ExecutableElement executableElement, Messager messager) {
        if (executableElement.getKind() != ElementKind.METHOD) {
            messager.printMessage(Diagnostic.Kind.ERROR, "Only method can be annotated", executableElement);
            return false;
        }
        if (executableElement.getModifiers().contains(Modifier.STATIC)) {
            messager.printMessage(Diagnostic.Kind.ERROR, "Sub method must not be static", executableElement);
            return false;
        }

        if (!executableElement.getModifiers().contains(Modifier.PUBLIC)) {
            messager.printMessage(Diagnostic.Kind.ERROR, "Sub method must be public", executableElement);
            return false;
        }

        List<? extends VariableElement> parameters = ((ExecutableElement) executableElement).getParameters();
        if (parameters.size() != 1) {
            messager.printMessage(Diagnostic.Kind.ERROR, "Sub method must have exactly 1 parameter", executableElement);
            return false;
        }
        Sub annotation = executableElement.getAnnotation(Sub.class);
        String key = annotation.key();
        if (key == null || "".equals(key)) {
            messager.printMessage(Diagnostic.Kind.ERROR, "key() is emplty", executableElement);
        }
        return true;
    }


    private void createInfoIndexFile() {
        BufferedWriter writer = null;
        try {
            JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(index);
            int period = index.lastIndexOf('.');
            String myPackage = period > 0 ? index.substring(0, period) : null;
            String clazz = index.substring(period + 1);
            writer = new BufferedWriter(sourceFile.openWriter());
            if (myPackage != null) {
                writer.write("package " + myPackage + ";\n\n");
            }
            writer.write("import java.util.Arrays;\n");
            writer.write("import java.util.HashMap;\n");
            writer.write("import java.util.Map;\n");
            writer.write("import java.util.List;\n\n");
            writer.write("import org.dzw.cc.processor.meta.Indexs;\n");
            writer.write("import org.dzw.cc.processor.meta.SimpleSubscriberInfo;\n");
            writer.write("import org.dzw.cc.processor.meta.SubscriberMethodInfo;\n");
            writer.write("import org.dzw.cc.processor.meta.SubscriberInfo;\n");
            writer.write("import org.dzw.cc.processor.SubscriberMethod;\n\n");

            writer.write("public class " + clazz + " implements Indexs {\n");
            writer.write("    private static final Map<String, List<SubscriberMethodInfo>> SUBSCRIBER_INDEX_KEYS;\n\n");
            writer.write("    private static final Map<Class<?>, SubscriberInfo> SUBSCRIBER_INDEX;\n\n");
            writer.write("    static {\n");
            writer.write("        SUBSCRIBER_INDEX_KEYS = new HashMap<String, List<SubscriberMethodInfo>>();\n\n");
            writer.write("        SUBSCRIBER_INDEX = new HashMap<Class<?>, SubscriberInfo>();\n\n");
            writeIndexLines(writer, myPackage);
            writer.write("    }\n\n");
            writer.write("    private static void putIndex(SubscriberInfo info) {\n");
            writer.write("        SUBSCRIBER_INDEX.put(info.getSubscriberClass(), info);\n");
            writer.write("    }\n\n");
            writer.write("    private static void putIndex(String key,List<SubscriberMethodInfo> info) {\n");
            writer.write("        SUBSCRIBER_INDEX_KEYS.put(key, info);\n");
            writer.write("    }\n\n");
            writer.write("    @Override\n");
            writer.write("    public SubscriberInfo getSubMethodInfo(Class<?> subscriberClass) {\n");
            writer.write("        SubscriberInfo info = SUBSCRIBER_INDEX.get(subscriberClass);\n");
            writer.write("        if (info != null) {\n");
            writer.write("            return info;\n");
            writer.write("        } else {\n");
            writer.write("            return null;\n");
            writer.write("        }\n");
            writer.write("    }\n");
            writer.write("    @Override\n");
            writer.write("    public List<SubscriberMethodInfo> getSubMethodInfo(String key) {\n");
            writer.write("        List<SubscriberMethodInfo> info = SUBSCRIBER_INDEX_KEYS.get(key);\n");
            writer.write("        if (info != null || info.size()>0) {\n");
            writer.write("            return info;\n");
            writer.write("        } else {\n");
            writer.write("            return null;\n");
            writer.write("        }\n");
            writer.write("    }\n");

            writer.write("}\n");
        } catch (IOException e) {
            throw new RuntimeException("Could not write source for " + index, e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    //Silent
                }
            }
        }
    }


    private void writeIndexLines(BufferedWriter writer, String myPackage) throws IOException {
        for (TypeElement subscriberTypeElement : methodsByClass.keySet()) {
            if (classesToSkip.contains(subscriberTypeElement)) {
                continue;
            }

            String subscriberClass = getClassString(subscriberTypeElement, myPackage);
            if (isVisible(myPackage, subscriberTypeElement)) {
                writeLine(writer, 2,
                        "putIndex(new SimpleSubscriberInfo(" + subscriberClass + ".class,",
                        "true,", "new SubscriberMethodInfo[] {");
                List<ExecutableElement> methods = methodsByClass.get(subscriberTypeElement);
                writeCreateSubscriberMethods(writer, methods, "new SubscriberMethodInfo", myPackage);
                writer.write("        }));\n\n");
            } else {
                writer.write("        // Subscriber not visible to index: " + subscriberClass + "\n");
            }
        }
        for (String key : keyByMethod.keySet()) {
            writeLine(writer, 2,
                    "putIndex(\"" + key + "\",SUBSCRIBER_INDEX_KEYS.get(\""+key+"\"));");
        }
    }

    private void writeLine(BufferedWriter writer, int indentLevel, String... parts) throws IOException {
        writeLine(writer, indentLevel, 2, parts);
    }

    private void writeLine(BufferedWriter writer, int indentLevel, int indentLevelIncrease, String... parts)
            throws IOException {
        writeIndent(writer, indentLevel);
        int len = indentLevel * 4;
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (i != 0) {
                if (len + part.length() > 118) {
                    writer.write("\n");
                    if (indentLevel < 12) {
                        indentLevel += indentLevelIncrease;
                    }
                    writeIndent(writer, indentLevel);
                    len = indentLevel * 4;
                } else {
                    writer.write(" ");
                }
            }
            writer.write(part);
            len += part.length();
        }
        writer.write("\n");
    }

    private void writeIndent(BufferedWriter writer, int indentLevel) throws IOException {
        for (int i = 0; i < indentLevel; i++) {
            writer.write("    ");
        }
    }


    private void writeCreateSubscriberMethods(BufferedWriter writer, List<ExecutableElement> methods,
                                              String callPrefix, String myPackage) throws IOException {
        for (ExecutableElement method : methods) {
            List<? extends VariableElement> parameters = method.getParameters();
            TypeMirror paramType = getParamTypeMirror(parameters.get(0), null);
            TypeElement paramElement = (TypeElement) processingEnv.getTypeUtils().asElement(paramType);
            String methodName = method.getSimpleName().toString();
            String eventClass = getClassString(paramElement, myPackage) + ".class";

            Sub subscribe = method.getAnnotation(Sub.class);
            List<String> parts = new ArrayList<>();
            parts.add(callPrefix + "(\"" + methodName + "\",");
            String lineEnd = "),";
            if (subscribe.priority() == 0 && !subscribe.sticky()) {
                if (subscribe.processMode() == ThreadMode.POSTING) {
                    parts.add(eventClass + lineEnd);
                } else {
                    parts.add(eventClass + ",");
                    parts.add("ProcessMode." + subscribe.processMode().name() + lineEnd);
                }
            } else {
                parts.add(eventClass + ",");
                parts.add("ThreadMode." + subscribe.processMode().name() + ",");
                parts.add(subscribe.priority() + ",");
                parts.add(subscribe.sticky() + lineEnd);
            }
            writeLine(writer, 3, parts.toArray(new String[parts.size()]));
        }
    }

    private String getClassString(TypeElement typeElement, String myPackage) {
        PackageElement packageElement = getPackageElement(typeElement);
        String packageString = packageElement.getQualifiedName().toString();
        String className = typeElement.getQualifiedName().toString();
        if (packageString != null && !packageString.isEmpty()) {
            if (packageString.equals(myPackage)) {
                className = cutPackage(myPackage, className);
            } else if (packageString.equals("java.lang")) {
                className = typeElement.getSimpleName().toString();
            }
        }
        return className;
    }

    private String cutPackage(String paket, String className) {
        if (className.startsWith(paket + '.')) {
            // Don't use TypeElement.getSimpleName, it doesn't work for us with inner classes
            return className.substring(paket.length() + 1);
        } else {
            // Paranoia
            throw new IllegalStateException("Mismatching " + paket + " vs. " + className);
        }
    }


    private void checkForSubscribersToSkip(Messager messager, String myPackage) {
        for (TypeElement skipCandidate : methodsByClass.keySet()) {
            TypeElement subscriberClass = skipCandidate;
            while (subscriberClass != null) {
                if (!isVisible(myPackage, subscriberClass)) {
                    boolean added = classesToSkip.add(skipCandidate);
                    if (added) {
                        String msg;
                        if (subscriberClass.equals(skipCandidate)) {
                            msg = "Falling back to reflection because class is not public";
                        } else {
                            msg = "Falling back to reflection because " + skipCandidate +
                                    " has a non-public super class";
                        }
                        messager.printMessage(Diagnostic.Kind.NOTE, msg, subscriberClass);
                    }
                    break;
                }
                List<ExecutableElement> methods = methodsByClass.get(subscriberClass);
                if (methods != null) {
                    for (ExecutableElement method : methods) {
                        String skipReason = null;
                        VariableElement param = method.getParameters().get(0);
                        TypeMirror typeMirror = getParamTypeMirror(param, messager);
                        if (!(typeMirror instanceof DeclaredType) ||
                                !(((DeclaredType) typeMirror).asElement() instanceof TypeElement)) {
                            skipReason = "event type cannot be processed";
                        }
                        if (skipReason == null) {
                            TypeElement eventTypeElement = (TypeElement) ((DeclaredType) typeMirror).asElement();
                            if (!isVisible(myPackage, eventTypeElement)) {
                                skipReason = "event type is not public";
                            }
                        }
                        if (skipReason != null) {
                            boolean added = classesToSkip.add(skipCandidate);
                            if (added) {
                                String msg = "Falling back to reflection because " + skipReason;
                                if (!subscriberClass.equals(skipCandidate)) {
                                    msg += " (found in super class for " + skipCandidate + ")";
                                }
                                messager.printMessage(Diagnostic.Kind.NOTE, msg, param);
                            }
                            break;
                        }
                    }
                }
                subscriberClass = getSuperclass(subscriberClass);
            }
        }
    }

    private boolean isVisible(String myPackage, TypeElement typeElement) {
        Set<Modifier> modifiers = typeElement.getModifiers();
        boolean visible;
        if (modifiers.contains(Modifier.PUBLIC)) {
            visible = true;
        } else if (modifiers.contains(Modifier.PRIVATE) || modifiers.contains(Modifier.PROTECTED)) {
            visible = false;
        } else {
            String subscriberPackage = getPackageElement(typeElement).getQualifiedName().toString();
            if (myPackage == null) {
                visible = subscriberPackage.length() == 0;
            } else {
                visible = myPackage.equals(subscriberPackage);
            }
        }
        return visible;
    }

    private PackageElement getPackageElement(TypeElement subscriberClass) {
        Element candidate = subscriberClass.getEnclosingElement();
        while (!(candidate instanceof PackageElement)) {
            candidate = candidate.getEnclosingElement();
        }
        return (PackageElement) candidate;
    }


    private TypeMirror getParamTypeMirror(VariableElement param, Messager messager) {
        TypeMirror typeMirror = param.asType();
        // Check for generic type
        if (typeMirror instanceof TypeVariable) {
            TypeMirror upperBound = ((TypeVariable) typeMirror).getUpperBound();
            if (upperBound instanceof DeclaredType) {
                if (messager != null) {
                    messager.printMessage(Diagnostic.Kind.NOTE, "Using upper bound type " + upperBound +
                            " for generic parameter", param);
                }
                typeMirror = upperBound;
            }
        }
        return typeMirror;
    }

    private TypeElement getSuperclass(TypeElement type) {
        if (type.getSuperclass().getKind() == TypeKind.DECLARED) {
            TypeElement superclass = (TypeElement) processingEnv.getTypeUtils().asElement(type.getSuperclass());
            String name = superclass.getQualifiedName().toString();
            if (name.startsWith("java.") || name.startsWith("javax.") || name.startsWith("android.")) {
                // Skip system classes, this just degrades performance
                return null;
            } else {
                return superclass;
            }
        } else {
            return null;
        }
    }
}
