package com.think.gradle_plugin


import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import com.inject.ext.ClassExtension
import com.inject.ext.FixExtension
import javassist.ClassPool
import org.gradle.api.Project

class ClassTransform extends Transform {

    private Project project;
    ClassPool classPool;
    ClassExtension classExtension;

    ClassTransform(Project project) {
        this.project = project;
        classExtension = project.getExtensions().getByName(Constants.CLS_EXT)
        classPool = ClassPool.getDefault()
    }

    /**
     * 设置自定义 Transform 对应的 Task 名称
     * 类似：TransformClassesWithPreDexForXXX，对应的 task 名称为：transformClassesWithMyTransformForDebug
     * 会生成目录 build/intermediates/transforms/MyTransform/
     */
    @Override
    public String getName() {
        return "ClassTransform";
    }

    /**
     * 指定输入的类型，可指定我们要处理的文件类型（保证其他类型文件不会传入）
     * CLASSES - 表示处理java的class文件
     * RESOURCES - 表示处理java的资源
     */
    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;
    }

    /**
     * 指定 Transform 的作用范围
     */
    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT;
    }

    /**
     * 是否支持增量编译
     */
    @Override
    public boolean isIncremental() {
        return false;
    }

    @Override
    public void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        // Transform 的 inputs 分为两种类型，一直是目录，一种是 jar 包。需要分开遍历
//        transformInvocation.inputs.parallelStream().each {TransformInput input ->
//            input.directoryInputs.each { DirectoryInput directoryInput ->
//                File dest = new File(project.projectDir,fixExtension.classRootDir)
//                FileUtils.copyDirectory(directoryInput.getFile(), dest);
//            }
//            input.jarInputs.each { JarInput jarInput ->
//                File dest = new File(project.projectDir,fixExtension.classRootDir)
//                CommonUtils.unzipFile(dest,jarInput.file)
//            }
//        }
        FixExtension fixExtension = classExtension.fixExt;
        if (fixExtension.useFixTransform) {
            transformInvocation.inputs.each { TransformInput input ->
                def versionClassDir = "$classExtension.buildWorkRootDir/$fixExtension.classRootDir/$fixExtension.versionCode"
                input.directoryInputs.each { DirectoryInput directoryInput ->
                    File dest = new File(project.projectDir, versionClassDir)
                    FileUtils.copyDirectory(directoryInput.getFile(), dest);
                }
                input.jarInputs.each { JarInput jarInput ->
                    File dest = new File(project.projectDir, versionClassDir)
                    CommonUtils.unzipFile(dest, jarInput.file)
                }
                // 生成table.txt文件
                println "versionClassDir = $versionClassDir"
                CommonUtils.buildFileMd5Table(new File(project.projectDir, versionClassDir))
            }
        }
    }
}
