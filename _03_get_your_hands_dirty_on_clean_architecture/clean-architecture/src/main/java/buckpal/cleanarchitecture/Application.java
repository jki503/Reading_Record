package buckpal.cleanarchitecture;

import buckpal.cleanarchitecture.account.adapter.in.web.SendMoneyController;
import buckpal.cleanarchitecture.account.application.port.in.SendMoneyUseCase;
import buckpal.cleanarchitecture.account.application.service.SendMoneyService;

public class Application {

	public static void main(String[] args) {
		AccountRepository accountRepository = new AccountRepository();
		ActivityRepository activityRepository = new ActivityRepository();

		AccountPersistenceAdapter accountPersistenceAdapter =
			new AccountPersistenceAdapter(accountRepository, activityRepository);

		SendMoneyUseCase sendMoneyUseCase =
			new SendMoneyService(
				accountPersistenceAdapter, // LoadAccountStatePort
				accountPersistenceAdapter
			); // UpdateAccountStatePort

		SendMoneyController sendMoneyController = new SendMoneyController(sendMoneyUseCase);

		startProcessingWebRequests(sendMoneyController);
	}

}
