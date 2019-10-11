package com.echsylon.example.rest.account;

import java.math.BigDecimal;
import java.util.Objects;

public class AccountData {
	private final String address;
	private final BigDecimal balance;

	public AccountData(String address, BigDecimal balance) {
		this.address = Objects.requireNonNull(address, "Address cannot be null");
		this.balance = Objects.requireNonNull(balance, "Balance cannot be null");
	}

	public String getAddress() {
		return address;
	}

	public BigDecimal getBalance() {
		return balance;
	}
}
