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

    void preCompile() {

        println "Sass Plugin: Precompiling sass"

        watches.each { String filename, SassContext context ->
            compiler.compile(filename, context)
        }
    }

    private Closure scan = { File inputFile ->

        Path filePath = inputs.relativize(inputFile.toPath())
        String relativePath = filePath.toString()

        if (!relativePath.contains(includes.toString())) {
            def context = compiler.createContext(inputFile.name)
            watches.put(inputFile.name, context)
        }

        def matcher
        inputFile.eachLine { line ->
            if ((matcher = line =~ /^\@import\ ["'](.*)["']/)) {
                String filename = matcher[0][1].split('/').last() + '.scss'

                if (!dependencies.containsKey(filename)) {
                    dependencies[filename] = []
                }

                if (!dependencies[filename].contains(inputFile.name)) {
                    dependencies[filename] << inputFile.name
                }
            }
        }
    }

    void compileDependenciesFor(String filename) {

        List<String> requiresCompilation = dependencies.containsKey(filename) ? dependencies[filename] : [filename]

        println "Sass Plugin: Compiling dependencies for ${filename}: ${requiresCompilation.join(', ')}"

        requiresCompilation.each { String watch ->
            SassContext sassContext = watches[watch]
            compiler.compile(watch, sassContext)
        }

    }

    void push(File file) {
        scan(file)
    }

}
