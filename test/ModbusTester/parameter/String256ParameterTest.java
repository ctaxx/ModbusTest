/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModbusTester.parameter;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author s.bikov
 */
public class String256ParameterTest {
    
    public String256ParameterTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getRange method, of class String256Parameter.
     */
    @Test
    public void testGetRange() {
        System.out.println("getRange");
        String256Parameter instance = new String256Parameter();
        String expResult = "String256";
        String result = instance.getRange();
        assertEquals(expResult, result);
    }

    /**
     * Test of getValueString method, of class String256Parameter.
     */
    @Test
    public void testGetValueString() {
        System.out.println("getValueString");
        int[] dataArray = {25924};
        String256Parameter instance = new String256Parameter();
        String expResult = "De";
        String result = instance.getValueString(dataArray);
        assertEquals(expResult, result);
    }
    
}
