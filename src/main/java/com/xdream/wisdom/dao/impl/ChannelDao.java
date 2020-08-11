package com.xdream.wisdom.dao.impl;

import org.springframework.stereotype.Repository;

import com.xdream.kernel.dao.jdbc.JdbcBaseDaoSupport;
import com.xdream.wisdom.dao.IChannelDao;
import com.xdream.wisdom.entity.Channel;
@Repository("channelDao")
public class ChannelDao extends JdbcBaseDaoSupport<Channel, Long> implements IChannelDao{

}
