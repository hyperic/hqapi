package org.hyperic.hq.hqapi1;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.hyperic.hq.hqapi1.types.AddResourceToGroupResponse;
import org.hyperic.hq.hqapi1.types.CreateGroupResponse;
import org.hyperic.hq.hqapi1.types.DeleteGroupResponse;
import org.hyperic.hq.hqapi1.types.GetGroupsResponse;
import org.hyperic.hq.hqapi1.types.Group;
import org.hyperic.hq.hqapi1.types.RemoveResourceFromGroupResponse;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.CreateGroupRequest;
import org.hyperic.hq.hqapi1.types.FindResourcesResponse;

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
     * Create a {@link org.hyperic.hq.hqapi1.types.Group}.<br><b>This API is
     * not yet availabile.  It will return an not implemented error.</b>
     *
     * @param group The group to create.
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if the
     * group was created successfully.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public CreateGroupResponse createGroup(Group group)
        throws IOException
    {
        CreateGroupRequest req = new CreateGroupRequest();
        req.setGroup(group);
        return doPost("group/create.hqu", req, CreateGroupResponse.class);
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
    public DeleteGroupResponse deleteGroup(Integer id)
        throws IOException
    {
        Map<String,String> params = new HashMap<String,String>();
        params.put("id", Integer.toString(id));
        return doGet("group/delete.hqu", params, DeleteGroupResponse.class);
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
    public RemoveResourceFromGroupResponse removeResource(int groupId,
                                                          int resourceId)
        throws IOException
    {
        Map<String,String> params = new HashMap<String,String>();
        params.put("groupId", Integer.toString(groupId));
        params.put("resourceId", Integer.toString(resourceId));
        return doGet("group/removeResource.hqu", params,
                     RemoveResourceFromGroupResponse.class);
    }

    /**
     * List all the Groups in HQ visible by the login.<br><b>This API is
     * not yet availabile.  It will return an not implemented error.</b>
     *
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if 
     * all the groups were successfully retrieved from the server.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public GetGroupsResponse listGroups()
        throws IOException
    {
        return doGet("group/list.hqu", new HashMap<String,String>(),
                     GetGroupsResponse.class);
    }

    /**
     * List all the Resources associated with a {@link Group}.<br><b>This API is
     * not yet availabile.  It will return an not implemented error.</b>
     *
     * @param groupId The {@link org.hyperic.hq.hqapi1.types.Group} id to query.
     *
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if all
     * the resources were successfully retrieved.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public FindResourcesResponse listResources(int groupId)
        throws IOException
    {
        Map<String,String> params = new HashMap<String,String>();
        params.put("groupId", Integer.toString(groupId));
        return doGet("group/listResources.hqu", params,
                     FindResourcesResponse.class);
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
    public AddResourceToGroupResponse addResource(int groupId, int resourceId)
        throws IOException
    {
        Map<String,String> params = new HashMap<String,String>();
        params.put("groupId", Integer.toString(groupId));
        params.put("resourceId", Integer.toString(resourceId));
        return doGet("group/addResource.hqu", params,
                     AddResourceToGroupResponse.class);
    }
}
