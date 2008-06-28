package com.hyperic.hq.hqapi1.test;

import junit.framework.TestCase;
import com.hyperic.hq.hqapi1.HQApi;

public class HQApiTestBase  extends TestCase {

    private static final String  HOST        = "localhost";
    private static final int     PORT        = 7080;
    private static final boolean IS_SECURE   = false;
    private static final String  USER        = "hqadmin";
    private static final String  PASSWORD    = "hqadmin";

    public HQApiTestBase(String name) {
        super(name);
    }

    HQApi getApi() {
        return new HQApi(HOST, PORT, IS_SECURE, USER, PASSWORD);
    }

    HQApi getApi(String user, String password) {
        return new HQApi(HOST, PORT, IS_SECURE, user, password);
    }
}
