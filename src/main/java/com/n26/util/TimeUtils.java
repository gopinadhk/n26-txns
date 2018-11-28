package com.n26.util;

import java.time.Instant;

import org.springframework.stereotype.Service;

@Service
public class TimeUtils {

	public static Instant getCurrentInstant() {
		return Instant.now();
	}

	public static Instant getInstantAhead(long seconds) {
		return Instant.now().plusSeconds(seconds);
	}

	public static Instant getInstantAgo(long seconds) {
		return Instant.now().minusSeconds(seconds);
	}

}
