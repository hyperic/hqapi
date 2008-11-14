package org.hyperic.hq.hqapi1;

import java.io.IOException;

import org.hyperic.hq.hqapi1.types.AddResourceToGroupResponse;
import org.hyperic.hq.hqapi1.types.CreateGroupResponse;
import org.hyperic.hq.hqapi1.types.DeleteGroupResponse;
import org.hyperic.hq.hqapi1.types.GetGroupsResponse;
import org.hyperic.hq.hqapi1.types.GetResourcesInGroupResponse;
import org.hyperic.hq.hqapi1.types.Group;
import org.hyperic.hq.hqapi1.types.RemoveResourceFromGroupResponse;
import org.hyperic.hq.hqapi1.types.Resource;

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
public class GroupApi {

    private final HQConnection _conn;

    GroupApi(HQConnection conn) {
        _conn = conn;
    }
    
    /**
     * Create a {@link org.hyperic.hq.hqapi1.types.Group}.
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
        return null;
    }
    
    /**
     * Delete a {@link Group}
     *
     * @param group The group to delete.
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if the
     * group was deleted successfully.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public DeleteGroupResponse deleteGroup(Group group)
        throws IOException
    {
        return null;
    }
    
    /**
     * Delete a {@link Resource} from a {@link Group}
     *
     * @param group The {@link Group} to delete the resource from.
     * @param resource The {@link Resource} to remove.
     *
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if the
     * resource was successfully removed from the group.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public RemoveResourceFromGroupResponse removeResource(Group group,
                                                          Resource resource)
        throws IOException
    {
        return null;
    }

    /**
     * List all the Groups in HQ visible by the login
     *
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if 
     * all the groups were successfully retrieved from the server.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public GetGroupsResponse listGroups()
        throws IOException
    {
        return null;
    }

    /**
     * List all the Resources associated with a {@link Group}
     *
     * @param group The {@link org.hyperic.hq.hqapi1.types.Group} to query.
     *
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if all
     * the resources were successfully retrieved.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public GetResourcesInGroupResponse listResources(Group group)
        throws IOException
    {
        return null;
    }

    /**
     * Add the specified {@link Resource} to the {@link Group}
     *
     * @param group The {@link org.hyperic.hq.hqapi1.types.Group} to operate on.
     * @param resource The {@link org.hyperic.hq.hqapi1.types.Resource} to add.
     *
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if the
     * resource was successfully added to the group.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public AddResourceToGroupResponse addResource(Group group, Resource resource)
        throws IOException
    {
        return null;
    }

}
