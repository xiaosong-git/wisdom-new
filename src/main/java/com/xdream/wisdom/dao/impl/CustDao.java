package com.xdream.wisdom.dao.impl;

import org.springframework.stereotype.Repository;

import com.xdream.kernel.dao.jdbc.JdbcBaseDaoSupport;
import com.xdream.wisdom.dao.ICustDao;
import com.xdream.wisdom.entity.Cust;

@Repository("custDao")
public class CustDao extends JdbcBaseDaoSupport<Cust, Long> implements ICustDao{

}
