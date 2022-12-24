package buckpal.cleanarchitecture.account.application.port.out;

import buckpal.cleanarchitecture.account.domain.Account;

public interface UpdateAccountStatePort {

	void updateActivities(Account account);

}
