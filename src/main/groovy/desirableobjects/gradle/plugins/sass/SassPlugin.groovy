package desirableobjects.gradle.plugins.sass

import org.gradle.api.Plugin
import org.gradle.api.Project

class SassPlugin implements Plugin<Project> {

    void apply(Project project) {
      project.task('compileSass', type: CompileSassTask)
    }

}
