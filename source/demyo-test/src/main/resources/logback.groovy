envCi = System.getProperty("CI");
isCi = envCi != null && envCi.length() != 0;

appender("console", ConsoleAppender) {
	encoder(PatternLayoutEncoder) {
		pattern = "%d{dd.MM.yyyy HH:mm:ss} [%-5level] %logger{36}:%L - %msg%n"
	}
}

appender("demyo", RollingFileAppender ) {
	file = "target/logs/demyo.log"
	append = true
	encoder(PatternLayoutEncoder) {
		pattern = "%d{dd.MM.yyyy HH:mm:ss} [%-5level] %logger{36}:%L - %msg%n"
	}
    rollingPolicy(TimeBasedRollingPolicy) {
		fileNamePattern = "target/logs/archives/demyo.%d.log.zip"
		// Keep 30 days
		maxHistory = 30
		compressionMode = "ZIP"
	}
}

appender("3rdParty", RollingFileAppender ) {
	file = "target/logs/3rd-party.log"
	append = true
	encoder(PatternLayoutEncoder) {
		pattern = "%d{dd.MM.yyyy HH:mm:ss} [%-5level] %logger{36}:%L - %msg%n"
	}
    rollingPolicy(TimeBasedRollingPolicy) {
		fileNamePattern = "target/logs/archives/3rd-party.%d.log.zip"
		// Keep 30 days
		maxHistory = 30
		compressionMode = "ZIP"
	}
}

if (isCi) {
    logger("org.demyo", DEBUG, ["console", "demyo"], false)
    
    root(WARN, ["console", "3rdParty"])
} else {
    logger("org.demyo", DEBUG, ["demyo"], false)
    
    root(WARN, ["3rdParty"])
}
