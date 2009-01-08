package org.hyperic.hq.hqapi1;

import org.hyperic.hq.hqapi1.types.Group;
import org.hyperic.hq.hqapi1.types.GroupResponse;
import org.hyperic.hq.hqapi1.types.GroupsRequest;
import org.hyperic.hq.hqapi1.types.GroupsResponse;
import org.hyperic.hq.hqapi1.types.ResponseStatus;
import org.hyperic.hq.hqapi1.types.StatusResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private GroupResponse syncSingleGroup(Group group)
        throws IOException
    {
        List<Group> groups = new ArrayList<Group>();
        groups.add(group);
        GroupsResponse syncResponse = syncGroups(groups);

        GroupResponse groupResponse = new GroupResponse();
        groupResponse.setStatus(syncResponse.getStatus());
        if (syncResponse.getStatus().equals(ResponseStatus.SUCCESS)) {
            groupResponse.setGroup(syncResponse.getGroup().get(0));
        } else {
            groupResponse.setError(syncResponse.getError());
        }

        return groupResponse;
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
    public GroupResponse createGroup(Group group)
        throws IOException
    {
        return syncSingleGroup(group);
    }

    /**
     * Update a {@link org.hyperic.hq.hqapi1.types.Group}.
     *
     * @param group The group to create.
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if the
     * group was created successfully.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public GroupResponse updateGroup(Group group)
        throws IOException
    {
        return syncSingleGroup(group);
    }

    /**
     * Delete a {@link Group}.
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
     * List all {@link org.hyperic.hq.hqapi1.types.Group}s.
     *
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if 
     * all the groups were successfully retrieved from the server.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public GroupsResponse getGroups()
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
    public GroupsResponse getCompatibleGroups()
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
    public GroupsResponse getMixedGroups()
        throws IOException
    {
        Map<String,String[]> params = new HashMap<String,String[]>();
        params.put("compatible", new String[] { Boolean.toString(false) });
        return doGet("group/list.hqu", params, GroupsResponse.class);
    }

    /**
     * Sync a list of {@link org.hyperic.hq.hqapi1.types.Group}s.
     *
     * @param groups The list of groups to sync.
     *
     * @return {@link org.hyperic.hq.hqapi1.types.ResponseStatus#SUCCESS} if
     * all the groups were successfully syced.
     *
     * @throws IOException If a network error occurs while making the request.
     */
    public GroupsResponse syncGroups(List<Group> groups)
            throws IOException {

        GroupsRequest groupRequest = new GroupsRequest();
        groupRequest.getGroup().addAll(groups);
        return doPost("group/sync.hqu", groupRequest, GroupsResponse.class);
    }
}
