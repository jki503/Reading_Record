package buckpal.cleanarchitecture.account.adapter;

import static buckpal.cleanarchitecture.account.domain.Account.*;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import buckpal.cleanarchitecture.account.application.port.in.SendMoneyCommand;
import buckpal.cleanarchitecture.account.application.port.in.SendMoneyUseCase;
import buckpal.cleanarchitecture.account.domain.Money;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class SendMoneyController {

	private final SendMoneyUseCase sendMoneyUseCase;

	@PostMapping("/accounts/send/{sourceAccountId}/{targetAccountId}/{amount}")
	public void sendMoney(
		@PathVariable("sourceAccountId") Long sourceAccountId,
		@PathVariable("targetAccountId") Long targetAccountId,
		@PathVariable("amount") Long amount
	) {
		SendMoneyCommand command = new SendMoneyCommand(
			new AccountId(sourceAccountId),
			new AccountId(targetAccountId),
			Money.of(amount)
		);

		sendMoneyUseCase.sendMoney(command);
	}

}
