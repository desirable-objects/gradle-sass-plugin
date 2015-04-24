package desirableobjects.gradle.plugins.sass

import com.cathive.sass.SassCompilationException
import com.cathive.sass.SassContext
import com.cathive.sass.SassFileContext
import com.cathive.sass.SassOptions
import com.cathive.sass.SassOutputStyle

import java.nio.file.Path

class SassCompiler {

    static compile(File outputDir, String originalFilename, SassContext ctx) {

        println 'Sass Plugin: Compiling ' + originalFilename

        String outputFilename = (originalFilename - '.scss') + '.css'
        FileOutputStream fos = new FileOutputStream("${outputDir.path}/${outputFilename}", true)

        try {
            ctx.compile(fos)
        } catch (SassCompilationException e) {
            System.err.println(e.getMessage())
            throw e
        } catch (IOException e) {
            System.err.println(String.format("Compilation failed: %s", e.getMessage()))
            throw e
        } finally {
            fos.close()
        }
    }

    static createContext(Path sourceDir, Path includesDir, String filename) {
        SassContext ctx = SassFileContext.create(sourceDir.resolve(filename)) as SassContext
        SassOptions options = ctx.getOptions()
        options.setIncludePath(includesDir)
        options.setOutputStyle(SassOutputStyle.COMPRESSED)
        return ctx
    }

}