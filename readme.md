Gradle SASS plugin using the native libsass library and cathive JNI bindings.

This plugin is in very early stages. Pull requests are welcome and encouraged!

Advantages
==========
* It's really really fast!

Installation
============
Dependencies don't seem to work properly, so use this:

```groovy
buildscript {
    dependencies {
        classpath 'com.cathive.sass:com.cathive.sass:3.1.0.1'
        classpath 'desirableobjects.gradle.plugins:gradle-sass-plugin:0.3'
    }
}
```

Using the plugin
================

```groovy
apply plugin: 'sass'

sass {
    fileExtension = 'scss'
    inputDir = file('src/main/scss')
    includesDir = file('src/main/scss/include')
    outputDir = file('src/ratpack/public/css')
}

task onlyCompileSass(type: desirableobjects.gradle.plugins.sass.CompileSassTask) {
}

task compileAndWatchSass(type: desirableobjects.gradle.plugins.sass.CompileSassTask) {
    watch = true
}
```

Roadmap
=======
* Issues as below
* Fixing the manual cathive dependency
* Better scss error logging

Current known issues
====================

* Can't run the compileSass task itself (doesn't see the extension?)
* Exceptions/bad scss kill the watcher
* Delete is not supported
* The insides need a lot of rejigging