package desirableobjects.gradle.plugins.sass

import com.cathive.sass.SassContext

import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.WatchEvent
import java.nio.file.WatchKey
import java.nio.file.WatchService

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY

class SassWatcher implements Runnable {

    private WatchService myWatcher
    private ContextBuilder contexts
    private File outputDir
    private String fileExtension
    private Path sourceDir
    private Path includesDir

    public SassWatcher(Path sourceDir, Path includesDir, File outputDir, String fileExtension, ContextBuilder contextBuilder) {
        this.myWatcher = FileSystems.getDefault().newWatchService()
        this.contexts = contextBuilder
        this.outputDir = outputDir
        this.fileExtension = fileExtension
        this.sourceDir = sourceDir
        this.includesDir = includesDir
    }

    public void register(Path dir) {
        dir.register(myWatcher, ENTRY_CREATE, ENTRY_MODIFY)
    }

    @Override
    public void run() {
        try {
            WatchKey key = myWatcher.take();
            while(key != null) {
                for (WatchEvent event : key.pollEvents()) {

                    String context = event.context() as String
                    if(context.endsWith('.'+fileExtension)) {

                        Path filePath = sourceDir.relativize(sourceDir.resolve(context))
                        String filename = filePath.toString()

                        if (ENTRY_CREATE.name() == event.kind().name()) {
                            contexts.push(sourceDir.resolve(context).toFile())
                        }

                        if (ENTRY_MODIFY.name() == event.kind().name()) {
                            contexts.compileDependenciesFor(filename)
                        }

                    }

                }
                key.reset();
                key = myWatcher.take();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        println 'Sass Plugin: Stopping watch'
    }
}
