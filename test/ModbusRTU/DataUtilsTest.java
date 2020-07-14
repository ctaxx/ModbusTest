/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModbusRTU;

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
public class DataUtilsTest {
    
    public DataUtilsTest() {
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
     * Test of dataArrayToInt method, of class DataUtils.
     */
    @Test
    public void testDataArrayToInt() {
        System.out.println("dataArrayToInt");
;
        int[] dataArray = {0xEFC5, 1};
        int expResult = 126917;
        int result = DataUtils.dataArrayToInt(dataArray);
        assertEquals(expResult, result);
        
        
        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }
    
    @Test
    public void testConvertLongToRegisters(){
        System.out.println("convertLongToRegisters");
        long l = 77777;
        int expResult[] = {0x1, 0x2FD1};
        int result [] = DataUtils.ConvertLongToRegisters(l);
        assertEquals(expResult[0], result[0]);
    }
    
}
