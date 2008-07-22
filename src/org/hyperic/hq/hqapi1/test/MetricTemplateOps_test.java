package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.MetricApi;
import org.hyperic.hq.hqapi1.types.GetMetricTemplateResponse;
import org.hyperic.hq.hqapi1.types.MetricTemplate;
import org.hyperic.hq.hqapi1.types.SetMetricDefaultIndicatorResponse;
import org.hyperic.hq.hqapi1.types.SetMetricDefaultIntervalResponse;
import org.hyperic.hq.hqapi1.types.SetMetricDefaultOnResponse;

public class MetricTemplateOps_test extends HQApiTestBase {
    
    private static final Integer metricId = 10103;

    public MetricTemplateOps_test(String name) {
        super(name);
    }
/*    
    public void testSetDefaultInterval() throws Exception {
        MetricApi api = getApi().getMetricApi();
        GetMetricTemplateResponse resp = api.getMetricTemplate(metricId);
        MetricTemplate templ = resp.getMetricTemplate();
        int templateId = templ.getId();

        SetMetricDefaultIntervalResponse diResp =
            api.setDefaultInterval(templateId, 10);
        hqAssertSuccess(diResp);
        resp = api.getMetricTemplate(metricId);
        templ = resp.getMetricTemplate();
        assertTrue(templ.getDefaultInterval().equals(600000));
    }

    public void testSetDefaultIndicator() throws Exception {
        MetricApi api = getApi().getMetricApi();
        GetMetricTemplateResponse resp = api.getMetricTemplate(metricId);
        MetricTemplate templ = resp.getMetricTemplate();
        int templateId = templ.getId();

        SetMetricDefaultIndicatorResponse diResp =
            api.setDefaultIndicator(templateId, true);
        hqAssertSuccess(diResp);
        resp = api.getMetricTemplate(metricId);
        templ = resp.getMetricTemplate();
        assertTrue(templ.isIndicator());

        diResp = api.setDefaultIndicator(templateId, false);
        hqAssertSuccess(diResp);
        resp = api.getMetricTemplate(metricId);
        templ = resp.getMetricTemplate();
        assertFalse(templ.isIndicator());
    }

    public void testSetDefaultOn() throws Exception {
        MetricApi api = getApi().getMetricApi();
        GetMetricTemplateResponse resp = api.getMetricTemplate(metricId);
        MetricTemplate templ = resp.getMetricTemplate();
        int templateId = templ.getId();

        SetMetricDefaultOnResponse donResp = api.setDefaultOn(templateId, true);
        hqAssertSuccess(donResp);
        resp = api.getMetricTemplate(metricId);
        templ = resp.getMetricTemplate();
        assertTrue(templ.isDefaultOn());

        donResp = api.setDefaultOn(templateId, false);
        hqAssertSuccess(donResp);
        resp = api.getMetricTemplate(metricId);
        templ = resp.getMetricTemplate();
        assertTrue(!templ.isDefaultOn());
    }
*/
}
