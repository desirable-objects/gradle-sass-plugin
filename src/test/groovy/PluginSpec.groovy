import com.cathive.sass.SassContext
import com.cathive.sass.SassFileContext
import desirableobjects.gradle.plugins.sass.CompileSassTask
import desirableobjects.gradle.plugins.sass.SassCompiler
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

import java.nio.file.Path
import java.nio.file.Paths

class PluginSpec extends Specification {

    def 'reads sass properties'() {

        given:
            Project project = createProject()
            project.evaluate()

        expect:
            project.tasks.compileSass instanceof CompileSassTask
            project.tasks.compileSass.inputDir == project.file('./test/resources')

    }

    def 'reads sass properties in custom task'() {

        given:
            Project project = createProject()
            project.tasks.create('doSass', CompileSassTask)
            project.evaluate()

        expect:
            project.tasks.doSass instanceof CompileSassTask
            project.tasks.doSass.inputDir == project.file('./test/resources')

    }

    private Project createProject(inputDirectory = './test/resources', includesDirectory = null) {
        final Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'sass'
        project.sass {
            inputDir = project.file(inputDirectory)
            outputDir = project.file('/tmp')
            includesDir = project.file(includesDirectory ?: inputDirectory)
        }
        return project
    }

}
