package org.demyo.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.the4thlaw.commons.services.image.BaseThumbnailService;

import org.demyo.common.config.SystemConfiguration;
import org.demyo.service.IThumbnailService;

/**
 * Implements the contract defined by {@link IThumbnailService}.
 */
@Service
public class ThumbnailService extends BaseThumbnailService implements IThumbnailService {

	/**
	 * Default constructor.
	 */
	public ThumbnailService() {
		super(SystemConfiguration.getInstance().getThumbnailDirectory(),
				SystemConfiguration.getInstance().getThumbnailQueueSize(),
				Optional.ofNullable(SystemConfiguration.getInstance().getMaxThumbnailThreads()));
	}
}
