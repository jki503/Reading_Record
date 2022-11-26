package buckpal.cleanarchitecture.account.application.port.out;

import static buckpal.cleanarchitecture.account.domain.Account.*;

import java.time.LocalDateTime;

import buckpal.cleanarchitecture.account.domain.Account;

public interface LoadAccountPort {
	Account loadAccount(AccountId accountId, LocalDateTime now);
}
