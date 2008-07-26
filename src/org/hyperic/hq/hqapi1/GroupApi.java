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

public class GroupApi {
    
    private static final String HQU_URI = "/hqu/hqapi1/group";

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
     * On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#FAILURE} the
     * following error codes can be returned:
     *
     * <p>
     * <ul>
     *   <li>LoginFailure - The given username and password could not be validated.
     *   <li>InvalidParameters - All the required parameters in the Group object were not supplied.
     *   <li>ObjectExists - The group by the given name already exists.
     * </ul>
     * </p>
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
     * On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#FAILURE} the
     * following error codes can be returned:
     *
     * <p>
     * <ul>
     *   <li>LoginFailure - The given username and password could not be validated.
     *   <li>ObjectNotFound - The given group was not found in the system.
     * </ul>
     * </p>
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
     * @param group The group to delete the resource from.
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if the
     * resource was successfully removed from the group.
     *
     * On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#FAILURE} the
     * following error codes can be returned:
     *
     * <p>
     * <ul>
     *   <li>LoginFailure - The given username and password could not be validated.
     *   <li>ObjectNotFound - The given group or resource was not found in the system.
     * </ul>
     * </p>
     * @throws IOException If a network error occurs while making the request.
     */
    public RemoveResourceFromGroupResponse removeResource(Group group,
                                                          Resource res)
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
     * On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#FAILURE} the
     * following error codes can be returned:
     *
     * <p>
     * <ul>
     *   <li>LoginFailure - The given username and password could not be validated.
     * </ul>
     * </p>
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
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if all
     * the resources were successfully retrieved from the server
     *
     * On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#FAILURE} the
     * following error codes can be returned:
     *
     * <p>
     * <ul>
     *   <li>LoginFailure - The given username and password could not be validated.
     *   <li>ObjectNotFound - The given group was not found in the system.
     * </ul>
     * </p>
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
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if the
     * resource was successfully added to the group.
     *
     * On {@link org.hyperic.hq.hqapi1.types.ResponseStatus#FAILURE} the
     * following error codes can be returned:
     *
     * <p>
     * <ul>
     *   <li>LoginFailure - The given username and password could not be validated.
     *   <li>ObjectNotFound - The given group or resource was not found in the system.
     * </ul>
     * </p>
     * @throws IOException If a network error occurs while making the request.
     */
    public AddResourceToGroupResponse addResource(Group group, Resource res)
        throws IOException
    {
        return null;
    }

}
