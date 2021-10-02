package Lotto.model.vo;

import java.util.ArrayList;
import java.util.Random;

import Lotto.model.comparator.ascLotto;

public class Lotto {
	
	private ArrayList<Integer> lotto_Num;
	private String location;
	private int Sequence_num;
	private int grade;


	public Lotto() {
		
		lotto_Num = new ArrayList<Integer>();
		
	}

	public void SetLotto_Num() {
		
		this.lotto_Num.clear();
		
		for(int i = 0; i < 6; i++) {
			this.lotto_Num.add(new Random().nextInt(45)+1);
			
			for(int j = 0; j < i; j++) {
				if(lotto_Num.get(i)==lotto_Num.get(j)) {
					this.lotto_Num.remove(i);
					i--; 
					break;
				}
			}
		}
		this.lotto_Num.sort(new ascLotto());
	}

	public ArrayList<Integer> getLotto_Num() {
		return lotto_Num;
	}
		
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public int getSequence_num() {
		return Sequence_num;
	}
	
	public void setSequence_num(int sequence_num) {
		Sequence_num = sequence_num;
	}
	
	public int getGrade() {
		return grade;
	}
	
	public void setGrade(int grade) {
		this.grade = grade;
	}

	@Override
	public String toString() {
		
		String str = "";
		String GL = (grade == 0 || location == "")? "" : "("+grade+"등-"+location+")";
		for(int i : lotto_Num) {
			if(i < 10)
				str += "0" + i + "  ";
			else
				str += i + "  ";
		}
		return "자 동  " + str + GL;
	}
}
