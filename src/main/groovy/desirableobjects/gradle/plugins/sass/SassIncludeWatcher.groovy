package desirableobjects.gradle.plugins.sass

import java.nio.file.Path
import java.nio.file.WatchEvent

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY

class SassIncludeWatcher extends SassWatcher {

    SassIncludeWatcher(Path sourceDir, Path includesDir, File outputDir, String fileExtension, ContextBuilder contextBuilder) {
        super(sourceDir, includesDir, outputDir, fileExtension, contextBuilder)
    }

    @Override
    protected void takeAction(WatchEvent event) {
        Path context = event.context() as Path

        if (event.kind().name() == ENTRY_MODIFY.name()) {
            if (context.fileName.toString().endsWith('.' + fileExtension)) {
                contexts.compileDependenciesFor(context.fileName.toString())
            }
        }
    }

}
