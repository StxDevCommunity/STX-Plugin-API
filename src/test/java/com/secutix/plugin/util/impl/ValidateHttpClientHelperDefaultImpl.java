package com.secutix.plugin.util.impl;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.secutix.plugin.util.StxPluginHttpResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.HashMap;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

public class ValidateHttpClientHelperDefaultImpl {

    private HttpClientHelperDefaultImpl httpClientHelper;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort());

    @Before
    public void init(){
        httpClientHelper = new HttpClientHelperDefaultImpl();
    }

    @Test
    public void testGetMethod(){
        stubFor(get(urlEqualTo("/query11"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody("<response>Some content</response>")));

        StxPluginHttpResponse res = httpClientHelper.doGet("http://localhost:" + wireMockRule.port()+"/query11", new HashMap<>());
        Assert.assertEquals(200,res.getStatusCode());

        verify(getRequestedFor(urlMatching("/query11")));
    }

    @Test
    public void testPostMethod(){
        stubFor(post(urlEqualTo("/action1"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody("<response>Some content</response>")));

        StxPluginHttpResponse res = httpClientHelper.doPost("http://localhost:" + wireMockRule.port()+"/action1","Anything", new HashMap<>());
        Assert.assertEquals(200,res.getStatusCode());

        verify(postRequestedFor(urlMatching("/action1")).withRequestBody(matching(".*Anything.*")));
    }


}
