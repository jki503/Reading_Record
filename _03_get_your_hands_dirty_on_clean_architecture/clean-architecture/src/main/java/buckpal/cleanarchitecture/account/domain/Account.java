package buckpal.cleanarchitecture.account.domain;

import java.time.LocalDateTime;
import java.util.Optional;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Account {

	@Getter
	private final AccountId id;

	@Getter
	private final Money baselineBalance;

	@Getter
	private final ActivityWindow activityWindow;

	public static Account withoutId(
		Money baselineBalance,
		ActivityWindow activityWindow) {
		return new Account(null, baselineBalance, activityWindow);
	}

	public static Account withId(
		AccountId accountId,
		Money baselineBalance,
		ActivityWindow activityWindow) {
		return new Account(accountId, baselineBalance, activityWindow);
	}

	public Money calculateBalance() {
		return Money.add(
			this.baselineBalance,
			this.activityWindow.calculateBalance(this.id)
		);
	}

	public boolean withdraw(Money money, AccountId targetAccountId) {
		if (!mayWithdraw(money)) {
			return false;
		}

		Activity withdrawal = new Activity(
			this.id,
			this.id,
			targetAccountId,
			LocalDateTime.now(),
			money
		);

		this.activityWindow.addActivity(withdrawal);
		return true;
	}

	public boolean deposit(Money money, AccountId sourceAccountId) {
		Activity deposit = new Activity(
			this.id,
			sourceAccountId,
			this.id,
			LocalDateTime.now(),
			money);
		this.activityWindow.addActivity(deposit);
		return true;
	}

	private boolean mayWithdraw(Money money) {
		return Money.add(
			this.calculateBalance(),
			money.negate()
		).isPositive();
	}

	public Optional<AccountId> getId() {
		return Optional.ofNullable(this.id);
	}

	@Value
	public static class AccountId {
		private Long value;
	}
}
