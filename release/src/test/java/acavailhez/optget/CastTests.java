package acavailhez.optget;

import acavailhez.optget.casts.CastMode;
import acavailhez.optget.casts.IntegerCast;
import org.junit.Assert;
import org.junit.Test;

public class CastTests extends AbstractTests {
    @Test
    public void testInteger() throws Exception {
        IntegerCast castor = new IntegerCast();
        Integer ONE = Integer.valueOf(1);
        Assert.assertEquals(ONE, castor.cast(1));
        Assert.assertEquals(ONE, castor.cast("1"));
        Assert.assertEquals(ONE, castor.cast("   1"));

        Assert.assertEquals(ONE, castor.cast("1.0", CastMode.UNSAFE_BEST_EFFORT));
        Assert.assertEquals(ONE, castor.cast("1.9", CastMode.UNSAFE_BEST_EFFORT));
    }


}
