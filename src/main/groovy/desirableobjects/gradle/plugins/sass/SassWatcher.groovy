package desirableobjects.gradle.plugins.sass

import sun.reflect.generics.reflectiveObjects.NotImplementedException

import java.nio.file.Path
import java.nio.file.WatchEvent
import java.nio.file.WatchKey
import java.nio.file.WatchService

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY

class SassWatcher implements Runnable {

    protected WatchService myWatcher
    protected ContextBuilder contexts
    protected File outputDir
    protected String fileExtension
    protected Path sourceDir
    protected Path includesDir

    public SassWatcher(Path sourceDir, Path includesDir, File outputDir, String fileExtension, ContextBuilder contextBuilder) {
        this.myWatcher = sourceDir.getFileSystem().newWatchService()
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
                    takeAction(event)
                }
                key.reset();
                key = myWatcher.take();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        println 'Sass Plugin: Stopping watch'
    }

    protected void takeAction(WatchEvent event) {
        throw new NotImplementedException()
    }
}
