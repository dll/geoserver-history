package org.geoserver.security.decorators;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import org.springframework.security.SpringSecurityException;
import org.geoserver.security.SecureObjectsTest;
import org.geoserver.security.SecureCatalogImpl.WrapperPolicy;
import org.geotools.data.DataStore;
import org.geotools.data.DataUtilities;
import org.geotools.data.Transaction;
import org.geotools.data.simple.SimpleFeatureStore;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;

public class ReadOnlyDataStoreTest extends SecureObjectsTest {

    private DataStore ds;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        SimpleFeatureStore fs = createNiceMock(SimpleFeatureStore.class);
        expect(fs.getSchema()).andReturn(DataUtilities.createType("test", "g:Polygon,name:String")).anyTimes();
        replay(fs);
        ds = createNiceMock(DataStore.class);
        expect(ds.getFeatureSource("blah")).andReturn(fs);
        replay(ds);
    }

    public void testDontChallenge() throws Exception {
        ReadOnlyDataStore ro = new ReadOnlyDataStore(ds, WrapperPolicy.HIDE);

        try {
            ro.createSchema(null);
            fail("Should have failed with an unsupported operation exception");
        } catch (UnsupportedOperationException e) {
            //
        }
        try {
            ro.updateSchema((String) null, null);
            fail("Should have failed with an unsupported operation exception");
        } catch (UnsupportedOperationException e) {
            //
        }

        try {
            ro.updateSchema((Name) null, null);
            fail("Should have failed with an unsupported operation exception");
        } catch (UnsupportedOperationException e) {
            //
        }
        try {
            ro.getFeatureWriter("states", Transaction.AUTO_COMMIT);
            fail("Should have failed with an unsupported operation exception");
        } catch (UnsupportedOperationException e) {
            //
        }
        try {
            ro.getFeatureWriter("states", Filter.INCLUDE,
                    Transaction.AUTO_COMMIT);
            fail("Should have failed with an unsupported operation exception");
        } catch (UnsupportedOperationException e) {
            //
        }
        try {
            ro.getFeatureWriterAppend("states", Transaction.AUTO_COMMIT);
            fail("Should have failed with an unsupported operation exception");
        } catch (UnsupportedOperationException e) {
            //
        }
    }
    
    public void testChallenge() throws Exception {
        ReadOnlyDataStore ro = new ReadOnlyDataStore(ds, WrapperPolicy.RO_CHALLENGE);

        try {
            ro.createSchema(null);
            fail("Should have failed with a security exception");
        } catch (SpringSecurityException e) {
            //
        }
        try {
            ro.updateSchema((String) null, null);
            fail("Should have failed with a security exception");
        } catch (SpringSecurityException e) {
            //
        }

        try {
            ro.updateSchema((Name) null, null);
            fail("Should have failed with a security exception");
        } catch (SpringSecurityException e) {
            //
        }
        try {
            ro.getFeatureWriter("states", Transaction.AUTO_COMMIT);
            fail("Should have failed with a security exception");
        } catch (SpringSecurityException e) {
            //
        }
        try {
            ro.getFeatureWriter("states", Filter.INCLUDE,
                    Transaction.AUTO_COMMIT);
            fail("Should have failed with a security exception");
        } catch (SpringSecurityException e) {
            //
        }
        try {
            ro.getFeatureWriterAppend("states", Transaction.AUTO_COMMIT);
            fail("Should have failed with a security exception");
        } catch (SpringSecurityException e) {
            //
        }
    }
}
