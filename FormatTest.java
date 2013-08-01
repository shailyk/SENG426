/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmonkey.export;

import java.awt.Color;
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
public class FormatTest extends TestCase{

    public FormatTest() {
    }

    public void testasciiToLowerCase() {
        
        String result = Format.asciiToLowerCase("hello");
        String expResult = "hello";
        assertEquals(expResult, result);

    }
}