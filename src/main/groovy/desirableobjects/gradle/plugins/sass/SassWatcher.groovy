package desirableobjects.gradle.plugins.sass

import com.cathive.sass.SassContext

import java.nio.file.Path
import java.nio.file.WatchEvent
import java.nio.file.WatchKey
import java.nio.file.WatchService

class SassWatcher implements Runnable {

    private WatchService myWatcher
    private Map<String, SassContext> contexts
    private File outputDir
    private String fileExtension
    private Path sourceDir
    private Path includesDir

    public SassWatcher(Path sourceDir, Path includesDir, File outputDir, String fileExtension, WatchService myWatcher, Map<String, SassContext> contexts) {
        this.myWatcher = myWatcher
        this.contexts = contexts
        this.outputDir = outputDir
        this.fileExtension = fileExtension
        this.sourceDir = sourceDir
        this.includesDir = includesDir
    }

    @Override
    public void run() {
        try {
            WatchKey key = myWatcher.take();
            while(key != null) {
                for (WatchEvent event : key.pollEvents()) {
                    String filename = event.context()
                    if(filename.endsWith('.'+fileExtension)) {
                        if (contexts[filename]) {
                            SassCompiler.compile(outputDir, filename, contexts[filename])
                        } else {
                            contexts[filename] = SassCompiler.createContext(sourceDir, includesDir, filename)
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
