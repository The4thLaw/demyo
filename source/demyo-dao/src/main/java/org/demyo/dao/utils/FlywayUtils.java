package org.demyo.dao.utils;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.output.RepairOutput;
import org.flywaydb.core.api.output.RepairResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.demyo.common.config.SystemConfiguration;

/**
 * Utilities to work with Flyway.
 */
public final class FlywayUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(FlywayUtils.class);

	/**
	 * Repairs the Flyway migrations if such a repair was requested in the {@link SystemConfiguration}.
	 * 
	 * @param flyway The configured Flyway instance.
	 */
	public static void repairIfNeeded(Flyway flyway) {
		if (SystemConfiguration.getInstance().isFlywayRepairRequired()) {
			LOGGER.info("A Flyway repair was requested");
			RepairResult repair = flyway.repair();
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("Flyway repair complete: {}", toString(repair));
			}
		}
	}

	private static String toString(RepairResult result) {
		return new ToStringBuilder(result, ToStringStyle.SHORT_PREFIX_STYLE)//
				.append("flywayVersion", result.flywayVersion)//
				.append("operation", result.operation)//
				.append("repairActions", result.repairActions)//
				.append("warnings", result.warnings)//
				.append("migrationsAligned", toString(result.migrationsAligned))//
				.append("migrationsDeleted", toString(result.migrationsDeleted))//
				.append("migrationsRemoved", toString(result.migrationsRemoved))//
				.build();
	}

	private static String toString(List<RepairOutput> list) {
		return "[" + list.stream().map(FlywayUtils::toString).collect(Collectors.joining(", ")) + "]";
	}

	private static String toString(RepairOutput output) {
		return new ToStringBuilder(output, ToStringStyle.SHORT_PREFIX_STYLE)//
				.append("filepath", output.filepath)//
				.append("version", output.version)//
				.append("description", output.description)//
				.build();
	}
}
