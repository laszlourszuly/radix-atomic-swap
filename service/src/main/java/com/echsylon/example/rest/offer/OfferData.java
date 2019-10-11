package com.echsylon.example.rest.offer;

import java.math.BigDecimal;
import java.util.Objects;

public class OfferData {
	private final BigDecimal offer;
	private final BigDecimal request;
	private final String partnerAddress;

	public OfferData(BigDecimal offer, BigDecimal request, String address) {
		this.offer = Objects.requireNonNull(offer, "Offer cannot be null");
		this.request = Objects.requireNonNull(request, "Request cannot be null");
		this.partnerAddress = Objects.requireNonNull(address, "Address cannot be null");
	}

	public BigDecimal getOffer() {
		return offer;
	}

	public BigDecimal getRequest() {
		return request;
	}

	public String getPartnerAddress() {
		return partnerAddress;
	}
}
