package acavailhez.optget;

import acavailhez.optget.wraps.MapOptGet;
import com.google.gson.Gson;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class ListOptTests extends AbstractTests {
    @Test
    public void testIndices() throws Exception {
        Map json = new Gson().fromJson("""
                {
                    a: [
                      "0",
                      {b:1.0},
                      2,
                      {c:"test"},
                      {d:["e", "f", "g"]}, 
                      ["h", "i", "j"]
                    ]
                }
                """, Map.class);

        MapOptGet map = new MapOptGet(json);

        Assert.assertEquals("0", map.get("a.0"));
        Assert.assertEquals(1.0, map.get("a.1.b"));
        Assert.assertEquals(2, map.getInt("a.2"));
        Assert.assertEquals("test", map.get("a.3.c"));
        Assert.assertEquals("f", map.get("a.4.d.1"));
        Assert.assertEquals("j", map.get("a.5.2"));
    }

    @Test
    public void testSubOptGet() throws Exception {
        Map json = new Gson().fromJson("""
                {
                    a: [
                      {b:{c:1}},
                      {d:{e:2}}
                    ]
                }
                """, Map.class);

        MapOptGet map = new MapOptGet(json);

        Assert.assertEquals(1, map.getListOfOptGet("a").get(0).getInt("b.c"));
    }

}
