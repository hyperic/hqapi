package com.hyperic.hq.hqapi1;

import java.io.IOException;

import com.hyperic.hq.hqapi1.types.AddResourceToGroupResponse;
import com.hyperic.hq.hqapi1.types.CreateGroupResponse;
import com.hyperic.hq.hqapi1.types.DeleteGroupResponse;
import com.hyperic.hq.hqapi1.types.GetGroupsResponse;
import com.hyperic.hq.hqapi1.types.GetResourcesInGroupResponse;
import com.hyperic.hq.hqapi1.types.Group;
import com.hyperic.hq.hqapi1.types.RemoveResourceFromGroupResponse;
import com.hyperic.hq.hqapi1.types.Resource;

public class GroupApi {
    
    private static final String HQU_URI = "/hqu/hqapi1/user";

    private final HQConnection _conn;

    public GroupApi(HQConnection conn) {
        _conn = conn;
    }
    
    /**
     * Create a {@link com.hyperic.hq.hqapi1.types.Group}.
     *
     * @param group The group to create.
     * @return {@link com.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if the
     * group was created successfully.
     *
     * On {@link com.hyperic.hq.hqapi1.types.ResponseStatus#FAILURE} the
     * following error codes can be returned:
     *
     * <p>
     * <ul>
     *   <li>LoginFailure - The given username and password could not be validated.
     *   <li>InvalidParameters - All the required parameters in the Group object were not supplied.
     *   <li>ObjectExists - The group by the given name already exists.
     *   <li>UnexpectedError - Any other internal server error.
     * </ul>
     * </p>
     * @throws IOException If a network error occurs while making the request.
     */
    public CreateGroupResponse createGroup(Group group) {
        return null;
    }
    
    /**
     * Delete a {@link Group}
     *
     * @param group The group to delete.
     * @return {@link com.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if the
     * group was deleted successfully.
     *
     * On {@link com.hyperic.hq.hqapi1.types.ResponseStatus#FAILURE} the
     * following error codes can be returned:
     *
     * <p>
     * <ul>
     *   <li>LoginFailure - The given username and password could not be validated.
     *   <li>ObjectNotFound - The given group was not found in the system.
     *   <li>UnexpectedError - Any other internal server error.
     * </ul>
     * </p>
     * @throws IOException If a network error occurs while making the request.
     */
    public DeleteGroupResponse deleteGroup(Group group) {
        return null;
    }
    
    /**
     * Delete a {@link Resource} from a {@link Group}
     *
     * @param group The group to delete the resource from.
     * @return {@link com.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if the
     * resource was successfully removed from the group.
     *
     * On {@link com.hyperic.hq.hqapi1.types.ResponseStatus#FAILURE} the
     * following error codes can be returned:
     *
     * <p>
     * <ul>
     *   <li>LoginFailure - The given username and password could not be validated.
     *   <li>ObjectNotFound - The given group or resource was not found in the system.
     *   <li>UnexpectedError - Any other internal server error.
     * </ul>
     * </p>
     * @throws IOException If a network error occurs while making the request.
     */
    public RemoveResourceFromGroupResponse removeResource(Group group,
                                                          Resource res) {
        return null;
    }

    /**
     * List all the Groups in HQ visible by the login
     *
     * @return {@link com.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if 
     * all the groups were successfully retrieved from the server.
     *
     * On {@link com.hyperic.hq.hqapi1.types.ResponseStatus#FAILURE} the
     * following error codes can be returned:
     *
     * <p>
     * <ul>
     *   <li>LoginFailure - The given username and password could not be validated.
     *   <li>UnexpectedError - Any other internal server error.
     * </ul>
     * </p>
     * @throws IOException If a network error occurs while making the request.
     */
    public GetGroupsResponse listGroups() {
        return null;
    }

    /**
     * List all the Resources associated with a {@link Group}
     *
     * @return {@link com.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if all
     * the resources were successfully retrieved from the server
     *
     * On {@link com.hyperic.hq.hqapi1.types.ResponseStatus#FAILURE} the
     * following error codes can be returned:
     *
     * <p>
     * <ul>
     *   <li>LoginFailure - The given username and password could not be validated.
     *   <li>ObjectNotFound - The given group was not found in the system.
     *   <li>UnexpectedError - Any other internal server error.
     * </ul>
     * </p>
     * @throws IOException If a network error occurs while making the request.
     */
    public GetResourcesInGroupResponse listResources(Group group) {
        return null;
    }

    /**
     * Add the specified {@link Resource} to the {@link Group}
     *
     * @return {@link com.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if the
     * resource was successfully added to the group.
     *
     * On {@link com.hyperic.hq.hqapi1.types.ResponseStatus#FAILURE} the
     * following error codes can be returned:
     *
     * <p>
     * <ul>
     *   <li>LoginFailure - The given username and password could not be validated.
     *   <li>ObjectNotFound - The given group or resource was not found in the system.
     *   <li>UnexpectedError - Any other internal server error.
     * </ul>
     * </p>
     * @throws IOException If a network error occurs while making the request.
     */
    public AddResourceToGroupResponse addResource(Group group, Resource res) {
        return null;
    }

}
