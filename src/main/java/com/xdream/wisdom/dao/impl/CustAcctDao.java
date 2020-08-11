package com.xdream.wisdom.dao.impl;

import org.springframework.stereotype.Repository;

import com.xdream.kernel.dao.jdbc.JdbcBaseDaoSupport;
import com.xdream.wisdom.dao.ICustAcctDao;
import com.xdream.wisdom.entity.CustAcct;
@Repository("custAcctDao")
public class CustAcctDao extends JdbcBaseDaoSupport<CustAcct, String> implements ICustAcctDao{

}
