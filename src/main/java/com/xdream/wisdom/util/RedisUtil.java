package com.xdream.wisdom.util;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import redis.clients.jedis.BinaryClient;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import com.xdream.kernel.DreamConf;

public final class RedisUtil {

    //Redis服务器IP
   private static String    ADDR           =  DreamConf.getPropertie("RedisAddr");
  //   private static String    ADDR           = "127.0.0.1";
    //Redis的端口号
    private static int       PORT           = 6379;
    //访问密码
    private static String    AUTH           = DreamConf.getPropertie("RedisAuth");
 // private static String    AUTH           = "wgm";
    //可用连接实例的最大数目，默认值为8；
    //如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
    private static int       MAX_TOTAL      = 1024;

    //控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8。
    private static int       MAX_IDLE       = 200;

    //等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；
    private static int       MAX_WAIT       = 10000;

    private static int       TIMEOUT        = 10000;

    //在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
    private static boolean   TEST_ON_BORROW = true;

    private static JedisPool jedisPool      = null;

    /**
     * 初始化Redis连接池
     */
    static {
        try {

            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(MAX_TOTAL);
            config.setMaxIdle(MAX_IDLE);
            config.setMaxWaitMillis(MAX_WAIT);
            config.setTestOnBorrow(TEST_ON_BORROW);
//            System.out.println("端口："+PORT);
//            System.out.println("地址："+ADDR);
//            System.out.println("密码："+AUTH);
            jedisPool = new JedisPool(config, ADDR, PORT, TIMEOUT, AUTH);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取Jedis实例
     * @return
     */
    public synchronized static Jedis getJedis(Integer dbNum) {
        try {
            if (jedisPool != null) {
                Jedis resource = jedisPool.getResource();
                resource.select(dbNum);
                return resource;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 释放jedis资源
     * @param jedis
     */
    public static void returnResource(final Jedis jedis) {
        if (jedis != null) {
            jedisPool.returnResource(jedis);
        }
    }

    /**
     * 覆盖key所对应的值(覆盖，key不存在则为添加)
     * 成功返回ok   
     * @Author linyb
     * @Date 2017/2/23 14:06
     *@param key 
     *@param value  存储值
     *@param dbNum 几号库
     *@param expire 数据的有效时间
     */
    public static String setStr(String key, String value,Integer dbNum, Integer expire) {
        Jedis jedis = null;
        try {
            jedis = getJedis(dbNum);
            return  jedis.set(key, value);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
        	if(expire != null){
        		jedis.expire(key,expire);
        	}
            returnResource(jedis);
        }
    }

    /**
     * 保存对象
     * @Author linyb
     * @Date 2017/2/28 14:44
     *
     */
    public static String setObject(byte[] key, Object object,Integer expire,Integer dbNum) {
        Jedis jedis = null;
        try {
            jedis = getJedis(dbNum);
            byte[] serialize = SerializeUtil.serialize(object);
            return  jedis.set(key, serialize);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
        	
        	if(expire != null){
        		jedis.expire(key,expire);
        	}
            returnResource(jedis);
        }
    }

    /**
     * 根据key获取对应的value
     * @Author linyb
     * @Date 2017/2/23 14:04
     *
     */
    public static String getStrVal(String key,Integer dbNum) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        Jedis jedis = null;
        try {
            jedis = getJedis(dbNum);
            return  jedis.get(key);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            returnResource(jedis);
        }
    }

    /**
     * 根据key获取对象
     * @Author linyb
     * @Date 2017/2/28 14:48
     *
     */
    public static Object getObject(byte[] key,Integer dbNum) {
        Jedis jedis = null;
        try {
            jedis = getJedis(dbNum);
            byte[] objByte = jedis.get(key);
            if(objByte == null){
                return null;
            }
            return SerializeUtil.unserialize(objByte);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            returnResource(jedis);
        }
    }

    /**
     * 是否存在key对应的值
     * @Author linyb
     * @Date 2017/2/23 14:21
     *
     */
    public static boolean exists(String key,Integer dbNum) {
        Jedis jedis = null;
        try {
            jedis = getJedis(dbNum);
            return jedis.exists(key);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            returnResource(jedis);
        }
    }


    /**
     * 删除指定的key,也可以传入一个包含key的数组
     * @param keys 一个key  也可以使 string 数组
     * @return 返回删除成功的个数
     */
    public static Long del(Integer dbNum,String... keys) {
        Jedis jedis = null;
        try {
            jedis = getJedis(dbNum);
            return jedis.del(keys);
        } catch (Exception e) {
            e.printStackTrace();
            return 0l;
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * 通过key向指定的value值追加值
     * @param key
     * @param str
     * @return 成功返回 添加后value的长度 失败 返回 添加的 value 的长度  异常返回0L
     */
    public static Long append(String key, String str,Integer dbNum) {
        Jedis jedis = null;
        try {
            jedis = getJedis(dbNum);
            return jedis.append(key, str);
        } catch (Exception e) {
            e.printStackTrace();
            return 0l;
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * 设置key value,如果key已经存在则返回0
     * @param key
     * @param value
     * @return 成功返回1 如果存在 和 发生异常 返回 0
     */
    public static Long setNotExist(String key, String value,Integer dbNum) {
        Jedis jedis = null;
        try {
            jedis = getJedis(dbNum);
            return jedis.setnx(key, value);
        } catch (Exception e) {
            e.printStackTrace();
            return 0l;
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * 设置key value并制定这个键值的有效期
     * @param key
     * @param value
     * @param seconds 单位:秒
     * @return 成功返回OK 失败和异常返回null
     */
    public static String setex(String key, String value, int seconds,Integer dbNum) {

        Jedis jedis = null;
        try {
            jedis = getJedis(dbNum);
            return jedis.setex(key, seconds, value);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * 通过key 和offset 从指定的位置开始将原先value替换
     * 下标从0开始,offset表示从offset下标开始替换
     * 如果替换的字符串长度过小则会这样
     * <p>example:</p>
     * <p>value : bigsea@zto.cn</p>
     * <p>str : abc </p>
     * <P>从下标7开始替换  则结果为</p>
     * <p>RES : bigsea.abc.cn</p>
     * @param key
     * @param str
     * @param offset 下标位置
     * @return 返回替换后  value 的长度
     */
    public static Long setrange(String key, String str, int offset,Integer dbNum) {
        Jedis jedis = null;
        try {
            jedis = getJedis(dbNum);
            return jedis.setrange(key, offset, str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * 通过批量的key获取批量的value
     * @param keys string数组 也可以是一个key
     * @return 成功返回value的集合, 失败返回null的集合 ,异常返回空
     */
    public static List<String> batchGet(Integer dbNum,String... keys) {

        Jedis jedis = null;
        try {
            jedis = getJedis(dbNum);
            return jedis.mget(keys);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            returnResource(jedis);
        }

    }

    /**
     * 批量的设置key:value,可以一个
     * <p>example:</p>
     * <p>  obj.mset(new String[]{"key2","value1","key2","value2"})</p>
     * @param keysvalues
     * @return 成功返回OK 失败 异常 返回 null
     *
     */
    public static String batchSet(Integer dbNum,String... keysvalues) {
        Jedis jedis = null;
        try {
            jedis = getJedis(dbNum);
            return jedis.mset(keysvalues);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            returnResource(jedis);
        }

    }

    /**
     * 批量的设置key:value,可以一个,如果key已经存在则会失败,操作会回滚
     * <p>example:</p>
     * <p>  obj.msetnx(new String[]{"key2","value1","key2","value2"})</p>
     * @param keysvalues
     * @return 成功返回1 失败返回0
     */
    public static Long batchSetNotExist(Integer dbNum,String... keysvalues) {

        Jedis jedis = null;
        try {
            jedis = getJedis(dbNum);
            return jedis.msetnx(keysvalues);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            returnResource(jedis);
        }

    }

    /**
     * 通过key 对value进行加值+1操作,当value不是int类型时会返回错误,当key不存在是则value为1
     * @param key
     * @return 加值后的结果
     */
    public static Long incr(String key,Integer dbNum, Integer expire) {

        Jedis jedis = null;
        try {
            jedis = getJedis(dbNum);
            return jedis.incr(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            jedis.expire(key,expire);
            returnResource(jedis);
        }

    }

    /**
     * 通过key给指定的value加值,如果key不存在,则这是value为该值
     * @param key
     * @param integer
     * @return
     */
    public static Long incrBy(String key, Long integer,Integer dbNum) {

        Jedis jedis = null;
        try {
            jedis = getJedis(dbNum);
            return jedis.incrBy(key, integer);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            returnResource(jedis);
        }

    }

    /**
     * 对key的值做减减操作,如果key不存在,则设置key为-1
     * @param key
     * @return
     */
    public static Long decr(String key,Integer dbNum) {

        Jedis jedis = null;
        try {
            jedis = getJedis(dbNum);
            return jedis.decr(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            returnResource(jedis);
        }

    }

    /**
     * 减去指定的值
     * @param key
     * @param integer
     * @return
     */
    public static Long decrBy(String key, Long integer,Integer dbNum) {

        Jedis jedis = null;
        try {
            jedis = getJedis(dbNum);
            return jedis.decrBy(key, integer);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * 通过key获取value值的长度
     * @param key
     * @return 失败返回null
     */
    public static Long valLength(String key,Integer dbNum) {

        Jedis jedis = null;
        try {
            jedis = getJedis(dbNum);
            return jedis.strlen(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            returnResource(jedis);
        }

    }

    /**
     * 通过key给field设置指定的值,如果key不存在,则先创建
     * @param key
     * @param field 字段
     * @param value
     * @return 如果存在返回0 异常返回null
     */
    public static Long setFieldVal(String key, String field, String value,Integer dbNum) {
        Jedis jedis = null;
        try {
            jedis = getJedis(dbNum);
            return jedis.hset(key, field, value);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            returnResource(jedis);
        }

    }

    /**
     * 通过key给field设置指定的值,如果key不存在则先创建,如果field已经存在,返回0
     * @param key
     * @param field
     * @param value
     * @return
     */
    public static Long setFieldValNotExist(String key, String field, String value,Integer dbNum) {
        Jedis jedis = null;
        try {
            jedis = getJedis(dbNum);
            return jedis.hsetnx(key, field, value);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            returnResource(jedis);
        }

    }

    /**
     * 通过key同时设置 hash的多个field
     * @param key
     * @param hash
     * @return 返回OK 异常返回null
     */
    public static String setFieldMap(String key, Map<String, String> hash,Integer dbNum) {
        Jedis jedis = null;
        try {
            jedis = getJedis(dbNum);
            return jedis.hmset(key, hash);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * 通过key 和 field 获取指定的 value
     * @param key
     * @param field
     * @return 没有返回null
     */
    public static String getByKeyAndField(String key, String field,Integer dbNum) {

        Jedis jedis = null;
        try {
            jedis = getJedis(dbNum);
            return jedis.hget(key, field);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            returnResource(jedis);
        }

    }

    /**
     * <p>通过key给指定的field的value加上给定的值</p>
     * @param key
     * @param field
     * @param value
     * @return
     */
    public static Long fieldValIncr(String key, String field, Long value,Integer dbNum) {
        Jedis jedis = null;
        try {
            jedis = getJedis(dbNum);
            return jedis.hincrBy(key, field, value);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            returnResource(jedis);
        }

    }

    /**
     * 通过key和field判断是否有指定的value存在
     * @param key
     * @param field
     * @return
     */
    public static Boolean existsByKeyAndField(String key, String field,Integer dbNum) {
        Jedis jedis = null;
        try {
            jedis = getJedis(dbNum);
            return jedis.hexists(key, field);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * 通过key返回field的数量
     * @param key
     * @return
     */
    public static Long fieldCountByKey(String key,Integer dbNum) {
        Jedis jedis = null;
        try {
            jedis = getJedis(dbNum);
            return jedis.hlen(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            returnResource(jedis);
        }

    }

    /**
     * 通过key 删除指定的 field
     * @param key
     * @param fields 可以是 一个 field 也可以是 一个数组
     * @return
     */
    public Long delByKeyAndField(String key,Integer dbNum, String... fields) {

        Jedis jedis = null;
        try {
            jedis = getJedis(dbNum);
            return jedis.hdel(key, fields);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            returnResource(jedis);
        }

    }

    /**
     * 通过key返回所有的field
     * @param key
     * @return
     */
    public Set<String> getFieldsNameByKey(String key,Integer dbNum) {
        Jedis jedis = null;
        try {
            jedis = getJedis(dbNum);
            return jedis.hkeys(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            returnResource(jedis);
        }

    }

    /**
     * 通过key返回所有和key有关的value
     * @param key
     * @return
     */
    public static List<String> getAllValByKey(String key,Integer dbNum) {
        Jedis jedis = null;
        try {
            jedis = getJedis(dbNum);
            return jedis.hvals(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            returnResource(jedis);
        }

    }

    /**
     * 通过key获取所有的field和value
     * @param key
     * @return
     */
    public static Map<String, String> getAllFieldAndValueByKey(String key,Integer dbNum) {

        Jedis jedis = null;
        try {
            jedis = getJedis(dbNum);
            return jedis.hgetAll(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            returnResource(jedis);
        }

    }

    /**
     * 通过key向list头部添加字符串
     * @param key
     * @param strs 可以使一个string 也可以使string数组
     * @return 返回list的value个数
     */
    public static Long addToListHead(String key,Integer dbNum, String... strs) {
        Jedis jedis = null;
        try {
            jedis = getJedis(dbNum);
            return jedis.lpush(key, strs);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            returnResource(jedis);
        }

    }

    /**
     * 通过key向list尾部添加字符串
     * @param key
     * @param strs 可以使一个string 也可以使string数组
     * @return 返回list的value个数
     */
    public static Long addToListFooter(String key,Integer dbNum, String... strs) {
        Jedis jedis = null;
        try {
            jedis = getJedis(dbNum);
            return jedis.rpush(key, strs);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            returnResource(jedis);
        }

    }

    /**
     * 通过key在list指定的位置之前或者之后 添加字符串元素
     * @param key
     * @param where LIST_POSITION枚举类型
     * @param pivot list里面的value
     * @param value 添加的value
     * @return
     */
    public static Long insertList(String key, BinaryClient.LIST_POSITION where, String pivot,
                                  String value,Integer dbNum) {
        Jedis jedis = null;
        try {
            jedis = getJedis(dbNum);
            return jedis.linsert(key, where, pivot, value);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            returnResource(jedis);
        }

    }

    /**
     * 通过key设置list指定下标位置的value
     * 如果下标超过list里面value的个数则报错
     * @param key
     * @param index 从0开始
     * @param value
     * @return 成功返回OK
     */
    public static String insertListForIndex(String key, Long index, String value,Integer dbNum) {
        Jedis jedis = null;
        try {
            jedis = getJedis(dbNum);
            return jedis.lset(key, index, value);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            returnResource(jedis);
        }

    }

    /**
     * 通过key从对应的list中删除指定的count个 和 value相同的元素
     * @param key
     * @param count 当count为0时删除全部
     * @param value
     * @return 返回被删除的个数
     */
    public static Long delListByCountAndValue(String key, long count, String value,Integer dbNum) {
        Jedis jedis = null;
        try {
            jedis = getJedis(dbNum);
            return jedis.lrem(key, count, value);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            returnResource(jedis);
        }

    }

    /**
     * 通过key保留list中从strat下标开始到end下标结束的value值
     * @param key
     * @param start
     * @param end
     * @return 成功返回OK
     */
    public static String keepRegion(String key, long start, long end,Integer dbNum) {
        Jedis jedis = null;
        try {
            jedis = getJedis(dbNum);
            return jedis.ltrim(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * 通过key从list的头部删除一个value,并返回该value
     * @param key
     * @return
     */
    public static String delListHead(String key,Integer dbNum) {
        Jedis jedis = null;
        try {
            jedis = getJedis(dbNum);
            return jedis.lpop(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * 通过key从list尾部删除一个value,并返回该元素
     * @param key
     * @return
     */
    public static String delListFooter(String key,Integer dbNum) {
        Jedis jedis = null;
        try {
            jedis = getJedis(dbNum);
            return jedis.rpop(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * 通过key从一个list的尾部删除一个value并添加到另一个list的头部,并返回该value
     * 如果第一个list为空或者不存在则返回null
     * @param srckey
     * @param dstkey
     * @return
     */
    public static String rpoplpush(String srckey, String dstkey,Integer dbNum) {
        Jedis jedis = null;
        try {
            jedis = getJedis(dbNum);
            return jedis.rpoplpush(srckey, dstkey);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            returnResource(jedis);
        }

    }

    /**
     * 通过key获取list中指定下标位置的value
     * @param key
     * @param index
     * @return 如果没有返回null
     */
    public static String getListValByIndex(String key, long index,Integer dbNum) {
        Jedis jedis = null;
        try {
            jedis = getJedis(dbNum);
            return jedis.lindex(key, index);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            returnResource(jedis);
        }

    }

    /**
     * 通过key返回list的长度
     * @param key
     * @return
     */
    public static Long listLength(String key,Integer dbNum) {
        Jedis jedis = null;
        try {
            jedis = getJedis(dbNum);
            return jedis.llen(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * 通过key获取list指定下标位置的value
     * 如果start 为 0 end 为 -1 则返回全部的list中的value
     * @param key
     * @param start
     * @param end
     * @return
     */
    public static List<String> getListValsByRegion(String key, long start, long end,Integer dbNum) {
        Jedis jedis = null;
        try {
            jedis = getJedis(dbNum);
            return jedis.lrange(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            returnResource(jedis);
        }

    }

    /**
     * 通过key向指定的set中添加value
     * @param key
     * @param members 可以是一个String 也可以是一个String数组
     * @return 添加成功的个数
     */
    public static Long addSet(String key,Integer dbNum, String... members) {
        Jedis jedis = null;
        try {
            jedis = getJedis(dbNum);
            return jedis.sadd(key, members);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * 通过key删除set中对应的value值
     * @param key
     * @param members 可以是一个String 也可以是一个String数组
     * @return 删除的个数
     */
    public static Long delSet(String key,Integer dbNum, String... members) {
        Jedis jedis = null;
        try {
            jedis = getJedis(dbNum);
            return jedis.srem(key, members);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * 通过key随机删除一个set中的value并返回该值
     * @param key
     * @return
     */
    public static String delSetRandom(String key,Integer dbNum) {
        Jedis jedis = null;
        try {
            jedis = getJedis(dbNum);
            return jedis.spop(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * 通过key获取set中的差集
     * <p>以第一个set为标准</p>
     * @param keys 可以使一个string 则返回set中所有的value 也可以是string数组
     * @return
     */
    public static Set<String> setDiff(Integer dbNum,String... keys) {
        Jedis jedis = null;
        try {
            jedis = getJedis(dbNum);
            return jedis.sdiff(keys);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            returnResource(jedis);
        }

    }

    /**
     * 通过key获取set中的差集并存入到另一个key中
     * <p>以第一个set为标准</p>
     * @param dstkey 差集存入的key
     * @param keys 可以使一个string 则返回set中所有的value 也可以是string数组
     * @return
     */
    public static Long setdiffstore(String dstkey,Integer dbNum, String... keys) {
        Jedis jedis = null;
        try {
            jedis = getJedis(dbNum);
            return jedis.sdiffstore(dstkey, keys);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * <p>通过key获取指定set中的交集</p>
     * @param keys 可以使一个string 也可以是一个string数组
     * @return
     */
    public static Set<String> setInter(Integer dbNum,String... keys) {
        Jedis jedis = null;
        try {
            jedis = getJedis(dbNum);
            return jedis.sinter(keys);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * <p>通过key获取指定set中的交集 并将结果存入新的set中</p>
     * @param dstkey
     * @param keys 可以使一个string 也可以是一个string数组
     * @return
     */
    public static Long sInterStore(String dstkey,Integer dbNum, String... keys) {
        Jedis jedis = null;
        try {
            jedis = getJedis(dbNum);
            return jedis.sinterstore(dstkey, keys);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * <p>通过key返回所有set的并集</p>
     * @param keys 可以使一个string 也可以是一个string数组
     * @return
     */
    public static Set<String> setUnion(Integer dbNum,String... keys) {

        Jedis jedis = null;
        try {
            jedis = getJedis(dbNum);
            return jedis.sunion(keys);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * 通过key返回所有set的并集,并存入到新的set中
     * @param dstkey
     * @param keys 可以使一个string 也可以是一个string数组
     * @return
     */
    public static Long setUnionStore(String dstkey,Integer dbNum, String... keys) {

        Jedis jedis = null;
        try {
            jedis = getJedis(dbNum);
            return jedis.sunionstore(dstkey, keys);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * 通过key将set中的value移除并添加到第二个set中
     * @param srckey 需要移除的
     * @param dstkey 添加的
     * @param member set中的value
     * @return
     */
    public static Long setMove(String srckey, String dstkey, String member,Integer dbNum) {
        Jedis jedis = null;
        try {
            jedis = getJedis(dbNum);
            return jedis.smove(srckey, dstkey, member);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            returnResource(jedis);
        }

    }

    /**
     * 通过key获取set中value的个数
     * @param key
     * @return
     */
    public static Long setLength(String key,Integer dbNum) {
        Jedis jedis = null;
        try {
            jedis = getJedis(dbNum);
            return jedis.scard(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * 通过key判断value是否是set中的元素
     * @param key
     * @param value
     * @return
     */
    public static Boolean setIsExistVal(String key, String value,Integer dbNum) {
        Jedis jedis = null;
        try {
            jedis = getJedis(dbNum);
            return jedis.sismember(key, value);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * 通过key获取set中随机的value,不删除元素
     * @param key
     * @return
     */
    public static String setRandomVal(String key,Integer dbNum) {
        Jedis jedis = null;
        try {
            jedis = getJedis(dbNum);
            return jedis.srandmember(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * 通过key获取set中所有的value
     * @param key
     * @return
     */
    public static Set<String> setAllValue(String key,Integer dbNum) {
        Jedis jedis = null;
        try {
            jedis = getJedis(dbNum);
            return jedis.smembers(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * 通过key向zset中添加value,score,其中score就是用来排序的
     * 如果该value已经存在则根据score更新元素
     * @param key
     * @param scoreMembers
     * @return
     */
    public static Long zsetAdd(String key, Map<String, Double> scoreMembers,Integer dbNum) {
        Jedis jedis = null;
        try {
            jedis = getJedis(dbNum);
            return jedis.zadd(key, scoreMembers);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            returnResource(jedis);
        }

    }

    /**
     * 通过key向set中添加value,score,其中score就是用来排序的
     * 如果该value已经存在则根据score更新元素
     * @param key
     * @param score
     * @param member
     * @return
     */
    public static Long zsetAdd(String key, double score, String member,Integer dbNum) {
        Jedis jedis = null;
        try {
            jedis = getJedis(dbNum);
            return jedis.zadd(key, score, member);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            returnResource(jedis);
        }

    }

    /**
     * 通过key删除在zset中指定的value
     * @param key
     * @param members 可以使一个string 也可以是一个string数组
     * @return
     */
    public static Long delZset(String key,Integer dbNum, String... members) {

        Jedis jedis = null;
        try {
            jedis = getJedis(dbNum);
            return jedis.zrem(key, members);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            returnResource(jedis);
        }

    }

    /**
     * 通过key增加该zset中value的score的值
     * @param key
     * @param score
     * @param member
     * @return
     */
    public static Double zsetIncrBy(String key, double score, String member,Integer dbNum) {

        Jedis jedis = null;
        try {
            jedis = getJedis(dbNum);
            return jedis.zincrby(key, score, member);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * 通过key返回zset中value的排名
     * 下标从小到大排序
     * @param key
     * @param member
     * @return
     */
    public static Long zsetRank(String key, String member,Integer dbNum) {
        Jedis jedis = null;
        try {
            jedis = getJedis(dbNum);
            return jedis.zrank(key, member);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * 通过key返回zset中value的排名
     * 下标从大到小排序
     * @param key
     * @param member
     * @return
     */
    public static Long zsetValRank(String key, String member,Integer dbNum) {
        Jedis jedis = null;
        try {
            jedis = getJedis(dbNum);
            return jedis.zrevrank(key, member);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * 通过key将获取score从start到end中zset的value
     * socre从大到小排序
     * 当start为0 end为-1时返回全部
     * @param key
     * @param start
     * @param end
     * @return
     */
    public static Set<String> getZsetByRegion(String key, long start, long end,Integer dbNum) {
        Jedis jedis = null;
        try {
            jedis = getJedis(dbNum);
            return jedis.zrevrange(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            returnResource(jedis);
        }

    }

    /**
     * 通过key返回指定score内zset中的value
     * @param key
     * @param max
     * @param min
     * @return
     */
    public static Set<String> getZsetByMaxMinRegion(String key, String max, String min,Integer dbNum) {

        Jedis jedis = null;
        try {
            jedis = getJedis(dbNum);
            return jedis.zrevrangeByScore(key, max, min);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            returnResource(jedis);
        }

    }

    /**
     * 通过key返回指定score内zset中的value
     * @param key
     * @param max
     * @param min
     * @return
     */
    public static Set<String> getZsetByMaxMinRegion(String key, double max, double min,Integer dbNum) {
        Jedis jedis = null;
        try {
            jedis = getJedis(dbNum);
            return jedis.zrevrangeByScore(key, max, min);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            returnResource(jedis);
        }

    }

    /**
     * 返回指定区间内zset中value的数量
     * @param key
     * @param min
     * @param max
     * @return
     */
    public static Long zsetCountByRegion(String key, String min, String max,Integer dbNum) {
        Jedis jedis = null;
        try {
            jedis = getJedis(dbNum);
            return jedis.zcount(key, min, max);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            returnResource(jedis);
        }

    }

    /**
     * <p>通过key返回zset中的value个数</p>
     * @param key
     * @return
     */
    public static Long zsetCount(String key,Integer dbNum) {
        Jedis jedis = null;
        try {
            jedis = getJedis(dbNum);
            return jedis.zcard(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * <p>通过key获取zset中value的score值</p>
     * @param key
     * @param member
     * @return
     */
    public static Double zsetScore(String key, String member,Integer dbNum) {
        Jedis jedis = null;
        try {
            jedis = getJedis(dbNum);
            return jedis.zscore(key, member);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * 通过key删除给定区间内的元素
     * @param key
     * @param start
     * @param end
     * @return
     */
    public static Long delZsetByRegion(String key, long start, long end,Integer dbNum) {
        Jedis jedis = null;
        try {
            jedis = getJedis(dbNum);
            return jedis.zremrangeByRank(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * 通过key删除指定score内的元素
     * @param key
     * @param start
     * @param end
     * @return
     */
    public static Long delZsetByScore(String key, double start, double end,Integer dbNum) {
        Jedis jedis = null;
        try {
            jedis = getJedis(dbNum);
            return jedis.zremrangeByScore(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            returnResource(jedis);
        }

    }

    /**
     * 返回满足pattern表达式的所有key
     * <p>keys(*)</p>
     * <p>返回所有的key</p>
     * @param pattern
     * @return
     */
    public static Set<String> getKeysByPattern(String pattern,Integer dbNum) {
        Jedis jedis = null;
        try {
            jedis = getJedis(dbNum);
            return jedis.keys(pattern);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * 通过key判断值得类型
     * @param key
     * @return
     */
    public static String keyType(String key,Integer dbNum) {
        Jedis jedis = null;
        try {
            jedis = getJedis(dbNum);
            return jedis.type(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            returnResource(jedis);
        }
    }


	public static void main(String[] args) {
       
		RedisUtil.setStr("1","666", 1, 60);
System.out.println(RedisUtil.getStrVal("1", 1));		
    }
    
}