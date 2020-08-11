package com.xdream.wisdom.dao.impl;

import org.springframework.stereotype.Repository;

import com.xdream.kernel.dao.jdbc.JdbcBaseDaoSupport;
import com.xdream.wisdom.dao.IJsonSerDepDao;
import com.xdream.wisdom.entity.JSonSerDep;
@Repository("jsonSerDepDao")
public class JsonSerDepDao extends JdbcBaseDaoSupport<JSonSerDep, String>implements IJsonSerDepDao {

}
