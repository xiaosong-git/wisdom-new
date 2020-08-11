package com.xdream.wisdom.dao.impl;

import org.springframework.stereotype.Repository;

import com.xdream.kernel.dao.jdbc.JdbcBaseDaoSupport;
import com.xdream.wisdom.dao.IReqTxCodeDepDao;
import com.xdream.wisdom.entity.ReqTxCodeDep;
@Repository("reqTxCodeDepDao")
public class ReqTxCodeDepDao extends JdbcBaseDaoSupport<ReqTxCodeDep, Long> implements IReqTxCodeDepDao{

}
