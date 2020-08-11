package com.xdream.wisdom.dao.impl;

import org.springframework.stereotype.Repository;

import com.xdream.kernel.dao.jdbc.JdbcBaseDaoSupport;
import com.xdream.wisdom.dao.IChannelSerDao;
import com.xdream.wisdom.entity.ChannelSer;
@Repository("channelSerDao")
public class ChannelSerDao extends JdbcBaseDaoSupport<ChannelSer, Long> implements IChannelSerDao{

}
