package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.types.Group;
import org.hyperic.hq.hqapi1.types.Resource;
import org.hyperic.hq.hqapi1.types.ResourcePrototype;
import org.hyperic.hq.hqapi1.types.Role;

import java.util.Random;

public class GroupTestBase extends HQApiTestBase {

    static final String GROUP_NAME = "API Test Group";
    static final String GROUP_LOCATION = "API Test Group Location";
    static final String GROUP_DESCRIPTION = "API Test Group Description";

    public GroupTestBase(String name) {
        super(name);
    }

    protected void validateGroup(Group g) {
        assertTrue("Invalid id for Group.", g.getId() >= 0);
        assertTrue("Found invalid name for Group with id=" + g.getId(),
                   g.getName().length() > 0);

        if (g.getResourcePrototype() != null) {
            ResourcePrototype pt = g.getResourcePrototype();
            assertTrue("Invalid prototype id", pt.getId() > 0);
            assertTrue("Invalid prototype name for group " + g.getName(),
                       pt.getName().length() > 0);
        }

        if (g.getResource().size() > 0) {
            for (Resource r : g.getResource()) {
                assertTrue("Invalid resource id for group member", r.getId() > 0);
                assertTrue("Invalid resource name for group member",
                           r.getName().length() > 0);
            }
        }

        if (g.getRole().size() > 0) {
            for (Role r : g.getRole()) {
                assertTrue("Invalid role id", r.getId() >= 0);
                assertTrue("Invalid role name", r.getName().length() > 0);
            }
        }
    }

    /**
     * Generate a valid Group object that's guaranteed to have a unique Name
     *
     * @return A valid Group object.
     */
    public Group generateTestGroup() {

        Random r = new Random();

        Group group = new Group();
        group.setName(GROUP_NAME + r.nextInt());
        group.setDescription(GROUP_DESCRIPTION);
        group.setLocation(GROUP_LOCATION);

        return group;
    }
}
