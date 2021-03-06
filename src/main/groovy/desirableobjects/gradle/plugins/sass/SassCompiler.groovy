package desirableobjects.gradle.plugins.sass

import com.cathive.sass.SassCompilationException
import com.cathive.sass.SassContext
import com.cathive.sass.SassFileContext
import com.cathive.sass.SassOptions
import com.cathive.sass.SassOutputStyle
import sun.font.FontScalerException

import java.nio.file.Path

class SassCompiler {

    File outputDir
    Path sourceDir
    Path includesDir

    SassCompiler(Path sourceDir, Path includesDir, File outputDir) {
        this.includesDir = includesDir
        this.outputDir = outputDir
        this.sourceDir = sourceDir
    }

    void compile(String originalFilename, SassContext ctx) {

        println 'Sass Plugin: Compiling ' + originalFilename

        String outputFilename = (originalFilename - '.scss') + '.css'
        FileOutputStream fos = new FileOutputStream("${outputDir.path}/${outputFilename}", true)

        try {
            ctx.compile(fos)
        } catch (SassCompilationException e) {
            System.err.println("Sass Plugin: ${e.getMessage()}")
            throw e
        } catch (IOException e) {
            System.err.println("Sass Plugin: Compilation failed: ${e.getMessage()}")
            throw e
        } finally {
            fos.close()
        }
    }

    SassContext createContext(String filename) {
        SassContext ctx = SassFileContext.create(sourceDir.resolve(filename)) as SassContext
        SassOptions options = ctx.getOptions()
        options.setIncludePath(includesDir)
        options.setOutputStyle(SassOutputStyle.COMPRESSED)
        return ctx
    }

}