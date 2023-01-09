package com.zipper.think.gradle.apt

import com.squareup.javapoet.TypeSpec
import com.zipper.think.apt.anno.InflateUnReflectView
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

class InflateViewProcessor : AbstractProcessor() {

    private var messager: Messager? = null

    override fun init(processingEnv: ProcessingEnvironment?) {
        super.init(processingEnv)
        log("============================== InflateViewProcessor init")
        println("==============================1 InflateViewProcessor init")
        processingEnv?.let {
            messager = it.messager
        }
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(
            InflateUnReflectView::class.java.canonicalName
        )
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }

    override fun process(
        annotations: MutableSet<out TypeElement>?,
        roundEnv: RoundEnvironment
    ): Boolean {
        log("annotations = $annotations")
        log("roundEnv = $annotations")
        val elements = roundEnv.getElementsAnnotatedWith(InflateUnReflectView::class.java)
        log("elements = ${elements.size}")


        return true
    }

    private fun log(message: String?){
        messager?.printMessage(Diagnostic.Kind.NOTE, "$message")
    }
}