package ballboy.model.entities.utilities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProjectileKinematicsUtilTest {
    @Test
    void Test(){

        assertEquals(10, ProjectileKinematicsUtil.getDeltaToMaxHeight(10, 5),0.02);
        assertEquals(10, ProjectileKinematicsUtil.getCurrentVelocityForMaxHeight(10, 5),0.02);
    }
}