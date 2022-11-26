package buckpal.cleanarchitecture.account.application.port.in;

import static buckpal.cleanarchitecture.account.domain.Account.*;

import javax.validation.constraints.NotNull;

import buckpal.cleanarchitecture.account.domain.Money;
import buckpal.cleanarchitecture.common.SelfValidating;
import lombok.Getter;

@Getter
public class SendMoneyCommand extends SelfValidating<SendMoneyCommand> {

	@NotNull
	private final AccountId sourceAccountId;

	@NotNull
	private final AccountId targetAccountId;

	@NotNull
	private final Money money;

	public SendMoneyCommand(AccountId sourceAccountId,
		AccountId targetAccountId, Money money) {
		this.sourceAccountId = sourceAccountId;
		this.targetAccountId = targetAccountId;
		this.money = money;
		this.validateSelf();
	}
}
