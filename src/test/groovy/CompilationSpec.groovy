import com.cathive.sass.SassCompilationException
import com.cathive.sass.SassContext
import com.cathive.sass.SassFileContext
import desirableobjects.gradle.plugins.sass.SassCompiler
import spock.lang.Specification

import java.nio.file.Path
import java.nio.file.Paths

class CompilationSpec extends Specification {

    def 'Sass compilation failure throws exception'() {

        given:
            Path path = Paths.get('src/test/resources')
            SassContext ctx = SassFileContext.create(path.resolve('broken.scss'))

        when:
            SassCompiler.compile(new File('/tmp/'), 'broken.scss', ctx)

        then:
            thrown SassCompilationException

    }

}
