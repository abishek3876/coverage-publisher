package org.jenkinsci.plugins.coveragepublisher;

public enum CoverageMetric {

	CLASS("Class"),
	METHOD("Method"),
	LINE("Line"),
	BRANCH("Line"),
	INSTRUCTION("Instruction"),
	COMPLEXITY("Complexity");
	
	private String name;
	
	private CoverageMetric(String name) {
		this.name = name;
	}
}
