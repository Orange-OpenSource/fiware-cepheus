/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.ngsi.model;

import com.orange.espr4fastdata.Application;
import com.orange.ngsi.model.CodeEnum;
import com.orange.ngsi.model.StatusCode;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by pborscia on 08/07/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class StatusCodeTest {


    @Test
    public void createStatusCode200(){
        StatusCode statusCode = new StatusCode(CodeEnum.CODE_200);
        Assert.assertEquals(CodeEnum.CODE_200.getLabel(),statusCode.getCode());
        Assert.assertEquals(CodeEnum.CODE_200.getShortPhrase(),statusCode.getReasonPhrase());
        Assert.assertEquals(CodeEnum.CODE_200.getLongPhrase(),statusCode.getDetail());
    }

    @Test
    public void createStatusCode400(){
        StatusCode statusCode = new StatusCode(CodeEnum.CODE_400);
        Assert.assertEquals(CodeEnum.CODE_400.getLabel(),statusCode.getCode());
        Assert.assertEquals(CodeEnum.CODE_400.getShortPhrase(),statusCode.getReasonPhrase());
        Assert.assertEquals(CodeEnum.CODE_400.getLongPhrase(),statusCode.getDetail());
    }

    @Test
    public void createStatusCode403(){
        StatusCode statusCode = new StatusCode(CodeEnum.CODE_403);
        Assert.assertEquals(CodeEnum.CODE_403.getLabel(),statusCode.getCode());
        Assert.assertEquals(CodeEnum.CODE_403.getShortPhrase(),statusCode.getReasonPhrase());
        Assert.assertEquals(CodeEnum.CODE_403.getLongPhrase(),statusCode.getDetail());
    }

    @Test
    public void createStatusCode404(){
        StatusCode statusCode = new StatusCode(CodeEnum.CODE_404,"temp");
        Assert.assertEquals(CodeEnum.CODE_404.getLabel(),statusCode.getCode());
        Assert.assertEquals(CodeEnum.CODE_404.getShortPhrase(),statusCode.getReasonPhrase());
        Assert.assertTrue(statusCode.getDetail().contains("temp"));
    }

    @Test
    public void createStatusCode470(){
        StatusCode statusCode = new StatusCode(CodeEnum.CODE_470,"124578");
        Assert.assertEquals(CodeEnum.CODE_470.getLabel(),statusCode.getCode());
        Assert.assertEquals(CodeEnum.CODE_470.getShortPhrase(),statusCode.getReasonPhrase());
        Assert.assertTrue(statusCode.getDetail().contains("124578"));
    }

    @Test
    public void createStatusCode471(){
        StatusCode statusCode = new StatusCode(CodeEnum.CODE_471, "updateAction", "UpdateAction");
        Assert.assertEquals(CodeEnum.CODE_471.getLabel(),statusCode.getCode());
        Assert.assertEquals(CodeEnum.CODE_471.getShortPhrase(),statusCode.getReasonPhrase());
        Assert.assertTrue(statusCode.getDetail().contains("updateAction"));
        Assert.assertTrue(statusCode.getDetail().contains("UpdateAction"));
    }

    @Test
    public void createStatusCode472(){
        StatusCode statusCode = new StatusCode(CodeEnum.CODE_472, "updateAction");
        Assert.assertEquals(CodeEnum.CODE_472.getLabel(),statusCode.getCode());
        Assert.assertEquals(CodeEnum.CODE_472.getShortPhrase(),statusCode.getReasonPhrase());
        Assert.assertTrue(statusCode.getDetail().contains("updateAction"));

    }

    @Test
    public void createStatusCode473(){
        StatusCode statusCode = new StatusCode(CodeEnum.CODE_473);
        Assert.assertEquals(CodeEnum.CODE_473.getLabel(),statusCode.getCode());
        Assert.assertEquals(CodeEnum.CODE_473.getShortPhrase(),statusCode.getReasonPhrase());
        Assert.assertEquals(CodeEnum.CODE_473.getLongPhrase(),statusCode.getDetail());

    }

    @Test
    public void createStatusCode480(){
        StatusCode statusCode = new StatusCode(CodeEnum.CODE_480, "S*", "S1");
        Assert.assertEquals(CodeEnum.CODE_480.getLabel(),statusCode.getCode());
        Assert.assertEquals(CodeEnum.CODE_480.getShortPhrase(),statusCode.getReasonPhrase());
        Assert.assertTrue(statusCode.getDetail().contains("S*"));
        Assert.assertTrue(statusCode.getDetail().contains("S1"));
    }

    @Test
    public void createStatusCode481(){
        StatusCode statusCode = new StatusCode(CodeEnum.CODE_481, "Temperature");
        Assert.assertEquals(CodeEnum.CODE_481.getLabel(),statusCode.getCode());
        Assert.assertEquals(CodeEnum.CODE_481.getShortPhrase(),statusCode.getReasonPhrase());
        Assert.assertTrue(statusCode.getDetail().contains("Temperature"));

    }

    @Test
    public void createStatusCode482(){
        StatusCode statusCode = new StatusCode(CodeEnum.CODE_482);
        Assert.assertEquals(CodeEnum.CODE_482.getLabel(),statusCode.getCode());
        Assert.assertEquals(CodeEnum.CODE_482.getShortPhrase(),statusCode.getReasonPhrase());
        Assert.assertEquals(CodeEnum.CODE_482.getLongPhrase(),statusCode.getDetail());

    }

    @Test
    public void createStatusCode500(){
        StatusCode statusCode = new StatusCode(CodeEnum.CODE_500);
        Assert.assertEquals(CodeEnum.CODE_500.getLabel(),statusCode.getCode());
        Assert.assertEquals(CodeEnum.CODE_500.getShortPhrase(),statusCode.getReasonPhrase());
        Assert.assertEquals(CodeEnum.CODE_500.getLongPhrase(),statusCode.getDetail());

    }


}
