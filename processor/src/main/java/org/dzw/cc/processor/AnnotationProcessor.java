package org.dzw.cc.processor;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;

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


    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        System.out.println("3==============================");
        Messager messager = processingEnv.getMessager();
        for (TypeElement te : annotations) {
            for (Element e : env.getElementsAnnotatedWith(te)) {
                messager.printMessage(Diagnostic.Kind.NOTE, "Printing: " + e.toString());
            }
        }
        return true;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
