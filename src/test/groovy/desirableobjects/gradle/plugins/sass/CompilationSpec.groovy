package desirableobjects.gradle.plugins.sass

import com.cathive.sass.SassCompilationException
import com.cathive.sass.SassContext
import com.cathive.sass.SassFileContext
import desirableobjects.gradle.plugins.sass.SassCompiler
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class CompilationSpec extends Specification {

    def 'Sass compilation failure throws exception'() {

        given:
            Path path = Paths.get('src/test/resources')
            Path output = Files.createTempDirectory('sassy')
            SassContext ctx = SassFileContext.create(path.resolve('broken.scss'))
            SassCompiler sassCompiler = new SassCompiler(path, path, output.toFile())

        when:
            sassCompiler.compile('broken.scss', ctx)

        then:
            thrown SassCompilationException

    }

}
