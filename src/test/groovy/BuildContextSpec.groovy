import desirableobjects.gradle.plugins.sass.CompileSassTask
import desirableobjects.gradle.plugins.sass.ContextBuilder
import org.gradle.api.Project
import spock.lang.Specification

import java.nio.file.Path
import java.nio.file.Paths

class BuildContextSpec extends Specification {

    def 'build include context'() {

        given:
            ContextBuilder contextBuilder = new ContextBuilder()
            File includesDir = new File('src/test/resources/example/includes')

        when:
            contextBuilder.buildIncludesContext(includesDir)

        then:
            contextBuilder.includeContext == ['include.scss': [], 'include2.scss': []]
    }

    def 'Build import context'() {

        given:
            ContextBuilder contextBuilder = new ContextBuilder()
            File inputDir = new File('src/test/resources/example')
            Path inputPath = Paths.get(inputDir.path)
            Path includesDir = Paths.get('src/test/resources/example/includes')

        when:
            contextBuilder.includeContext = ['include.scss': [], 'include2.scss': []]
            contextBuilder.buildInputContext(inputDir, inputPath, includesDir)

        then:
            contextBuilder.includeContext == ['include.scss': ['base.scss'], 'include2.scss': []]

    }



}
