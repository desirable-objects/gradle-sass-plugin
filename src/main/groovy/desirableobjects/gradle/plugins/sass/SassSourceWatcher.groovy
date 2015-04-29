package desirableobjects.gradle.plugins.sass

import java.nio.file.Path
import java.nio.file.WatchEvent

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY

class SassSourceWatcher extends SassWatcher {

    SassSourceWatcher(Path sourceDir, Path includesDir, File outputDir, String fileExtension, ContextBuilder contextBuilder) {
        super(sourceDir, includesDir, outputDir, fileExtension, contextBuilder)
    }

    @Override
    protected void takeAction(WatchEvent event) {
        Path context = event.context() as Path

        if (context.fileName.toString().endsWith('.' + fileExtension)) {

            if (ENTRY_CREATE.name() == event.kind().name()) {
                contexts.push(sourceDir.resolve(context).toFile())
            }

            if (ENTRY_MODIFY.name() == event.kind().name()) {
                contexts.compileDependenciesFor(context.fileName.toString())
            }

        }
    }

}
