package desirableobjects.gradle.plugins.sass

import com.cathive.sass.SassContext
import groovy.io.FileType
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.WatchService
import java.text.SimpleDateFormat

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY

class CompileSassTask extends DefaultTask {

    @InputDirectory
    public File inputDir = project.sass.inputDir

    @OutputDirectory
    public File outputDir = project.sass.outputDir

    public File includesDir = project.sass.includesDir
    public String fileExtension = project.sass.fileExtension
    public Boolean watch

    @TaskAction
    def compileSass() {

        Map<String, SassContext> contexts = [:]
        Path sourceDir = Paths.get(inputDir.path)
        Path sourceIncludeDir = includesDir ? Paths.get(includesDir.path) : sourceDir

        long start = System.currentTimeMillis()
        inputDir.eachFileRecurse(FileType.FILES) { File inputFile ->
            contexts.put(inputFile.name, SassCompiler.createContext(sourceDir, sourceIncludeDir, inputFile.name))
        }
        long consumed = System.currentTimeMillis() - start
        println('Sass Plugin: Compiled sass in ' + new SimpleDateFormat("ss").format(consumed) + ' seconds')

        contexts.each { String originalFilename, SassContext ctx ->
            SassCompiler.compile(outputDir, originalFilename, ctx)
        }

        if (watch) {
            WatchService myWatcher = sourceDir.getFileSystem().newWatchService()

            SassWatcher fileWatcher = new SassWatcher(sourceDir, sourceIncludeDir, outputDir, fileExtension, myWatcher, contexts)
            Thread th = new Thread(fileWatcher, "FileWatcher");
            th.start();

            sourceDir.register(myWatcher, ENTRY_CREATE, ENTRY_MODIFY)
        }

    }

}
