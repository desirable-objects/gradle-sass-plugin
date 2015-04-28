package desirableobjects.gradle.plugins.sass

import com.cathive.sass.SassContext
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class ContextBuilderSpec extends Specification {

    Path source = Paths.get('src/test/resources/hierarchy')
    Path includes = Paths.get('src/test/resources/hierarchy/includes')
    Path outputs = Files.createTempDirectory('outputs')

    @Shared ContextBuilder builder

    def setup() {
        builder = new ContextBuilder(source, includes, outputs)
    }

    def 'Build a context hierarchy'() {

        given:
            Map<String, List<String>> expected = [
                    'include.scss': ['base.scss', 'base2.scss'],
                    'include2.scss': [],
                    'include3.scss': ['base.scss']
            ]

        when:
            builder.buildHierarchy()

        then:
            builder.dependencies == expected

    }

    def 'build a watch list'() {

        given:
            SassContext sassContext = Mock(SassContext)
            builder.compiler = Mock(SassCompiler)

            Map<String, SassContext> expected = [
                    'base.scss': sassContext,
                    'base2.scss': sassContext
            ]

        when:
            builder.buildHierarchy()

        then:
            builder.compiler.createContext(_ as String) >> { return sassContext }
            0 * _

        and:
            builder.watches == expected

    }

    def 'compiles all dependencies'() {

        given:
            SassContext baseContext = Mock(SassContext)
            SassContext base2Context = Mock(SassContext)
            builder.compiler = Mock(SassCompiler)

        when:
            builder.buildHierarchy()
            builder.compileDependenciesFor('include.scss')

        then:
            1 * builder.compiler.createContext('base.scss') >> { return baseContext }
            1 * builder.compiler.createContext('base2.scss') >> { return base2Context }
            1 * builder.compiler.compile('base.scss', baseContext)
            1 * builder.compiler.compile('base2.scss', base2Context)
            0 * _

    }

    def 'compiles base file'() {

        given:
            SassContext baseContext = Mock(SassContext)
            builder.compiler = Mock(SassCompiler)

        when:
            builder.buildHierarchy()
            builder.compileDependenciesFor('base.scss')

        then:
            1 * builder.compiler.createContext('base.scss') >> { return baseContext }
            1 * builder.compiler.createContext('base2.scss') >> { return baseContext }
            1 * builder.compiler.compile('base.scss', baseContext)
            0 * _

    }

    def 'if a base file is created - determine its dependencies'() {

        given:
            SassContext baseContext = Mock(SassContext)
            builder.compiler = Mock(SassCompiler)

        when:
            builder.buildHierarchy()

        and:
            File base3 = new File(source.toFile(), 'base3.scss')
            base3.deleteOnExit()
            base3 << "@import 'includes/include';\n@import 'includes/include2';\n@import 'includes/include3';"

        then:
            1 * builder.compiler.createContext('base.scss') >> { return baseContext }
            1 * builder.compiler.createContext('base2.scss') >> { return baseContext }
            0 * _
            !builder.watches.containsKey('base3.scss')

        when:
            builder.push(base3)

        then:
            1 * builder.compiler.createContext('base3.scss') >> { return baseContext }
            0 * _

        and:
            builder.dependencies == [
                'include.scss': ['base.scss', 'base2.scss', 'base3.scss'],
                'include2.scss': ['base3.scss'],
                'include3.scss': ['base.scss', 'base3.scss']
            ]


    }

    def 'deletion'() {

    }

}
