package desirableobjects.gradle.plugins.sass

import com.cathive.sass.SassContext
import desirableobjects.gradle.plugins.sass.ContextBuilder
import desirableobjects.gradle.plugins.sass.SassCompiler
import desirableobjects.gradle.plugins.sass.SassWatcher
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path

class SassWatcherSpec extends Specification {

    static Thread th

    @Shared SassWatcher fileWatcher
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

        fileWatcher = new SassWatcher(source, includes, output.toFile(), 'scss', contextBuilder)
        fileWatcher.register(source)

    }

    def cleanup() {
        project.deleteDir()
    }

    def 'watch a new base file'() {

        given:
            String filename = 'demo.scss'
            th = new Thread(fileWatcher, "FileWatcher")
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
            th = new Thread(fileWatcher, "FileWatcher")
            th.start()

        and:
            originalFile << "h1 {\n color: #000000;\n }"
            Thread.sleep(3000)

        then:
            1 * contextBuilder.compiler.createContext(filename) >> { return sassContext }
            2 * contextBuilder.compiler.compile(filename, _ as SassContext)
            0 * _

    }

    def 'compile dependencies for existing files'() {

        given:
            String filename = 'hasimports.scss'
            File include = new File(includes.toFile(), 'youwantthis.scss')
            th = new Thread(fileWatcher, "FileWatcher")
            th.start()

        when:
            new File(source.toFile(), filename) << "@import '${include.path}';\nh1 {\n color: #ffffff;\n }"

        and:
            include << 'span {\ncolor: #fef1f0;\n}'
            Thread.sleep(1000)

        then:
            1 * contextBuilder.compiler.createContext(filename) >> { return sassContext }
            2 * contextBuilder.compiler.compile(filename, _ as SassContext)
            0 * _

    }

}
