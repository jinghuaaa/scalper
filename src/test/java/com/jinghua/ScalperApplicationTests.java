package com.jinghua;

import com.jinghua.service.client.HttpClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ScalperApplicationTests {

    @Autowired
    HttpClient httpClient;

    @Test
    public void contextLoads() throws Exception {
        String result = httpClient.getHttpResult("https://www.baidu.com/");
        System.out.println(result);
    }
}
