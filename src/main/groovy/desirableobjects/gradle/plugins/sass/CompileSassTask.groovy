package desirableobjects.gradle.plugins.sass

import com.cathive.sass.SassContext
import groovy.io.FileType
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.WatchService

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY

class CompileSassTask extends DefaultTask {

    @InputDirectory
    public File inputDir = project.sass.inputDir

    @OutputDirectory
    public File outputDir = project.sass.outputDir

    public File includesDir = project.sass.includesDir ?: project.sass.inputDir
    public String fileExtension = project.sass.fileExtension
    public Boolean watch

    @TaskAction
    def compileSass() {

        Path sourceDir = Paths.get(inputDir.path)
        Path sourceIncludeDir = Paths.get(includesDir.path)

        ContextBuilder contextBuilder = new ContextBuilder(sourceDir, sourceIncludeDir, outputDir.toPath())
        contextBuilder.buildHierarchy()
        contextBuilder.preCompile()

        if (watch) {
            SassSourceWatcher sourceWatcher = new SassSourceWatcher(sourceDir, sourceIncludeDir, outputDir, fileExtension, contextBuilder)
            SassIncludeWatcher includeWatcher = new SassIncludeWatcher(sourceIncludeDir, sourceIncludeDir, outputDir, fileExtension, contextBuilder)
            Thread th = new Thread(sourceWatcher, "SourceWatcher")
            Thread th2 = new Thread(includeWatcher, "IncludeWatcher")

            sourceWatcher.register(sourceDir)
            includeWatcher.register(sourceIncludeDir)

            th2.start()
            th.start()
        }

    }


}
