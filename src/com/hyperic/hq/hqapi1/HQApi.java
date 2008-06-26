package com.hyperic.hq.hqapi1;

import org.hyperic.hq.hqapi1.jaxb.GetUsersResponse;
import org.hyperic.hq.hqapi1.jaxb.GetUserResponse;
import org.hyperic.hq.hqapi1.jaxb.CreateUserResponse;

import javax.xml.bind.JAXBException;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;

/**
 * Public API to the HQApi.
 *
 * TODO: Move methods to subclasses on a per-controller basis?
 */
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

    public CreateUserResponse createUser(String name, String firstName,
                                         String lastName, String password,
                                         String email)
        throws IOException, JAXBException
    {
        Map params = new HashMap();
        params.put("Name", name);
        params.put("FirstName", firstName);
        params.put("LastName", lastName);
        params.put("Password", password);
        params.put("EmailAddress", email);

        return (CreateUserResponse)getRequest("/hqu/hqapi1/user/create.hqu",
                                              params, CreateUserResponse.class);
    }
}
