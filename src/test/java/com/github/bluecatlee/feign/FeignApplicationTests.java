package com.github.bluecatlee.feign;

import com.github.bluecatlee.feign.bean.FeignResult;
import com.github.bluecatlee.feign.service.SomeService;
import com.github.bluecatlee.feign.service.WeatherService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = FeignApplication.class)
public class FeignApplicationTests {

	@Autowired
	private WeatherService weatherService;

	@Autowired
	private SomeService someService;

	@Test
	public void contextLoads() {
	}

	@Test
	public void testFeign() {
		WeatherService.Weatcher w = weatherService.w("101030100");
		Assert.assertNotNull("weather call fail", w);
		Assert.assertEquals("weather call resp data not consistent","天津市", w.getCityInfo().getCity());
	}

	// @Test
	// public void testSth() {
	// 	FeignResult s1 = someService.mform("s");
	// 	Assert.assertNotNull("self define service call fail", s1);
	// 	FeignResult s2 = someService.mjson("{\"id\":\"1\", \"name\":\"蓝猫\"}");
	// 	Assert.assertNotNull("self define service call fail", s2);
	// }
}
