package org.hyperic.hq.hqapi1.test;

import org.hyperic.hq.hqapi1.EscalationApi;
import org.hyperic.hq.hqapi1.types.CreateEscalationResponse;
import org.hyperic.hq.hqapi1.types.Escalation;
import org.hyperic.hq.hqapi1.types.FullEscalationAction;
import org.hyperic.hq.hqapi1.types.Who;

public class EscalationTestBase extends HQApiTestBase {
    protected static final String TEST_NAME = EscalationTestBase.class.getName();
    private Escalation _esc = null;

    public EscalationTestBase(String name) {
        super(name);
    }

    protected EscalationApi getEscalationApi() {
        return getApi().getEscalationApi();
    }

    /* (non-Javadoc)
     * @see org.hyperic.hq.hqapi1.test.HQApiTestBase#setUp()
     */
    @Override
    public void setUp() throws Exception {
        super.setUp();
        
        Escalation esc = new Escalation();
        esc.setName(TEST_NAME);
        
        EscalationApi escApi = getEscalationApi();

        FullEscalationAction act = escApi.createEmailAction();
        Who who = new Who();
        who.setName("escalation@test.com");
        act.getNotify().add(who);
        esc.getAction().add(act);
        
        CreateEscalationResponse resp =
            escApi.createEscalation(esc);
        _esc = resp.getEscalation();
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        getEscalationApi().deleteEscalation(_esc.getId());
    }
    
    protected Escalation getTestEscalation() {
        return _esc;
    }

}
