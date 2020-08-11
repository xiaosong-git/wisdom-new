package com.xdream.wisdom.dao.impl;


import org.springframework.stereotype.Repository;

import com.xdream.kernel.dao.jdbc.JdbcBaseDaoSupport;
import com.xdream.wisdom.dao.ITranslsDao;
import com.xdream.wisdom.entity.TransLs;
@Repository("translsDao")
public class TranslsDao extends JdbcBaseDaoSupport<TransLs, Long> implements ITranslsDao {

}
