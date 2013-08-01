/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmonkey.export;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.util.Enumeration;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import junit.framework.*;

/**
 *
 * @author Shaily
 */
public class RegistryTest extends TestCase {

    public RegistryTest() {
    }

    public void testjavaTypeToType() {

        int result = Registry.TYPE_STRING_SINGLE;
        int expresult;

        expresult = Registry.javaTypeToType("String");
        assertEquals(expresult, result);

        try {
            Registry.javaTypeToType("invalid");
            fail("Registry Exception should have been thrown");
        } catch (RegistryException re) {
            assertTrue(true);
        } catch (Exception e) {
            fail("Didnt get expected Exception");

        }

    }

    public void testtypeToJavaType() {

        String expresult;
        expresult = Registry.typeToJavaType(0);
        String result = "unknown";
        assertEquals(expresult, result);


    }

    public void testencryptMD5() {
        try {
            Registry.encryptMD5(null);
            fail("shouldnt reach here");
        } 
        catch (Exception nsa) {
            assertTrue(true);
            System.out.println("Exception");
           // fail("Exception");
            
            
            
        }

    }
}
