 // packages 
var gulp        = require('gulp'),
    babel       = require('gulp-babel'),
    sourcemaps  = require('gulp-sourcemaps'),
    browserify  = require('browserify'),
    source      = require('vinyl-source-stream');

// build all the JavaScript things
gulp.task('babelify', function() {
    var src = [
        './src/main/js/*.js',
        './src/main/js/*.jsx'
        ];

    return gulp.src(src)
                .pipe(sourcemaps.init())
                .pipe(babel({
                    presets: [
                        'es2015',
                        'react'
                        ]
                    }))
                .pipe(sourcemaps.write('.'))
                .pipe(gulp.dest('./dist/js'));
});

gulp.task('default', ['babelify'], function() {
   return browserify('./dist/js/CoverageTemplates.js')
       .bundle()
       //Pass desired output filename to vinyl-source-stream
       .pipe(source('CoverageTemplates.js'))
       // Start piping stream to tasks!
       .pipe(gulp.dest('./target/classes/org/jenkins/ui/jsmodules/coverage-publisher'));
});