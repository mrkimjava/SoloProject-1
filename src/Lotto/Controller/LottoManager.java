package Lotto.Controller;

import java.util.ArrayList;
import java.util.Random;

import Lotto.model.dao.LottoDao;
import Lotto.model.vo.Lotto;

public class LottoManager {

	private ArrayList<Lotto> Lotto;
	private LottoDao ldao;
	private int Sequence;

	public LottoManager() {

		ldao = new LottoDao();

	}

	public void Paylotto(int count, int place) {

		Sequence = ldao.getLastSequence() + 1;
		Lotto = new ArrayList<Lotto>();

		for (int i = 0; i < count; i++) {
			// 새로운 주소값을 생성해야 다른값이 계속 리스트에 들어감
			Lotto lotto = new Lotto();

			lotto.SetLotto_Num();
			lotto.setSequence_num(Sequence++);
			Lotto.add(lotto);
		}
		String[] date_place = ldao.insertLotto(Lotto, place);
		System.out.println("#     ###  ##### #####  ### ");
		System.out.println("#    #   #   #     #   #   #");
		System.out.println("#    #   #   #     #   #   #");
		System.out.println("####  ###    #     #    ### ");
		System.out.println("발행일:" + date_place[0]);
		System.out.println("구매지점:" + date_place[1]);
		System.out.println("----------------------------");
		for (Lotto l : Lotto) {
			System.out.println(l);
		}
		System.out.println();
	}

	public void CreateWinNum() {

		if (ldao.getLastSequence() == 0) {
			System.out.println("로또복권 구매 먼저 해주세요.");
			return;
		}
		if (ldao.getWinNumber() != null)
			System.out.println("\n이미 당첨번호가 생성되었습니다.(모든 내역을 삭제하시려면 로그 전체 삭제 메뉴를 눌러주세요.)");
		else {
			Lotto l = new Lotto();
			l.SetLotto_Num();
			ldao.addWinNumber(l);

			for (int i : ldao.getWinNumber().getLotto_Num()) {
				System.out.print(i + " ");
			}
			System.out.println();
		}
	}

	public void CreateBonusNum() {

		Lotto l = ldao.getWinNumber();
		if (l == null)
			return;
		if (l.getLotto_Num().size() != 0 && ldao.getBonusNumber() != 0) {
			return;
		}
		int Bonus_Num;
		while (true) {
			boolean b = true;
			Bonus_Num = new Random().nextInt(45) + 1;

			for (int i : l.getLotto_Num()) {
				if (Bonus_Num == i) {
					b = false;
					break;
				}
			}
			if (b) {
				ldao.addBonusNumber(Bonus_Num);
				break;
			}
		}
	}

	public void showResult() {

		if (ldao.getWinNumber() == null) {
			System.out.println("당첨번호 먼저 생성 ^^...");
			return;
		}
		if (ldao.getLastSequence() == 0) {
			System.out.println("로또복권 구매 먼저 해주세요.");
			return;
		}
		ldao.AddResult();

		for (Lotto l : ldao.getResult()) {
			System.out.println(l);
		}
		System.out.println();
	}

	public void showAreaStat() {

		if (ldao.getWinNumber() == null) {
			System.out.println("당첨번호 먼저 생성 ^^...");
			return;
		}
		if (ldao.getLastSequence() == 0) {
			System.out.println("로또복권 구매 먼저 해주세요.");
			return;
		}
		if (ldao.getNonResult().size() > 0) {
			System.out.println("당첨결과 업데이트 먼저 해주세요.");
			return;
		}
		ldao.getAreaStat();
	}

	public void deleteLog() {

		ldao.clearLog();
	}

	public void ViewFstMenu() {

		System.out.println("**     **** ************ **** ");
		System.out.println("**    ************************");
		System.out.println("**    **  **  **    **  **  **");
		System.out.println("**    **  **  **    **  **  **");
		System.out.println("************  **    **  ******");
		System.out.println("****** ****   **    **   **** ");
	}

}
