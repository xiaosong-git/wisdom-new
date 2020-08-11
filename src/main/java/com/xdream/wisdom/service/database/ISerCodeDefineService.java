package com.xdream.wisdom.service.database;

import java.util.List;

import com.xdream.wisdom.entity.CodeDefine;
import com.xdream.wisdom.entity.JSonSerDep;

public interface ISerCodeDefineService {
	public List<CodeDefine> findCodeDefine(String sercode) throws Exception;

}
