package buckpal.cleanarchitecture.account.application.service;

import static buckpal.cleanarchitecture.account.domain.Account.*;

import java.time.LocalDateTime;

import buckpal.cleanarchitecture.account.application.port.in.GetAccountBalanceQuery;
import buckpal.cleanarchitecture.account.application.port.out.LoadAccountPort;
import buckpal.cleanarchitecture.account.domain.Money;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GetAccountBalanceService implements GetAccountBalanceQuery {

	private final LoadAccountPort loadAccountPort;

	@Override
	public Money getAccountBalance(AccountId accountId) {
		return loadAccountPort.loadAccount(accountId, LocalDateTime.now())
			.calculateBalance();
	}
}
