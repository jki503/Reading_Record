package buckpal.cleanarchitecture.account.application.port.out;

import buckpal.cleanarchitecture.account.domain.Account.AccountId;

public interface AccountLock {

	void lockAccount(AccountId accountId);

	void releaseAccount(AccountId accountId);
}
