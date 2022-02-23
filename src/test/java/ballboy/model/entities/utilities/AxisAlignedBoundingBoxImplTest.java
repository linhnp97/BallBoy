package ballboy.model.entities.utilities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AxisAlignedBoundingBoxImplTest {
    @Test
    void Test(){
        AxisAlignedBoundingBox a = new AxisAlignedBoundingBoxImpl(new Vector2D(0,0), 100, 120);
        assertEquals(100, a.getHeight(), 0.01);
        assertEquals(120, a.getWidth(), 0.01);

        assertEquals(0, a.getLeftX());
        assertEquals(120, a.getRightX());

        assertEquals(0, a.getTopY());
        assertEquals(100, a.getBottomY());

        Vector2D v = new Vector2D(100, 100);

        a.setTopLeft(v);
        assertEquals(100, a.getLeftX());
        assertEquals(100,  a.getTopY());

        assertTrue(a.containsPoint(new Vector2D(150, 150)));
        assertFalse(a.containsPoint(new Vector2D(700, 700)));

        AxisAlignedBoundingBox hit = new AxisAlignedBoundingBoxImpl(new Vector2D(105, 107), 100, 100);
        AxisAlignedBoundingBox notHit = new AxisAlignedBoundingBoxImpl(new Vector2D(1000, 1000), 100, 100);
        assertTrue(a.collidesWith(hit));
        assertFalse(a.collidesWith(notHit));

        AxisAlignedBoundingBox copy = hit.copy();

        assertEquals(107,  copy.getTopY(), 0.01);
        assertEquals(100, copy.getWidth(), 0.01);








    }

}