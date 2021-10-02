package Lotto.model.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import Lotto.model.vo.Lotto;
import jdbc.JDBCTemplete;

public class LottoDao extends JDBCTemplete {
	//jdbc 객체변수
	private Connection con = null; 
	private ResultSet rs = null;	
	private PreparedStatement pstmt = null; //sql 해석객체(statement 자식클래스) : 컴파일을 미리 한번만 하고 캐시에 담아 반복 수행 시 성능이 좋다
	private CallableStatement cstmt = null; //프로시저 해석객체(pstmt 자식클래스) 
	//사용빈도 높은 변수
	private int startSequence;
	private String sql;
	
	
	public String[] insertLotto(ArrayList<Lotto> lotto, int place) {
		   		   
		startSequence = lotto.get(0).getSequence_num();
		String[] date_place = new String[2];
		
		try {		   
			sql = "INSERT INTO LOTTONUM VALUES(?,?,sysdate,?,?,?,?,?,?,null)";
			con = getConnection();
			pstmt = con.prepareStatement(sql); //PSTMT 객체생성 : 생성시 메모리에 올라가게 되어 동일한 쿼리의 경우 인자만 달라지게 되므로 매번 컴파일이 필요하지 않다. 
			   								   //(재사용 시 반복문 밖으로)반복문 안에 있으면 맨 마지막 pstmt만 close() 처리됨.
			for(Lotto l : lotto) {
				pstmt.setInt(1, l.getSequence_num());
				pstmt.setString(2,"G"+place);
				for(int i = 0; i < l.getLotto_Num().size(); i++) {   
					pstmt.setInt(i+3, l.getLotto_Num().get(i));
				}				
				pstmt.addBatch();
				pstmt.clearParameters(); //한번 돌때마다 파라미터 클리어
			}		
			
			int count[] = pstmt.executeBatch(); //jdbc의 배치기능 (여러개 쿼리를 모아서 한꺼번에 송신, 퍼포먼스 증가)
			int countSum = 0;   			
			for(int i : count) {
				countSum += i;	
			}
			System.out.println("로또 자동번호 "+Math.abs(countSum/2)+"회 구매 완료\n");

			closeAll(null, pstmt, null);
			sql = "SELECT PAYDATE, LNAME FROM V_LOTTONUM WHERE SEQ = " + startSequence;
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			while(rs.next()) {				
				date_place[0] = rs.getString(1);
				date_place[1] = rs.getString(2);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeAll(con,pstmt,rs);
		}
		return date_place;
	}
	
	      	   
	public void addWinNumber(Lotto lotto) {
		
		try {		
			sql = "INSERT INTO WINNUMBER VALUES(?,?,?,?,?,?,null)";
			con = getConnection();
			pstmt = con.prepareStatement(sql);
			
			for(int i = 0; i < lotto.getLotto_Num().size(); i++) {
				pstmt.setInt(i+1, lotto.getLotto_Num().get(i));
			}	
			int res = pstmt.executeUpdate();
			if(res > 0)
				System.out.print("\n당첨 번호 : ");	
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeAll(con, pstmt, null);
		}
	}
	
	
	public void addBonusNumber(int bonusNum) {
		try {			
			sql = "UPDATE WINNUMBER SET BONUSNUM = " + bonusNum;
			con = getConnection();
			pstmt = con.prepareStatement(sql);
			int res = pstmt.executeUpdate();		
			if(res > 0)
				System.out.println("보너스 번호 : " + bonusNum + "\n");
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeAll(con, pstmt, null);
		}
	}
	
	   	   
	public Lotto getWinNumber() {
		
		Lotto winNum = new Lotto();
		try {			
			sql = "SELECT * FROM WINNUMBER";
			con = getConnection();
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();					
			
			while(rs.next()) {		
				for(int i = 1; i <=6; i++) {
					winNum.getLotto_Num().add(rs.getInt("WINNUM"+i));
				}		
			}
			if(winNum.getLotto_Num().size() == 0)
				return null;
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeAll(con, pstmt, rs);
		}
		return winNum;
	}
		   	
	public int getBonusNumber() {
		
		int BonusNumber = 0;
		try {			
			sql = "SELECT BONUSNUM FROM WINNUMBER";
			con = getConnection();   
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			while(rs.next()) {	
				BonusNumber = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeAll(con, pstmt, rs);
		}
		return BonusNumber;
	}		
	
	public ArrayList<Lotto> getNonResult(){
		ArrayList<Lotto> nonResult = new ArrayList<Lotto>();
		try {
			sql = "SELECT * FROM V_NONRESULT"; //view 호출문 (grade is null)
			con = getConnection();
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				Lotto lotto = new Lotto();
				for(int i = 1; i<=6; i++) {
					lotto.getLotto_Num().add(rs.getInt("NUM"+i));
				}
				lotto.setSequence_num(rs.getInt("SEQ"));
				nonResult.add(lotto);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeAll(con, pstmt, rs);
		}
		return nonResult;
	}
	
	
	public void AddResult() {	
		//1 grade가 null인 행 가져오기, 2 당첨번호,보너스번호 가져오기 3 비교 후 업데이트		
		ArrayList<Lotto> setResult = getNonResult();
		if(setResult.size() == 0) { 	//resultset이 false였다면 return;
			return;		
		}
		
		try {
			Lotto WinLotto = this.getWinNumber();	
			int bonusNum = this.getBonusNumber();
			int count; 
			boolean bonus;
		
			for(Lotto l : setResult) {			
				count = 0;
				bonus = false;

				for(int i : l.getLotto_Num()) {
					for(int j : WinLotto.getLotto_Num()) {
						if(i == j){
							count++;
						}
					}
					if(i == bonusNum) {
						bonus = true;
					}
				}	
				
				switch(count) {	//등수 update
				case 6 : l.setGrade(1); break;
				case 5 : if(bonus) {
						l.setGrade(2);
						break;
					 	}else {
					 		l.setGrade(3);
					 		break;
					 	}					
				case 4 : l.setGrade(4); break;
				case 3 : l.setGrade(5); break;
				default : l.setGrade(0); break;
				}
			}
			
			sql = "{CALL ADD_RESULT(?,?)}"; //프로시저 호출문	, 인자값 (등수, seq) -> seq를 찾아 등수를 update하는 프로시저		
			con = getConnection();
			cstmt = con.prepareCall(sql);
			
			for(Lotto l : setResult) {
				cstmt.setInt(1, l.getGrade());
				cstmt.setInt(2, l.getSequence_num());
				cstmt.addBatch();
				cstmt.clearParameters();
			}	
			cstmt.executeBatch();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeAll(con, cstmt, null);	
		}				
	}
		
	
	public ArrayList<Lotto> getResult() {
		
		ArrayList<Lotto> resultList = new ArrayList<Lotto>();
		ArrayList<String> strList = new ArrayList<String>();
		int totalPay = 0;
		
		try {
			sql = "SELECT * FROM V_RESULT";
			con = getConnection();
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			while(rs.next()) {	
				Lotto tempLotto = new Lotto();				
				tempLotto.setGrade(rs.getInt("GRADE"));
				tempLotto.setLocation(rs.getString("LNAME"));
				for(int i = 1; i <= 6; i++) {
					tempLotto.getLotto_Num().add(rs.getInt("NUM" + i));
				}				
				resultList.add(tempLotto);				
			}
			closeAll(null, pstmt, rs);
			
			sql = "SELECT * FROM V_GRADE";				
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				strList.add(rs.getString("GRADE_RESULT"));			
				totalPay += rs.getInt("TOTALPAY");
			}
			
			System.out.println("\n전체 투입 금액 : " + totalPay + "원");
			for(String str : strList) {
				System.out.println(str);
			}
			
			System.out.print("\n당첨 번호 : ");
			for(int i : this.getWinNumber().getLotto_Num()) {
				System.out.print(i + " ");
			}
			System.out.print("\n보너스 번호 : " + this.getBonusNumber());
			System.out.println("\n---------------<당첨 내역>----------------");
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeAll(con, pstmt, rs);
		}
		return resultList;
	}
	
	
	public void getAreaStat() {
		ArrayList<String> areaList = new ArrayList<String>();
		try {
			sql = "SELECT * FROM V_AREA";
			con = getConnection();
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				String str = "";
				for(int i = 1; i <= 3; i++) {
					str += (i == 1)? rs.getString(i)+"\t| " : rs.getString(i)+"\t";
				}
				areaList.add(str);
			}
			if(areaList.size() == 0) {
				System.out.println("당첨 내역이 없습니다.");
				closeAll(con, cstmt, rs); return;
			}
			
			System.out.println("---------------------");
			for(int i = 0; i < areaList.size(); i++) {
				
				if(i >= 1) {
					if(!(areaList.get(i).substring(0,5).equals(areaList.get(i-1).substring(0,5)))) {
						System.out.println("---------------------");
						System.out.println(areaList.get(i));
					}else
						System.out.println(areaList.get(i));
				}else
					System.out.println(areaList.get(i));
			}
			System.out.println("---------------------");
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeAll(con, pstmt, rs);
		}
	}
		
	
	public void clearLog() {
		try {
			sql = "BEGIN DEL_ALL_LOG; END;"; //프로시저 호출문
			con = getConnection();
			pstmt = con.prepareStatement(sql);
			int res = pstmt.executeUpdate();
			
			if(res > 0)
				System.out.println("데이터 초기화 완료.");

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeAll(con, pstmt, null);
		}
	}
	
	
	public int getLastSequence() {
		int lastNum = 0;
		try {
			sql = "SELECT MAX(SEQUENCENUM) MAX FROM LOTTONUM";
			con = getConnection();
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			if(rs.next()) 
				lastNum = rs.getInt("MAX");
			else
				return 0;
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeAll(con, pstmt, rs);
		}
		return lastNum;
	}
}
	   
	   

	   
 


//SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMddhhmmss");
//String str = "TO_DATE('"+ sdf.format(date) + "','YYYYMMDDHH24MISS')";   