package com.echsylon.example.rest.offer;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class AtomData {
	private final Map<String, BigDecimal> transfers;
	private final Set<String> signatures;

	public AtomData(Map<String, BigDecimal> transfers, Set<String> signatures) {
		this.transfers = Objects.requireNonNull(transfers, "Transfers cannot be null");
		this.signatures = Objects.requireNonNull(signatures, "Signatures cannot be null");
	}

	public Map<String, BigDecimal> getTransfers() {
		return transfers;
	}

	public Set<String> getSignatures() {
		return signatures;
	}
}
