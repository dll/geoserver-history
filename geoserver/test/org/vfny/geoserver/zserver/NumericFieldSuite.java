/* Copyright (c) 2001 TOPP - www.openplans.org.  All rights reserved.
 * This code is licensed under the GPL 2.0 license, availible at the root 
 * application directory.
 */
package org.vfny.geoserver.zserver;

import java.util.logging.Logger;
import java.util.logging.Level;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.geotools.resources.Geotools;
import java.math.*;

/**
 * Tests the NumericField number and string conversion.
 *
 * @author Chris Holmes, TOPP
 * @version $VERSION$
 */

public class NumericFieldSuite extends TestCase {

    /* Initializes the logger. */
    static {
        Geotools.init("Log4JFormatter", Level.FINEST);
    }

    /** Standard logging instance */
    private static final Logger LOGGER = 
        Logger.getLogger("org.vfny.geoserver.zserver");


    
    /**
     * Initializes the database and request handler.
     */
    public NumericFieldSuite (String testName) {
        super(testName);
    }


    
    public static Test suite() {
        LOGGER.info("Creating NumericField test suite");
	TestSuite suite = new TestSuite(NumericFieldSuite.class);
        return suite;
    }
    

    public void setUp() {
	
    }

    

    public void testSimple() {
	assertTrue(checkNumToString(10));
	assertTrue(checkNumToString(5));
	assertTrue(checkNumToString(15.5));
	assertTrue(checkNumToString(.004));
	
	
	//only currently handles 10 digits to right of decimal
	//and 10 to the right.  Change defaultLeft and defaultRight
	//in NumericField if it needs to handle larger values.

	

    }

    public void testBorderCases() {
	assertTrue(checkNumToString(150000000));
	assertTrue(checkNumToString(9999999999.0));
	assertTrue(checkNumToString(.0000000001));
	assertTrue(checkNumToString(1242048.3488392823));
    }

    public void testNegatives() {
	assertTrue(checkNumToString(-10));
	assertTrue(checkNumToString(-5));
	assertTrue(checkNumToString(-15.5));
	assertTrue(checkNumToString(-.004));
	assertTrue(checkNumToString(-1500000));
	assertTrue(checkNumToString(-9999999999.0));
	assertTrue(checkNumToString(-.0000000001));
	assertTrue(checkNumToString(-1242048.3488392823));
    }
    
    /**
     * Tests numbers that are too big or small to be handled
     * by the current implementation.  
     */
    public void testFails() {
	assertTrue(!checkNumToString(10000000000.0));
	assertTrue(!checkNumToString(.000000000099));
    }

    public void testParsing() {
	Double dub = new Double(150);
	assertTrue(NumericField.numberToString(dub).
		   equals(NumericField.numberToString("150")));
	dub = new Double(-0.05);
	assertTrue(NumericField.numberToString(dub).
		   equals(NumericField.numberToString("-.05")));
	try { //should throw exception, user should deal with 
	    //appropriately.
	    NumericField.numberToString("234b.32");
	} catch (NumberFormatException e) {
	    assertTrue((e.getMessage().equals("For input string: \"234b.32\"")));
	}
    }

    private boolean checkNumToString(double num) {
	Double test = new Double(num);
	String stringRep = NumericField.numberToString(test);
	LOGGER.fine("test is " + num + " as a string is " + stringRep); 
	Double backToDub = NumericField.stringToNumber(stringRep);
	LOGGER.fine(" converted back to a double is " + backToDub);
	return num == backToDub.doubleValue();
						       
				
    }


	
}
