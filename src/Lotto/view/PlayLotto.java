package Lotto.view;

import java.util.InputMismatchException;
import java.util.Scanner;

import Lotto.Controller.LottoManager;
import Lotto.Controller.illegelNumberException;
import Lotto.model.vo.Lotto;

public class PlayLotto {

	private Scanner sc;
	private LottoManager lm;
	private int num, count, place;

	public PlayLotto() {

		lm = new LottoManager();
		sc = new Scanner(System.in);
		this.Lotto_Menu();
	}

	public void Lotto_Menu() {

		while (true) {

			try {

				lm.ViewFstMenu();
				System.out.println();
				System.out.println("(1) 로또복권 구매(자동) ");
				System.out.println("(2) 당첨번호 및 보너스넘버 생성 ");
				System.out.println("(3) 당첨결과 업데이트");
				System.out.println("(4) 지역별 당첨 통계");
				System.out.println("(5) 로그 전체 삭제 ");
				System.out.println("(6) 종료 \n");

				System.out.print("메뉴번호 입력 >> ");
				num = sc.nextInt();

				switch (num) {
				case 1:
					System.out.print("구매지점 선택 (1.잠실  2.역삼  3.목동  4.성수) >> ");
					place = sc.nextInt();
					new illegelNumberException().placeException(place);

					System.out.print("구매 수량 >> ");
					count = sc.nextInt();
					new illegelNumberException().payException(count);

					lm.Paylotto(count, place);
					break;

				case 2:
					lm.CreateWinNum();
					lm.CreateBonusNum();
					break;

				case 3:
					lm.showResult();
					break;

				case 4:
					lm.showAreaStat();
					break;

				case 5:
					lm.deleteLog();
					break;

				case 6:
					System.out.println("종료합니다.");
					return;

				default:
					System.out.println("메뉴에 해당하는 번호가 아닙니다.");
					break;
				}
			} catch (InputMismatchException e) {
				System.out.println("메뉴에 해당하는 숫자만 입력해주세요.^^");
				sc.next();
			} catch (illegelNumberException e) {
				System.out.println(e.getMessage());
			}
			System.out.println("계속하시려면 아무키나 입력해주세요...");
			sc.next();

		}

	}

}