package desirableobjects.gradle.plugins.sass

import com.cathive.sass.SassContext
import groovy.io.FileType

import java.nio.file.Path

class ContextBuilder {

    Map<String, SassContext> context = [:]
    Map<String, List<String>> includeContext = [:]

    void buildInputContext(File inputDir, Path inputPath, Path includesPath) {

        inputDir.eachFileRecurse(FileType.FILES) { File inputFile ->
            context.put(inputFile.name, SassCompiler.createContext(inputPath, includesPath, inputFile.name))

            def matcher
            inputFile.eachLine { line ->
                if ((matcher = line =~ /^\@import\ ["'](.*)["']/)) {
                    String filename = matcher[0][1].split('/').last()+'.scss'
                    includeContext[filename] << inputFile.name
                } else {
                    if (line.trim() != '') {
                        println "escaping at " + line
                    }
                }
            }
        }

    }

    void buildIncludesContext(File dir) {
        dir.eachFileRecurse(FileType.FILES) { File includeFile ->
            includeContext.put(includeFile.name, [])
        }
    }

}
