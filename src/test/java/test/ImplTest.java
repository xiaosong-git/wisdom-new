package test;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONObject;
import com.xdream.wisdom.util.SSLClient;
import com.xdream.wisdom.util.SerPackageUtil;

public class ImplTest extends Thread{
    String name="";
	public ImplTest(String string) {
		// TODO Auto-generated constructor stub
		name=string;
	}

	public void run() {
		// TODO Auto-generated method stu
		try {
			HttpClient httpClient = new SSLClient();
			HttpPost postMethod = new HttpPost("http://localhost:8080/wisdom/entrance/pub");
//			http://hwkapp.com/api/v1/deal
//			HttpPost postMethod = new HttpPost("http://hwkapp.com/api/v1/deal");
			JSONObject jsonObject=new JSONObject();
			String s="{\"custid\":\"1000000001\",\"txcode\":\"tx00001\",\"productcode\":\"000001\",\"serialno\":\"1\",\"mac\":\"D2B20033B3D33A4EDA1FE94D2BBB255D\",\"name\":\"黄文坤\",\"mobile\":\"15880485249\", \"idNo\":\"350123199705194193\", \"timestamp\":\"1555506038\"}";
			StringEntity entityStr= new StringEntity(s,HTTP.UTF_8);
			entityStr.setContentType("application/x-www-form-urlencoded");
			postMethod.setEntity(entityStr);
			HttpResponse resp = httpClient.execute(postMethod);
			String str = EntityUtils.toString(resp.getEntity(), HTTP.UTF_8);
			System.out.println(str);
		}catch(Exception e) {
			System.out.println("异常"+e.getMessage());
		}
	}
}
