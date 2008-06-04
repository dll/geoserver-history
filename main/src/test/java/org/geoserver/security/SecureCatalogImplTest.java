package org.geoserver.security;

import static org.easymock.EasyMock.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.acegisecurity.context.SecurityContextHolder;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.CoverageInfo;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.security.decorators.ReadOnlyFeatureTypeInfo;
import org.geoserver.security.decorators.ReadOnlyLayerInfo;

public class SecureCatalogImplTest extends AbstractAuthorizationTest {

    private FeatureTypeInfo states;

    private CoverageInfo arcGrid;

    private List<LayerInfo> layers;

    private List<FeatureTypeInfo> featureTypes;

    private List<CoverageInfo> coverages;

    private Catalog catalog;

    private List<WorkspaceInfo> workspaces;

    private FeatureTypeInfo roads;

    private FeatureTypeInfo landmarks;

    private FeatureTypeInfo bases;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        states = (FeatureTypeInfo) statesLayer.getResource();
        arcGrid = (CoverageInfo) arcGridLayer.getResource();
        roads = (FeatureTypeInfo) roadsLayer.getResource();
        landmarks = (FeatureTypeInfo) landmarksLayer.getResource();
        bases = (FeatureTypeInfo) basesLayer.getResource();

        // build resource collections
        layers = Arrays.asList(statesLayer, roadsLayer, landmarksLayer, basesLayer, arcGridLayer);
        featureTypes = new ArrayList<FeatureTypeInfo>();
        coverages = new ArrayList<CoverageInfo>();
        for (LayerInfo layer : layers) {
            if (layer.getResource() instanceof FeatureTypeInfo)
                featureTypes.add((FeatureTypeInfo) layer.getResource());
            else
                coverages.add((CoverageInfo) layer.getResource());
        }
        workspaces = Arrays.asList(toppWs, nurcWs);

        // prime the catalog
        catalog = createNiceMock(Catalog.class);
        expect(catalog.getFeatureTypeByName("topp:states")).andReturn((FeatureTypeInfo) states)
                .anyTimes();
        expect(catalog.getResourceByName("topp:states", FeatureTypeInfo.class)).andReturn(
                (FeatureTypeInfo) states).anyTimes();
        expect(catalog.getLayerByName("topp:states")).andReturn(statesLayer).anyTimes();
        expect(catalog.getCoverageByName("nurc:arcgrid")).andReturn((CoverageInfo) arcGrid)
                .anyTimes();
        expect(catalog.getResourceByName("nurc:arcgrid", CoverageInfo.class)).andReturn(
                (CoverageInfo) arcGrid).anyTimes();
        expect(catalog.getFeatureTypeByName("topp:roads")).andReturn((FeatureTypeInfo) roads)
                .anyTimes();
        expect(catalog.getFeatureTypeByName("topp:landmarks")).andReturn(
                (FeatureTypeInfo) landmarks).anyTimes();
        expect(catalog.getFeatureTypeByName("topp:bases")).andReturn((FeatureTypeInfo) bases)
                .anyTimes();
        expect(catalog.getLayers()).andReturn(layers).anyTimes();
        expect(catalog.getFeatureTypes()).andReturn(featureTypes).anyTimes();
        expect(catalog.getCoverages()).andReturn(coverages).anyTimes();
        expect(catalog.getWorkspaces()).andReturn(workspaces).anyTimes();
        expect(catalog.getWorkspaceByName("topp")).andReturn(toppWs).anyTimes();
        expect(catalog.getWorkspaceByName("nurc")).andReturn(nurcWs).anyTimes();
        replay(catalog);
    }

    public void testWideOpen() throws Exception {
        DefaultDataAccessManager manager = buildManager("wideOpen.properties");
        SecureCatalogImpl sc = new SecureCatalogImpl(catalog, manager);

        // use no user at all
        assertSame(states, sc.getFeatureTypeByName("topp:states"));
        assertSame(arcGrid, sc.getCoverageByName("nurc:arcgrid"));
        assertSame(states, sc.getResourceByName("topp:states", FeatureTypeInfo.class));
        assertSame(arcGrid, sc.getResourceByName("nurc:arcgrid", CoverageInfo.class));
        assertEquals(featureTypes, sc.getFeatureTypes());
        assertEquals(coverages, sc.getCoverages());
        assertEquals(workspaces, sc.getWorkspaces());
        assertEquals(toppWs, sc.getWorkspaceByName("topp"));
    }

    public void testLockedDown() throws Exception {
        DefaultDataAccessManager manager = buildManager("lockedDown.properties");
        SecureCatalogImpl sc = new SecureCatalogImpl(catalog, manager);

        // try with read only user
        SecurityContextHolder.getContext().setAuthentication(roUser);
        assertNull(sc.getFeatureTypeByName("topp:states"));
        assertNull(sc.getCoverageByName("nurc:arcgrid"));
        assertNull(sc.getResourceByName("topp:states", FeatureTypeInfo.class));
        assertNull(sc.getResourceByName("nurc:arcgrid", CoverageInfo.class));
        assertEquals(0, sc.getFeatureTypes().size());
        assertEquals(0, sc.getCoverages().size());
        assertEquals(0, sc.getWorkspaces().size());
        assertNull(sc.getWorkspaceByName("topp"));

        // try with write enabled user
        SecurityContextHolder.getContext().setAuthentication(rwUser);
        assertSame(states, sc.getFeatureTypeByName("topp:states"));
        assertSame(arcGrid, sc.getCoverageByName("nurc:arcgrid"));
        assertSame(states, sc.getResourceByName("topp:states", FeatureTypeInfo.class));
        assertSame(arcGrid, sc.getResourceByName("nurc:arcgrid", CoverageInfo.class));
        assertEquals(featureTypes, sc.getFeatureTypes());
        assertEquals(coverages, sc.getCoverages());
        assertEquals(workspaces, sc.getWorkspaces());
        assertEquals(toppWs, sc.getWorkspaceByName("topp"));
    }

    public void testPublicRead() throws Exception {
        DefaultDataAccessManager manager = buildManager("publicRead.properties");
        SecureCatalogImpl sc = new SecureCatalogImpl(catalog, manager);

        // try with read only user
        SecurityContextHolder.getContext().setAuthentication(roUser);
        assertSame(arcGrid, sc.getCoverageByName("nurc:arcgrid"));
        assertSame(arcGrid, sc.getResourceByName("nurc:arcgrid", CoverageInfo.class));
        assertEquals(coverages, sc.getCoverages());
        assertEquals(workspaces, sc.getWorkspaces());
        assertEquals(toppWs, sc.getWorkspaceByName("topp"));
        // .. the following should have been wrapped
        assertNotNull(sc.getFeatureTypeByName("topp:states"));
        assertTrue(sc.getFeatureTypeByName("topp:states") instanceof ReadOnlyFeatureTypeInfo);
        assertTrue(sc.getResourceByName("topp:states", FeatureTypeInfo.class) instanceof ReadOnlyFeatureTypeInfo);
        assertEquals(featureTypes.size(), sc.getFeatureTypes().size());
        for (FeatureTypeInfo ft : sc.getFeatureTypes()) {
            assertTrue(ft instanceof ReadOnlyFeatureTypeInfo);
        }
        assertNotNull(sc.getLayerByName("topp:states"));
        assertTrue(sc.getLayerByName("topp:states") instanceof ReadOnlyLayerInfo);

        // try with write enabled user (nothing has been wrapped)
        SecurityContextHolder.getContext().setAuthentication(rwUser);
        assertSame(states, sc.getFeatureTypeByName("topp:states"));
        assertSame(arcGrid, sc.getCoverageByName("nurc:arcgrid"));
        assertSame(states, sc.getResourceByName("topp:states", FeatureTypeInfo.class));
        assertSame(arcGrid, sc.getResourceByName("nurc:arcgrid", CoverageInfo.class));
        assertEquals(featureTypes, sc.getFeatureTypes());
        assertEquals(coverages, sc.getCoverages());
        assertEquals(workspaces, sc.getWorkspaces());
        assertEquals(toppWs, sc.getWorkspaceByName("topp"));
    }

    public void testComplex() throws Exception {
        DefaultDataAccessManager manager = buildManager("complex.properties");
        SecureCatalogImpl sc = new SecureCatalogImpl(catalog, manager);

        // try with anonymous user
        SecurityContextHolder.getContext().setAuthentication(anonymous);
        // ... roads follows generic namespace rule, it's read only, nobody can write it
        assertTrue(sc.getFeatureTypeByName("topp:roads") instanceof ReadOnlyFeatureTypeInfo);
        // ... states requires READER role
        assertNull(sc.getFeatureTypeByName("topp:states"));
        // ... landmarks requires WRITER role to be written
        assertTrue(sc.getFeatureTypeByName("topp:landmarks") instanceof ReadOnlyFeatureTypeInfo);
        // ... bases requires one to be in the military
        assertNull(sc.getFeatureTypeByName("topp:bases"));
        
        // ok, let's try the same with read only user
        SecurityContextHolder.getContext().setAuthentication(roUser);
        assertTrue(sc.getFeatureTypeByName("topp:roads") instanceof ReadOnlyFeatureTypeInfo);
        assertTrue(sc.getFeatureTypeByName("topp:states") instanceof ReadOnlyFeatureTypeInfo);
        assertTrue(sc.getFeatureTypeByName("topp:landmarks") instanceof ReadOnlyFeatureTypeInfo);
        assertNull(sc.getFeatureTypeByName("topp:bases"));
        
        // now with the write enabled user
        SecurityContextHolder.getContext().setAuthentication(rwUser);
        assertTrue(sc.getFeatureTypeByName("topp:roads") instanceof ReadOnlyFeatureTypeInfo);
        assertSame(states, sc.getFeatureTypeByName("topp:states"));
        assertSame(landmarks, sc.getFeatureTypeByName("topp:landmarks"));
        assertNull(sc.getFeatureTypeByName("topp:bases"));
        
        // finally let's try the military type
        SecurityContextHolder.getContext().setAuthentication(milUser);
        assertTrue(sc.getFeatureTypeByName("topp:roads") instanceof ReadOnlyFeatureTypeInfo);
        assertNull(sc.getFeatureTypeByName("topp:states"));
        assertTrue(sc.getFeatureTypeByName("topp:landmarks") instanceof ReadOnlyFeatureTypeInfo);
        // ... bases requires one to be in the military
        assertSame(bases, sc.getFeatureTypeByName("topp:bases"));
    }

}
