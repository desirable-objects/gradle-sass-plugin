package desirableobjects.gradle.plugins.sass

import com.cathive.sass.SassContext
import groovy.io.FileType

import java.nio.file.Path

class ContextBuilder {

    Path inputs
    Path includes
    Path outputs
    SassCompiler compiler

    Map<String, List<String>> dependencies = [:]
    Map<String, SassContext> watches = [:]

    ContextBuilder(Path inputs, Path includes, Path outputs) {
        this.inputs = inputs
        this.includes = includes
        this.outputs = outputs

        compiler = new SassCompiler(inputs, includes, outputs.toFile())
    }

    void buildHierarchy() {

        includes.toFile().eachFile(FileType.FILES) { File includeFile ->
            dependencies.put(includeFile.name, [])
        }

        inputs.toFile().eachFile(FileType.FILES, scan)

    }

    private Closure scan = { File inputFile ->

        watches.put(inputFile.name, compiler.createContext(inputFile.name))

        def matcher
        inputFile.eachLine { line ->
            if ((matcher = line =~ /^\@import\ ["'](.*)["']/)) {
                String filename = matcher[0][1].split('/').last() + '.scss'
                if (!dependencies[filename].contains(inputFile.name)) {
                    dependencies[filename] << inputFile.name
                }
            } else {
                if (line.trim() != '') {
                    println "escaping at " + line
                }
            }
        }
    }

    void compileDependenciesFor(String filename) {

        List<String> requiresCompilation = dependencies.containsKey(filename) ? dependencies[filename] : [filename]

        requiresCompilation.each { String watch ->
            SassContext sassContext = watches[watch]
            compiler.compile(watch, sassContext)
        }

    }

    void push(File file) {
        scan(file)
    }

}
