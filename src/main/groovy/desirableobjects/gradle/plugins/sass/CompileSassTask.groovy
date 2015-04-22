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

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY

class CompileSassTask extends DefaultTask {

    @InputDirectory
    public File inputDir

    @OutputDirectory
    public File outputDir

    public String fileExtension

    @TaskAction
    def compileSass() {

        Map<String, SassContext> contexts = [:]
        Path sourceDir = Paths.get(inputDir.path)

        inputDir.eachFileRecurse(FileType.FILES) { File inputFile ->
            contexts.put(inputFile.name, SassCompiler.createContext(sourceDir, inputFile.name))
        }

        contexts.each { String originalFilename, SassContext ctx ->
            SassCompiler.compile(outputDir, originalFilename, ctx)
        }

        WatchService myWatcher = sourceDir.getFileSystem().newWatchService();

        SassWatcher fileWatcher = new SassWatcher(sourceDir, outputDir, fileExtension, myWatcher, contexts)
        Thread th = new Thread(fileWatcher, "FileWatcher");
        th.start();

        sourceDir.register(myWatcher, ENTRY_CREATE, ENTRY_MODIFY);

    }

}
