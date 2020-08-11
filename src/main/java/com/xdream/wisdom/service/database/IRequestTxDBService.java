package com.xdream.wisdom.service.database;

import java.util.List;

import com.xdream.wisdom.entity.ReqTxCodeDep;

public interface IRequestTxDBService {
	
	public List<ReqTxCodeDep> findReqTxDep(String txCode);

}
