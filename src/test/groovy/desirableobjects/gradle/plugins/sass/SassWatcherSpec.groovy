package desirableobjects.gradle.plugins.sass

import com.cathive.sass.SassContext
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path

class SassWatcherSpec extends Specification {

    Thread th
    Thread th2

    @Shared SassSourceWatcher sourceWatcher
    @Shared SassIncludeWatcher includeWatcher
    @Shared ContextBuilder contextBuilder
    @Shared Path project
    @Shared Path source
    @Shared Path output
    @Shared Path includes
    @Shared SassContext sassContext

    def setup() {

        sassContext = Mock(SassContext)

        project = Files.createTempDirectory('project')
        source = Files.createTempDirectory(project, 'source')
        output = Files.createTempDirectory(project, 'output')
        includes = Files.createTempDirectory(source, 'includes')

        contextBuilder = new ContextBuilder(source, includes, output)
        contextBuilder.compiler = Mock(SassCompiler)

        sourceWatcher = new SassSourceWatcher(source, includes, output.toFile(), 'scss', contextBuilder)
        sourceWatcher.register(source)

        includeWatcher = new SassIncludeWatcher(includes, includes, output.toFile(), 'scss', contextBuilder)
        includeWatcher.register(includes)

    }

    def cleanup() {
        if (th) th.interrupt()
        if (th2) th2.interrupt()
        project.deleteDir()
    }

    def 'watch a new base file'() {

        given:
            String filename = 'demo.scss'
            th = new Thread(sourceWatcher, "SourceWatcher")
            th.start()

        when:
            new File(source.toFile(), filename) << "h1 {\n color: #ffffff;\n }"
            Thread.sleep(1000)

        then:
            1 * contextBuilder.compiler.createContext(filename) >> { return sassContext }
            1 * contextBuilder.compiler.compile(filename, _ as SassContext)
            0 * _

    }

    def 'watch a modified file'() {

        given:
            String filename = 'thingy.scss'
            File originalFile = new File(source.toFile(), filename) << "h1 {\n color: #ffffff;\n }"
            contextBuilder.watches = ['thingy.scss': sassContext]

        expect:
            new File(source.toFile(), filename).exists()

        when:
            th = new Thread(sourceWatcher, "SourceWatcher")
            th.start()

        and:
            originalFile << "h1 {\n color: #000000;\n }"
            Thread.sleep(3000)

        then:
            1 * contextBuilder.compiler.createContext(filename) >> { return sassContext }
            2 * contextBuilder.compiler.compile(filename, _ as SassContext)
            0 * _

    }

    def 'when a new file is added, calculate its dependencies'() {

        given:
            String filename = 'hasimports.scss'
            File include = new File(includes.toFile(), 'youwantthis.scss')
            Path relative = source.relativize(include.toPath())
            String includeName = relative.toFile().path.split('\\.').first()

        expect:
            !contextBuilder.dependencies.containsKey(filename)

        when:
            th = new Thread(sourceWatcher, "SourceWatcher")
            th.start()

        and:
            new File(source.toFile(), filename) << "@import '${includeName}';\nh1 {\n color: #ffffff;\n }"
            Thread.sleep(1000)

        then:
            1 * contextBuilder.compiler.createContext(filename) >> { return sassContext }
            1 * contextBuilder.compiler.compile(filename, sassContext)
            0 * _

        and:
            contextBuilder.watches.containsKey(filename)
            contextBuilder.dependencies[relative.fileName.toString()] == [filename]

    }

    def 'when a dependency changes - compile things which depend on it'() {

        given:
            String filename = 'base.scss'
            File include = new File(includes.toFile(), 'include1.scss')
            Path relative = source.relativize(include.toPath())
            String includeName = relative.toFile().path.split('\\.').first()
            new File(source.toFile(), filename) << "@import '${includeName}';\nh1 {\n color: #ffffff;\n }"

        and:
            th = new Thread(sourceWatcher, "SourceWatcher")
            th2 = new Thread(includeWatcher, "IncludeWatcher")
            th.start()
            th2.start()

        when:
            include << 'h2 { color: #c0c0c0 }'
            Thread.sleep(3000)

        then:
            1 * contextBuilder.compiler.createContext(_) >> { return sassContext }
            1 * contextBuilder.compiler.compile(filename, sassContext)
            0 * _

    }

}
