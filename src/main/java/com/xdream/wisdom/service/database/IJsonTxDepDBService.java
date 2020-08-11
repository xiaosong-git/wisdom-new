package com.xdream.wisdom.service.database;

import java.util.List;

import com.xdream.wisdom.entity.JSonSerDep;

public interface IJsonTxDepDBService {
	public List<JSonSerDep> findJsonSerDep(String sercode) throws Exception;

}
