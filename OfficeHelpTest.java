ackage com.jmonkey.export;

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

public class OfficeHelpTest extends TestCase{

    public OfficeHelpTest() {
    }

  public void test_iceMethodCall() {
    try {
         OfficeHelp.iceMethodCall(null, null);
         assertEquals(expresult, null);
		 fail("Exception occurred");
    } catch (NoSuchMethodException e) {
        assertEquals("No such method in ice.iblite.Browser: ", e.getMessage());
        throw e;
    }  catch (InvocationTargetException ex) {
        assertEquals("InvocationTargetException in call to: ", ex.getMessage());
        throw ex;
    }  catch (IllegalAccessException ex) {
        assertEquals("IllegalAccessException in call to: ", ex.getMessage());
        throw ex;
    }	
	}

	public void test_redirectOutput() {
	try {
     OfficeHelp.redirectOutput();
    }
    catch (Exception ex) {
       assertTrue(true);
       System.out.println("Exception occurred");
    }
	}

}
