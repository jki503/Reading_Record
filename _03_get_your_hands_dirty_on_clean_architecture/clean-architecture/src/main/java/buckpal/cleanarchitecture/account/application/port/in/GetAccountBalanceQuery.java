package buckpal.cleanarchitecture.account.application.port.in;

import static buckpal.cleanarchitecture.account.domain.Account.*;

import buckpal.cleanarchitecture.account.domain.Money;

public interface GetAccountBalanceQuery {

	Money getAccountBalance(AccountId accountId);

}
