# coverage-publisher
Code Coverage publisher plugin for Jenkins

This plugin supports publishing Code Coverage reports generated by the following tools to Jenkins:
- Jacoco
- SCoverage \[Planned]
- GCov \[Planned]

## Usage
Note that this plugin only helps in publishing the coverage reports. As such, it only makes sense to run this plugin as a Post-Build step. This plugin expects to the coverage data files *(Eg: \*.ec, \*.exec files for Jacoco coverage)* to be generated already from one of your build steps.

#### Coverage Publishing Tools
All the coverage tools supported by the plugin and any tools added as extentions from other plugins are listed in the drop-down, choose one or more tools from the drop down and give the configurations specific to each tool.

You can also choose the same tool multiple times if you want to split the coverage publishing report for some reason. *Eg: You can choose Jacoco tool twice, one for Unit test coverage and one for Integration test coverage.* Just make sure to give distinct names for each choice.

#### Run on Failed Builds
The Coverage publisher normally runs only if the build is Successful or Unstable when it reaches the point for the tool to run, but if you wan't the tool to also run on Failed builds, check this option.

The tool does not run on builds which are Aborted before it reaches the point for the tool to run.

#### Choosing the Health Thresholds
The Coverage publisher can set the build health based on some thresholds. If the coverage value is higher than or equal to the MAX threshold *(denoted by :sunny:)* the Health is reported as 100%. If the coverage value is less than the MIN threshold *(denoted by :cloud:)* the Health is reported as 0%. For coverage values between these thresholds, the Health is calculated proportionally.

#### Changing the Build Status based on the Thresholds
The Coverage publisher does not change the build status by default. But if this option is checked, then the build is set to "Failure" if the coverage is less than the MIN threashold. It is set to "Unstable" if the coverage is between MIN and MAX thresholds. It is set to "Success" if the coverage is higher than or equal to the MAX threshold.

#### Failing Build based on the delta change from previous build
The Coverage publisher can also fail the build if the coverage values fall below a specified percentage *(called the delta)* from the previous build. When a delta threshold is violated, the build Health is set to 0% by default.

### Pipeline Support
The Coverage publisher supports pipeline calls by defining proper `@Symbol`s for required classes. Use the Pipeline Syntax tool to get a pipeline step for the specified values.

## Extending the Plugin
If you want to add support for new coverage tools, feel free to open a Pull Request.

But if you feel don't feel like adding the tool to be a part of this plugin, you can also create your own plugin to parse the coverage data and delegate the publishing part to this plugin.

Just implement the `org.jenkinsci.plugins.coverage.CoverageTool` interface making sure to add a `public static` class implementing `CoverageTool.CoverageToolDescriptor` class inside the `CoverageTool` implementation you are creating, add the necessary `config.jelly` scripts for the configuration inputs your tool needs and voilaaa!!! Your tool becomes a part of the Coverage publisher.

Take a look at `org.jenkinsci.plugins.coverage.tools.jacoco.JacocoCoverageTool` for an example.
