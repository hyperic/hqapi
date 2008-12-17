package org.hyperic.hq.hqapi1;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.hyperic.hq.hqapi1.types.Group;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.CreateGroupRequest;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.types.GroupResponse;
import org.hyperic.hq.hqapi1.types.GroupsResponse;
import org.hyperic.hq.hqapi1.types.ResourcesResponse;

/**
 * The Hyperic HQ Group API.
 * <br><br>
 * This class provides access to the {@link org.hyperic.hq.hqapi1.types.Group}s
 * within the HQ system.  Each of the methods in this class return
 * {@link org.hyperic.hq.hqapi1.types.Response} objects that wrap the result
 * of the method with a
 * {@link org.hyperic.hq.hqapi1.types.ResponseStatus} and a
 * {@link org.hyperic.hq.hqapi1.types.ServiceError} that indicates the error
 * if the response status is {@link org.hyperic.hq.hqapi1.types.ResponseStatus#FAILURE}.
 */
public class GroupApi extends BaseApi {

    GroupApi(HQConnection conn) {
        super(conn);
    }

    /**
     * Get a {@link org.hyperic.hq.hqapi1.types.Group} by name.
     *
     * @param name The group name to search for.
     *
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * the User by the given name is returned via
     * {@link org.hyperic.hq.hqapi1.types.GroupResponse#getGroup()}.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public GroupResponse getGroup(String name)
        throws IOException
    {
        Map<String, String[]> params = new HashMap<String, String[]>();
        params.put("name", new String[] { name });
        return doGet("group/get.hqu", params, GroupResponse.class);
    }

    /**
     * Get a {@link org.hyperic.hq.hqapi1.types.Group} by id.
     *
     * @param id The group id to look up.
     *
     * @return On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS},
     * the User by the given id is returned via
     * {@link org.hyperic.hq.hqapi1.types.GroupResponse#getGroup()}.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public GroupResponse getGroup(int id)
        throws IOException
    {
        Map<String, String[]> params = new HashMap<String, String[]>();
        params.put("id", new String[] { Integer.toString(id) });
        return doGet("group/get.hqu", params, GroupResponse.class);
    }

    /**
     * Create a {@link org.hyperic.hq.hqapi1.types.Group}.<br><b>This API is
     * not yet availabile.  It will return an not implemented error.</b>
     *
     * @param group The group to create.
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if the
     * group was created successfully.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public GroupResponse createGroup(Group group)
        throws IOException
    {
        CreateGroupRequest req = new CreateGroupRequest();
        req.setGroup(group);
        return doPost("group/create.hqu", req, GroupResponse.class);
    }
    
    /**
     * Delete a {@link Group}.<br><b>This API is
     * not yet availabile.  It will return an not implemented error.</b>
     *
     * @param id The {@link org.hyperic.hq.hqapi1.types.Group} id to delete.
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if the
     * group was deleted successfully.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public StatusResponse deleteGroup(int id)
        throws IOException
    {
        Map<String,String[]> params = new HashMap<String,String[]>();
        params.put("id", new String[] { Integer.toString(id) });
        return doGet("group/delete.hqu", params, StatusResponse.class);
    }
    
    /**
     * Delete a {@link Resource} from a {@link Group}.<br><b>This API is
     * not yet availabile.  It will return an not implemented error.</b>
     *
     * @param groupId The {@link Group} id to operate on.
     * @param resourceId The {@link Resource} id to remove.
     *
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if the
     * resource was successfully removed from the group.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public StatusResponse removeResource(int groupId, int resourceId)
        throws IOException
    {
        Map<String,String[]> params = new HashMap<String,String[]>();
        params.put("groupId", new String[] { Integer.toString(groupId) });
        params.put("resourceId", new String[] { Integer.toString(resourceId) });
        return doGet("group/removeResource.hqu", params,
                     StatusResponse.class);
    }

    /**
     * List all {@link org.hyperic.hq.hqapi1.types.Group}s.
     *
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if 
     * all the groups were successfully retrieved from the server.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public GroupsResponse listGroups()
        throws IOException
    {
        return doGet("group/list.hqu", new HashMap<String,String[]>(),
                     GroupsResponse.class);
    }

    /**
     * List all compatible {@link org.hyperic.hq.hqapi1.types.Group}s.  A
     * compatible group is a group where all members of the group have the
     * same {@link org.hyperic.hq.hqapi1.types.ResourcePrototype}.
     *
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if
     * all the groups were successfully retrieved from the server.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public GroupsResponse listCompatibleGroups()
        throws IOException
    {
        Map<String,String[]> params = new HashMap<String,String[]>();
        params.put("compatible", new String[] { Boolean.toString(true) });
        return doGet("group/list.hqu", params, GroupsResponse.class);
    }

    /**
     * List all mixed {@link org.hyperic.hq.hqapi1.types.Group}s.  A
     * mixed group is a group where the members will have different
     * {@link org.hyperic.hq.hqapi1.types.ResourcePrototype}s.
     *
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if
     * all the groups were successfully retrieved from the server.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public GroupsResponse listMixedGroups()
        throws IOException
    {
        Map<String,String[]> params = new HashMap<String,String[]>();
        params.put("compatible", new String[] { Boolean.toString(false) });
        return doGet("group/list.hqu", params, GroupsResponse.class);
    }

    /**
     * List all the Resources associated with a {@link Group}.
     *
     * @param groupId The {@link org.hyperic.hq.hqapi1.types.Group} id to query.
     *
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if all
     * the resources were successfully retrieved.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public ResourcesResponse listResources(int groupId)
        throws IOException
    {
        Map<String,String[]> params = new HashMap<String,String[]>();
        params.put("groupId", new String[] { Integer.toString(groupId) });
        return doGet("group/listResources.hqu", params,
                     ResourcesResponse.class);
    }

    /**
     * Add the specified {@link Resource} to the {@link Group}.<br><b>This API is
     * not yet availabile.  It will return an not implemented error.</b>
     *
     * @param groupId The {@link org.hyperic.hq.hqapi1.types.Group} id to operate on.
     * @param resourceId The {@link org.hyperic.hq.hqapi1.types.Resource} id to add.
     *
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if the
     * resource was successfully added to the group.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public StatusResponse addResource(int groupId, int resourceId)
        throws IOException
    {
        Map<String,String[]> params = new HashMap<String,String[]>();
        params.put("groupId", new String[] { Integer.toString(groupId) });
        params.put("resourceId", new String[] { Integer.toString(resourceId) });
        return doGet("group/addResource.hqu", params,
                     StatusResponse.class);
    }
}
