import desirableobjects.gradle.plugins.sass.CompileSassTask
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

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

    private Project createProject() {
        final Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'sass'
        project.sass {
            inputDir = project.file('./test/resources')
            outputDir = project.file('/tmp')
        }
        return project
    }

}
