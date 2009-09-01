package org.geoserver.web.security.data;

import org.geoserver.data.test.MockData;
import org.geoserver.security.AccessMode;
import org.geoserver.security.DataAccessRule;
import org.geoserver.security.DataAccessRuleDAO;
import org.geoserver.web.GeoServerWicketTestSupport;

public class DataAccessRulePageTest extends GeoServerWicketTestSupport {
    
    private DataAccessRuleDAO dao;
    private DataAccessRule rule;

    @Override
    protected void setUpInternal() throws Exception {
        dao = DataAccessRuleDAO.get();
        rule = new DataAccessRule(MockData.CITE_PREFIX, MockData.BASIC_POLYGONS.getLocalPart(), AccessMode.READ, "*");
        dao.addRule(DataAccessRule.READ_ALL);
        dao.addRule(DataAccessRule.WRITE_ALL);
        dao.addRule(rule);
        login();
        tester.startPage(DataAccessRulePage.class);
    }

    public void testRenders() throws Exception {
        tester.assertRenderedPage(DataAccessRulePage.class);
    }
    
    public void testEditRule() throws Exception {
        // the name link for the first user
        tester.clickLink("table:listContainer:items:1:itemProperties:0:component:link");
        tester.assertRenderedPage(EditDataAccessRulePage.class);
        assertEquals("*", tester.getComponentFromLastRenderedPage("ruleForm:workspace").getModelObject());
    }
    
    public void testNewRule() throws Exception {
        tester.clickLink("addRule");
        tester.assertRenderedPage(NewDataAccessRulePage.class);
        assertEquals("*", tester.getComponentFromLastRenderedPage("ruleForm:workspace").getModelObject());
    }
    
    public void testRemove() throws Exception {
        assertTrue(dao.getRules().contains(rule));
        // the remove link for the second user
        tester.clickLink("table:listContainer:items:2:itemProperties:2:component:link");
        tester.assertRenderedPage(DataAccessRulePage.class);
        assertFalse(dao.getRules().contains(rule));
    }

}