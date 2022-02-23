package ballboy.model.entities.utilities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Vector2DTest {
    @Test
    void test1()
    {
        assertEquals(50, new Vector2D(50,20).getX(),0.02);
        assertEquals(20, new Vector2D(50,20).getY(),0.02);

        Vector2D v =  new Vector2D(50,20);
        v = v.setX(30);
        v = v.setY(50);
        assertEquals(30, v.getX(), 0.02);
        assertEquals(50, v.getY(), 0.02);


        assertTrue(v.isRightOf(10));
        assertTrue(v.isLeftOf(60));

        assertFalse(v.isRightOf(60));
        assertFalse(v.isLeftOf(10));

        v = v.reflectX();
        assertEquals(-30, v.getX(), 0.02);

        v = v.reflectY();
        assertEquals(-50, v.getY(), 0.02);

        v = v.addX(30);
        v = v.addY(50);

        assertEquals(0, v.getX(), 0.02);
        assertEquals(0, v.getY(), 0.02);

        v = v.add(new Vector2D(10,10));

        assertEquals(10, v.getX(), 0.02);
        assertEquals(10, v.getY(), 0.02);

        v  = v.scale(2);

        assertEquals(20, v.getX(), 0.02);
        assertEquals(20, v.getY(), 0.02);


        assertTrue(v.isBelow(1));
        assertTrue(v.isAbove(500));

        assertFalse(v.isBelow(500));
        assertFalse(v.isAbove(1));


        Vector2D copy = (Vector2D) v.clone();

        assertEquals(20, v.getX(), 0.02);
        assertEquals(20, v.getY(), 0.02);

    }

}