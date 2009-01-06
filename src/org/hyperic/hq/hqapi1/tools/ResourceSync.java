package org.hyperic.hq.hqapi1.tools;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.hyperic.hq.hqapi1.HQApi;
import org.hyperic.hq.hqapi1.XmlUtil;
import org.hyperic.hq.hqapi1.ResourceApi;
import org.hyperic.hq.hqapi1.types.StatusResponse;
import org.hyperic.hq.hqapi1.types.ResourcesResponse;
import org.hyperic.hq.hqapi1.types.Resource;

import java.util.List;

public class ResourceSync extends ToolsBase {

    private static void syncResources(String[] args) throws Exception {

        OptionParser p = getOptionParser();
        OptionSet options = getOptions(p, args);

        HQApi api = getApi(options);

        ResourceApi resourceApi = api.getResourceApi();

        ResourcesResponse resp = XmlUtil.deserialize(ResourcesResponse.class,
                                                     System.in);
        List<Resource> resources = resp.getResource();

        StatusResponse syncResponse = resourceApi.syncResources(resources);
        checkSuccess(syncResponse);

        System.out.println("Successfully synced " + resources.size() + " resources.");
    }

    public static void main(String[] args) throws Exception {
        try {
            syncResources(args);
        } catch (Exception e) {
            System.err.println("Error syncing resources: " + e.getMessage());
            System.exit(-1);
        }
    }
}
