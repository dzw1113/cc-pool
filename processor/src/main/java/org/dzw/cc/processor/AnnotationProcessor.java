package org.dzw.cc.processor;

import org.dzw.cc.annotation.Sub;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.BufferedWriter;
import java.io.IOException;
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


    private final ConcurrentHashMap<String, ExecutableElement> methodsByClass = new ConcurrentHashMap<>();

    private final String myPackage = "org.dzw.cc.worker.center";

    private final String index = "Indexs";

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
                    Sub annotation = element.getAnnotation(Sub.class);
                    String key = annotation.key();
                    methodsByClass.put(key, method);
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
            String clazz = index.substring(period + 1);
            writer = new BufferedWriter(sourceFile.openWriter());
            if (myPackage != null) {
                writer.write("package " + myPackage + ";\n\n");
            }
            writer.write("import org.greenrobot.eventbus.meta.SimpleSubscriberInfo;\n");
            writer.write("import org.greenrobot.eventbus.meta.SubscriberMethodInfo;\n");
            writer.write("import org.greenrobot.eventbus.meta.SubscriberInfo;\n");
            writer.write("import org.greenrobot.eventbus.meta.SubscriberInfoIndex;\n\n");
            writer.write("import org.greenrobot.eventbus.ThreadMode;\n\n");
            writer.write("import java.util.HashMap;\n");
            writer.write("import java.util.Map;\n\n");
            writer.write("/** This class is generated by EventBus, do not edit. */\n");
            writer.write("public class " + clazz + " implements SubscriberInfoIndex {\n");
            writer.write("    private static final Map<Class<?>, SubscriberInfo> SUBSCRIBER_INDEX;\n\n");
            writer.write("    static {\n");
            writer.write("        SUBSCRIBER_INDEX = new HashMap<Class<?>, SubscriberInfo>();\n\n");
//            writeIndexLines(writer, myPackage);
            writer.write("    }\n\n");
            writer.write("    private static void putIndex(SubscriberInfo info) {\n");
            writer.write("        SUBSCRIBER_INDEX.put(info.getSubscriberClass(), info);\n");
            writer.write("    }\n\n");
            writer.write("    @Override\n");
            writer.write("    public SubscriberInfo getSubscriberInfo(Class<?> subscriberClass) {\n");
            writer.write("        SubscriberInfo info = SUBSCRIBER_INDEX.get(subscriberClass);\n");
            writer.write("        if (info != null) {\n");
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

}
