package desirableobjects.gradle.plugins.sass

import groovy.transform.CompileStatic
import org.gradle.api.Project

@CompileStatic
class SassExtension {

    public File inputDir
    public File outputDir
    public File includesDir
    public String fileExtension
    public Boolean watch
    public String outputStyle

    SassExtension(Project project) {

    }

}
