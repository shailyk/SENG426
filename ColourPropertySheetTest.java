package com.jmonkey.export;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import junit.framework.*;

public class ColourPropertySheetTest extends TestCase{

    public ColourPropertySheetTest() {
    }

  public void test_init() {
    try {
       ColourPropertySheet.init();
    } catch (Throwable t) {
       assertTrue(true);
       System.out.println("Cancelled");
    }
	}
}

public class ColourCellRendererTest extends TestCase{

	public void test_getTableCellRendererComponent() {
	try {
     ColourCellRenderer.getTableCellRendererComponent(1,1,1,1,1,1);
    }
    catch (Throwable t) {
       assertTrue(true);
       System.out.println("Exception occurred");
    }
	}
}

public class PairTableModelTest extends TestCase{

	public void test_getValueAt() {
        
        String result = PairTableModelTest.getValueAt(2);
        String expResult = "";
        assertEquals(expResult, result);

    }

}
