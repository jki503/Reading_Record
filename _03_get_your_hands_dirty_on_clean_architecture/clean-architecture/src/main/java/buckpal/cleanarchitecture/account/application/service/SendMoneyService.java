package buckpal.cleanarchitecture.account.application.service;

import org.springframework.transaction.annotation.Transactional;

import buckpal.cleanarchitecture.account.application.port.in.SendMoneyCommand;
import buckpal.cleanarchitecture.account.application.port.in.SendMoneyUseCase;
import buckpal.cleanarchitecture.account.application.port.out.AccountLock;
import buckpal.cleanarchitecture.account.application.port.out.LoadAccountPort;
import buckpal.cleanarchitecture.account.application.port.out.UpdateAccountStatePort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
public class SendMoneyService implements SendMoneyUseCase {

	private final LoadAccountPort loadAccountPort;

	private final AccountLock accountLock;

	private final UpdateAccountStatePort updateAccountStatePort;

	@Override
	public boolean sendMoney(SendMoneyCommand command) {
		// TODO : 비즈니스 규칙 검증
		// TODO : 모델 상태 조작
		// TODO : 출력값 반환

		return false;
	}
}
