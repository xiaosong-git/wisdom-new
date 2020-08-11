package com.xdream.wisdom.service.database;

import java.math.BigDecimal;

public interface ICustAcctDBService {
	
	public int modifyCustAcct(String custacct,BigDecimal amt)throws Exception;

}
