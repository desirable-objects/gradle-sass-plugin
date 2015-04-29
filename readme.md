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

task compileAndWatchSass(type: desirableobjects.gradle.plugins.sass.CompileSassTask) {
    fileExtension = 'scss'
    inputDir = file('src/main/scss')
    includesDir = file('src/main/scss/include')
    outputDir = file('src/ratpack/public/css')
    watch = true
}
```

Roadmap
=======
* Issues as below
* Fixing the manual cathive dependency
* Better scss error logging
* Split the watch and compile tasks

Current known issues
====================

* Exceptions/bad scss kill the watcher
* Delete is not supported
* The insides need a lot of rejigging