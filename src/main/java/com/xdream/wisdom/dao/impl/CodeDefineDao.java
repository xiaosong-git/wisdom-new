package com.xdream.wisdom.dao.impl;

import org.springframework.stereotype.Repository;

import com.xdream.kernel.dao.jdbc.JdbcBaseDaoSupport;
import com.xdream.wisdom.dao.ICodeDefineDao;
import com.xdream.wisdom.entity.CodeDefine;
@Repository("codeDefineDao")
public class CodeDefineDao extends JdbcBaseDaoSupport<CodeDefine, Long> implements ICodeDefineDao {

}
