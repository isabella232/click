package net.sf.click.control;

import junit.framework.TestCase;
import net.sf.click.MockContext;

/**
 * Test for manipulating html attributes of Click Controls.
 * 
 * @author Bob Schellink
 */
public class AttributeTest extends TestCase {
        
    /**
     * Test for CLK-249.
     * Cleanup AbstractControl attributes and styles.
     * 
     * This tests the AbstractControl's style and attribute API set.
     */
    public void testAttributesAndStyles() {
        MockContext.initContext();
        
        //Check that multiple styles are rendered as expected
        TextField nameField = new TextField("nameFld");
        nameField.setStyle("color", "red");
        nameField.setStyle("border", "1px solid");
        String expected = "style=\"color:red;border:1px solid;\"";
        //Assert that the expected string was generated by the field
        assertTrue(nameField.toString().indexOf(expected) >= 0);

        //Check that setAttribute("style", null) will remove all styles
        nameField = new TextField("nameFld");
        nameField.setStyle("color", "blue");
        nameField.setStyle("color", "red");
        nameField.setAttribute("style", null);
        assertFalse(nameField.hasAttributes());

        //Check that setStyle("style", null) will remove all styles
        nameField = new TextField("nameFld");
        nameField.setStyle("color", "red");
        nameField.setStyle("color", null);
        assertFalse(nameField.hasAttributes());

        //Check that setAttribute("style", styles) will be parsed and added
        //to the styles map
        nameField = new TextField("nameFld");
        nameField.setStyle("color", "red");
        //Override the style
        nameField.setAttribute("style", "color:blue;border:1px;");
        expected = "style=\"color:blue;border:1px;\"";
        assertTrue(nameField.toString().indexOf(expected) >= 0);
        
        //Check setAttribute("style", ""). Because style value is an empty 
        //string, it should not render any key/value pairs
        nameField = new TextField("nameFld");
        nameField.setAttribute("style", "");
        expected = "style=\"\"";
        assertTrue(nameField.toString().indexOf(expected) > 0);

        //Check setAttribute("style", "color"). setAttribute will set the value
        //of style to the specified value, even if it is invalid. 
        //Here "color" is not a valid style since it does not contain any value.
        //However it is still rendered.
        nameField = new TextField("nameFld");
        nameField.setAttribute("style", "color");
        expected = "style=\"color\"";
        assertTrue(nameField.toString().indexOf(expected) > 0);
        
        //Check that setStyle will replace correctly
        nameField = new TextField("nameFld");
        nameField.setStyle("color", "red");//add color
        nameField.setStyle("color", "blue");//replace color
        expected = "style=\"color:blue;\"";
        assertTrue(nameField.toString().indexOf(expected) > 0);

        //Check that setStyle will add and remove multiple styles correctly
        nameField = new TextField("nameFld");
        nameField.setStyle("size", "3em");//add size
        nameField.setStyle("color", "red");//add color
        nameField.setStyle("color", "blue");//replace color
        nameField.setStyle("border", "1px solid black");//add border
        nameField.setStyle("color", "green");//replace color again
        expected = "style=\"size:3em;color:green;border:1px solid black;\"";        
        assertTrue(nameField.toString().indexOf(expected) > 0);

        //Check that setStyle will add and remove multiple styles correctly
        nameField = new TextField("nameFld");
        nameField.setStyle("size", "3em");//add size
        nameField.setStyle("color", "red");//add color
        nameField.setStyle("color", "blue");//replace color
        nameField.setStyle("border", "1px solid black");//add border
        nameField.setStyle("color", null);//remove color
        expected = "style=\"size:3em;border:1px solid black;\"";
        assertTrue(nameField.toString().indexOf(expected) > 0);
        
        //Check that setStyle still works when setAttribute provdes bogus style
        nameField = new TextField("nameFld");
        nameField.setAttribute("style", "color:");//add size
        nameField.setStyle("border", "1px");//add border
        expected = "style=\"border:1px;\"";
        assertTrue(nameField.toString().indexOf(expected) > 0);
    }

    /**
     * Performance test for CLK-249.
     * Cleanup AbstractControl attributes and styles.
     * 
     * This tests the AbstractControl's style and attribute API set.
     * 
     * This test ran in [5328 ms]
     * 
     * System used: Dual core, 2.16GHz, 2.00GB RAM, Windows XP
     */
    public void testAttributesAndStylesPerf() {
        MockContext.initContext();
        long start = System.currentTimeMillis();
        TextField nameField = null;
        
        //100000 iterations ran in
        //    1890ms for setStyle
        for (int i = 0; i < 100000; i++) {
            nameField = new TextField("nameFld");
            nameField.setStyle("size", "3em");//add size
            nameField.setStyle("color", "red");//add color
            nameField.setStyle("color", "blue");//replace color
            nameField.setStyle("border", "1px solid black");//add border
            nameField.toString();
        }
        System.out.println("Time for setStyle -> " + 
            (System.currentTimeMillis() - start));
        
        //100000 iterations ran in
        //  906ms for setAttribute
        start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            nameField = new TextField("nameFld");
            nameField.setAttribute("style", "size:3em");//add size
            nameField.setAttribute("style", "size:3em;color:red;");//add color
            nameField.setAttribute("style", "size:3em;color:blue");//replace color
            nameField.setAttribute("style", "size:3em;color:blue;border:1px solid black");//add border
            nameField.toString();
        }
        
        System.out.println("Time for setAttribute-> " + 
            (System.currentTimeMillis() - start));

        //100000 iterations ran in
        //  1704ms for setStyle and getStyle
        start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            nameField = new TextField("nameFld");
            nameField.setStyle("size", "3em");//add size
            nameField.setStyle("color", "red");//add color
            nameField.setStyle("color", "blue");//replace color
            nameField.getStyle("color");//get color
            nameField.getStyle("size");//get size
            nameField.toString();            
        }
        System.out.println("Time for setStyle and getStyle -> " + 
            (System.currentTimeMillis() - start));

        //100000 iterations ran in
        //  828ms for setAttribute and getAttribute
        start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            nameField = new TextField("nameFld");
            nameField.setAttribute("style", "size:3em");//add size
            nameField.getAttribute("style");//get style
            nameField.setAttribute("style", "size:3em;color:red;");//add color
            nameField.getAttribute("style");//get style
            nameField.toString();
        }
        System.out.println("Time for setAttribute and getAttribute -> " + 
            (System.currentTimeMillis() - start));
    }

    /**
     * Test AbstractControl class attributes.
     * 
     * This tests the AbstractControl's class attribute API set.
     */
    public void testClassAttributes() {
        MockContext.initContext();

        //Check that multiple class attributes are rendered as expected
        TextField nameField = new TextField("nameFld");
        nameField.addStyleClass("red");
        nameField.addStyleClass("border");
        String expected = "class=\"red border\"";
        //Assert that the expected string was generated by the field
        assertTrue(nameField.toString().indexOf(expected) >= 0);
        
        //Check that setAttribute("class", null) will remove all class 
        //attributes
        nameField = new TextField("nameFld");
        nameField.addStyleClass("blue");
        nameField.addStyleClass("red");
        nameField.setAttribute("class", null);
        assertFalse(nameField.hasAttributes());
        
        //Check that addStyleClass() will ignore duplicate class attributes
        nameField = new TextField("nameFld");
        nameField.addStyleClass("red");
        nameField.addStyleClass("red");
        expected = "class=\"red\"";
        assertTrue(nameField.toString().indexOf(expected) >= 0);

        //Check that removeStyleClass("red") will remove the "red" class attr
        nameField = new TextField("nameFld");
        nameField.addStyleClass("red");
        nameField.removeStyleClass("red");
        assertFalse(nameField.hasAttributes());

        //Check that setAttribute("class", "red") will be replace the current
        //class attributes
        nameField = new TextField("nameFld");
        nameField.addStyleClass("red");
        //Replace the styleClass
        nameField.setAttribute("class", "blue");
        expected = "class=\"blue\"";
        assertTrue(nameField.toString().indexOf(expected) >= 0);

        //Check setAttribute("class", ""). Because class value is an empty 
        //string, it should not render any value.
        nameField = new TextField("nameFld");
        nameField.setAttribute("class", "");
        expected = "class=\"\"";
        assertTrue(nameField.toString().indexOf(expected) > 0);

        //Check that addStyleClass(null) will ignore the null value.
        nameField = new TextField("nameFld");
        nameField.addStyleClass(null);
        assertFalse(nameField.hasAttributes());
        
        //Check that removeStyleClass will remove all duplicate class values 
        //as well
        nameField = new TextField("nameFld");
        nameField.setAttribute("class", "red red");//set duplicate class values
        //Confirm that duplicate values have been set
        expected = "class=\"red red\"";
        assertTrue(nameField.toString().indexOf(expected) > 0);        
        nameField.removeStyleClass("red");//remove the value
        //Confirm that the two duplicate values have been removed
        assertFalse(nameField.hasAttributes());

        //Check that addStyleClass and removeStyleClass will add and remove 
        //multiple class values correctly
        nameField = new TextField("nameFld");
        nameField.addStyleClass("red");
        nameField.addStyleClass("green");
        nameField.addStyleClass("blue");
        nameField.removeStyleClass("red");
        expected = "class=\"green blue\"";
        assertTrue(nameField.toString().indexOf(expected) > 0);

        //Check that addStyleClass add to existing value set by setAttribute
        nameField = new TextField("nameFld");
        nameField.setAttribute("class", "red");
        nameField.addStyleClass("blue");
        expected = "class=\"red blue\"";
        assertTrue(nameField.toString().indexOf(expected) > 0);

        //Check that removeStyleClass removes values set by setAttribute
        nameField = new TextField("nameFld");
        nameField.setAttribute("class", "red");
        nameField.removeStyleClass("red");
        assertFalse(nameField.hasAttributes());
}    
    /**
     * Performance test for AbstractControl#addStyleClass(String) and 
     * AbstractControl#removeStyleClass(String).
     * 
     * This test ran in [3156 ms]
     * 
     * System used: Dual core, 2.16GHz, 2.00GB RAM, Windows XP
     */
    public void testAttributesAndClassPerf() {
        MockContext.initContext();
        long start = System.currentTimeMillis();
        TextField nameField = null;

        //100000 iterations ran in
        //  1953ms for addStyleClass and removeStyleClass
        for (int i = 0; i < 100000; i++) {
            nameField = new TextField("nameFld");
            nameField.addStyleClass("hidden");
            nameField.addStyleClass("display-all");
            nameField.removeStyleClass("hidden");
            nameField.addStyleClass("hidden");
            nameField.toString();
        }
        System.out.println("Time for addStyleClass -> " + 
            (System.currentTimeMillis() - start));

        //100000 iterations ran in
        //  1203ms for setAttribute and getAttribute
        start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            nameField = new TextField("nameFld");
            nameField.setAttribute("class", "hidden");
            nameField.setAttribute("class", "hidden display-all");
            nameField.setAttribute("class", "display-all");
            nameField.setAttribute("class", "display-all hidden");
            nameField.toString();
        }

        System.out.println("Time for setAttribute-> " + 
            (System.currentTimeMillis() - start));
    }

    /**
     * Test for CLK-233.
     * 
     * Double "class" attributes are rendered in HTML for Field controls if 
     * validation fails.
     */
    public void testDoubleClassAttributes() {
        MockContext.initContext();

        TextField nameField = new TextField("nameFld");
        nameField.setError("dummy");
        nameField.setAttribute("class", "myClass");
        String expected = "class=\"myClass error\"";
        assertTrue(nameField.toString().indexOf(expected) > 0);
    }
}
