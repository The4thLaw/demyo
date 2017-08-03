appender("console", ConsoleAppender) {
	encoder(PatternLayoutEncoder) {
		pattern = "%d{dd.MM.yyyy HH:mm:ss} [%-5level] %logger{36} - %msg%n"
	}
}

appender("demyo", RollingFileAppender ) {
	file = "target/logs/demyo.log"
	append = true
	encoder(PatternLayoutEncoder) {
		pattern = "%d{dd.MM.yyyy HH:mm:ss} [%-5level] %logger{36} - %msg%n"
	}
}

appender("3rdParty", RollingFileAppender ) {
	file = "target/logs/3rd-party.log"
	append = true
	encoder(PatternLayoutEncoder) {
		pattern = "%d{dd.MM.yyyy HH:mm:ss} [%-5level] %logger{36} - %msg%n"
	}
}
  
logger("org.demyo", DEBUG, ["console", "demyo"], false)

root(WARN, ["3rdParty"])
