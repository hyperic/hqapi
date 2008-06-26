package com.hyperic.hq.hqapi1;

import org.hyperic.hq.hqapi1.jaxb.GetUsersResponse;
import org.hyperic.hq.hqapi1.jaxb.GetUserResponse;

import javax.xml.bind.JAXBException;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;

public class HQApi extends HQConnection {

    private static final Map NO_PARAMS = new HashMap();

    public HQApi(String host, int port, boolean isSecure, String user,
                  String password) {
        super(host, port, isSecure, user, password);
    }

    public GetUserResponse getUser(String name)
        throws IOException, JAXBException
    {
        Map params = new HashMap();
        params.put("name", name);
        return (GetUserResponse)getRequest("/hqu/hqapi1/user/get.hqu",
                                            params, GetUserResponse.class);
    }
    
    public GetUsersResponse getUsers()
        throws IOException, JAXBException
    {
        return (GetUsersResponse)getRequest("/hqu/hqapi1/user/list.hqu",
                                            NO_PARAMS, GetUsersResponse.class);
    }
}
