var builder = require('@jenkins-cd/js-builder');

var gulp        = require('gulp'),
    babel        = require('gulp-babel'),
    sourcemaps    = require('gulp-sourcemaps');

// build all the JavaScript things
gulp.task('build-script', function() {
    var src = [
        './src/main/js/*.*'
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

builder.bundle('src/main/js/CoverageTemplates.jsx');