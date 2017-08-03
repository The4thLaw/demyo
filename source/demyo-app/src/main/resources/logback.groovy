import org.demyo.common.config.SystemConfiguration

//scan("5 seconds")
//statusListener(OnConsoleStatusListener)

def USER_HOME = System.getProperty("user.home")
def LOG_DIR = SystemConfiguration.instance.userDirectory.absolutePath + "/logs"
//System.err.println("Logs go to \${LOG_DIR}");

appender("console", ConsoleAppender) {
	encoder(PatternLayoutEncoder) {
		pattern = "%d{dd.MM.yyyy HH:mm:ss} [%-5level] %logger{36} - %msg%n"
	}
}

appender("demyo", RollingFileAppender ) {
	file = "\${LOG_DIR}/demyo.log"
	append = true
	encoder(PatternLayoutEncoder) {
		pattern = "%d{dd.MM.yyyy HH:mm:ss} [%-5level] %logger{36} - %msg%n"
	}
	rollingPolicy(TimeBasedRollingPolicy) {
		fileNamePattern = "\${LOG_DIR}/archives/demyo.%d.log.zip"
		// Keep 30 days
		maxHistory = 30
		compressionMode = "ZIP"
	}
}

appender("3rdParty", RollingFileAppender ) {
	file = "\${LOG_DIR}/3rd-party.log"
	append = true
	encoder(PatternLayoutEncoder) {
		pattern = "%d{dd.MM.yyyy HH:mm:ss} [%-5level] %logger{36} - %msg%n"
	}
	rollingPolicy(TimeBasedRollingPolicy) {
		fileNamePattern = "\${LOG_DIR}/archives/3rd-party.%d.log.zip"
		// Keep 30 days
		maxHistory = 30
		compressionMode = "ZIP"
	}
}
  
logger("org.demyo", ${config.core.log.level}, ["console", "demyo"], false)

root(WARN, ["console", "3rdParty"])
