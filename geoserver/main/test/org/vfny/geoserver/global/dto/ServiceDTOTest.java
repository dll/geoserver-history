/* Copyright (c) 2001 - 2007 TOPP - www.openplans.org.  All rights reserved.
 * This code is licensed under the GPL 2.0 license, availible at the root
 * application directory.
 */
package org.vfny.geoserver.global.dto;

import java.net.URL;

import junit.framework.TestCase;


/**
 * ServiceDTOTest JUnit Test.
 *
 * @author jgarnett, Refractions Research, Inc.
 * @author $Author: jive $ (last modification)
 * @version $Id: ServiceDTOTest.java,v 1.5 2004/03/03 09:39:08 jive Exp $
 */
public class ServiceDTOTest extends TestCase {
    ServiceDTO dto;

    /**
     * Constructor for ServiceDTOTest.
     *
     * @param name
     */
    public ServiceDTOTest(String name) {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();

        dto = new ServiceDTO();
        dto.setAbstract("abstract");
        dto.setAccessConstraints("NONE");
        dto.setEnabled(true);
        dto.setFees("NONE");
        dto.setKeywords(new String[0]);
        dto.setMaintainer("junit");
        dto.setName("TestService");
        dto.setOnlineResource(new URL("http://localhost/"));
        dto.setTitle("Title");
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
        dto = null;
    }

//    public void testHashCode() {
//        assertEquals(0, new ServiceDTO().hashCode());
//        assertTrue(0 != dto.hashCode());
//    }

    /*
     * Test for void ServiceDTO()
     */
    public void testServiceDTO() {
    }

    /*
     * Test for void ServiceDTO(ServiceDTO)
     */
    public void testServiceDTOServiceDTO() {
    }

    /*
     * Test for Object clone()
     */
    public void testClone() {
    }

    /*
     * Test for boolean equals(Object)
     */
    public void testEqualsObject() {
    }

    public void testGetName() {
    }

    public void testGetOnlineResource() {
    }

    public void testGetTitle() {
    }

    public void testSetName() {
    }

    public void testSetOnlineResource() {
    }

    public void testSetTitle() {
    }

    public void testGetAbstract() {
    }

    public void testGetAccessConstraints() {
    }

    public void testIsEnabled() {
    }

    public void testGetFees() {
    }

    public void testGetKeywords() {
    }

    public void testGetMaintainer() {
    }

    public void testSetAbstract() {
    }

    public void testSetAccessConstraints() {
    }

    public void testSetEnabled() {
    }

    public void testSetFees() {
    }

    public void testSetKeywords() {
    }

    public void testSetMaintainer() {
    }
}
