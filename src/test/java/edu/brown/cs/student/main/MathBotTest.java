package edu.brown.cs.student.main;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MathBotTest {

  @Test
  public void testAddition() {
    MathBot matherator9000 = new MathBot();
    double output = matherator9000.add(10.5, 3);
    assertEquals(13.5, output, 0.01);
  }

  @Test
  public void testLargerNumbers() {
    MathBot matherator9001 = new MathBot();
    double output = matherator9001.add(100000, 200303);
    assertEquals(300303, output, 0.01);
  }

  @Test
  public void testSubtraction() {
    MathBot matherator9002 = new MathBot();
    double output = matherator9002.subtract(18, 17);
    assertEquals(1, output, 0.01);
  }

  // TODO: add more unit tests of your own
  @Test
  public void testZeros() {
    MathBot matherator9003 = new MathBot();
    double outputAdd = matherator9003.add(0,0);
    double outputSub = matherator9003.subtract(0,0);
    assertEquals(0,outputAdd,0.01);
    assertEquals(0,outputSub,0.01);
  }

  @Test
  public void testNegatives() {
    MathBot matherator9004 = new MathBot();
    double outputAdd1 = matherator9004.add(-4.2,-2.7);
    double outputAdd2 = matherator9004.add(0,-5.7);
    double outputAdd3 = matherator9004.add(-5.7,2.3);
    double outputSubtract1 = matherator9004.subtract(-4.2,-2.7);
    double outputSubtract2 = matherator9004.subtract(0,-5.7);
    double outputSubtract3 = matherator9004.subtract(-5.7,2.3);
    assertEquals(-6.9,outputAdd1,0.01);
    assertEquals(-5.7,outputAdd2,0.01);
    assertEquals(-3.4,outputAdd3,0.01);
    assertEquals(-1.5,outputSubtract1,0.01);
    assertEquals(5.7,outputSubtract2,0.01);
    assertEquals(-8.0,outputSubtract3,0.01);
  }
}
