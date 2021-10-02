package Lotto.Controller;

public class illegelNumberException extends Exception{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public illegelNumberException() {
		
	}
	
	public illegelNumberException(String msg){
		super(msg);
	}
	
	public void payException(int count) throws illegelNumberException  {
		
		if(count == 0) {
			throw new illegelNumberException("구매 수량을 0보다 크게 입력해주세요.^^");
		}
		
	}
	
	public void placeException(int place) throws illegelNumberException {
		
		if(!(place >= 1 && place <= 4)) {
			throw new illegelNumberException("지점에 해당하는 숫자만 입력해주세요.^^");
		}

	}

}
