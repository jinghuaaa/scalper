/**
 * BBD Service Inc
 * All Rights Reserved @2018
 */
package com.jinghua.service.client;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * @author jinghua
 * @version $Id: HttpClient.java, v0.1 2018/11/13 19:39 jinghua Exp $$
 */
@Service
public class HttpClient {

    @Resource
    private RestTemplate restTemplate;

    public String getHttpResult(String url) throws Exception {
        return restTemplate.getForObject(url, String.class);
    }

}
