package acavailhez.optget;

import acavailhez.optget.wraps.MapOptGet;
import com.google.gson.Gson;
import org.junit.Assert;
import org.junit.Test;

import java.io.Serializable;
import java.lang.constant.Constable;
import java.util.*;

public class GetOptTests extends AbstractTests {
    @Test
    public void testSimple() throws Exception {
        Map json = new Gson().fromJson("""
                {
                    a: 1.0,
                    b: "string",
                    d: {
                        da: 1.0,
                        db: {
                            dba: "two"
                        }
                    },
                    e: [1.0, 2.0],
                    f: "12"
                }
                                """, Map.class);

        json.put("c", Locale.FRENCH);
        MapOptGet map = new MapOptGet(json);

        assert map.get("a").equals(1.0d);
        assert map.getInteger("a").equals(1);
        assert map.get("b").equals("string");
        assert map.get("c").equals(Locale.FRENCH);
        assert map.get("d.da").equals(1.0d);
        assert map.get("d.db.dba").equals("two");

        // cast
        Assert.assertEquals(map.get("a", String.class), "1.0");
        assert map.get("e", List.class).get(0).equals(1.0d);
        assert map.get("f", Long.class) == 12L;

        // opt
        assert map.opt("z") == null;
//        assert map.opt("a.b") == null;
//        assert map.opt("d.da.da") == null;
        assert map.opt("d.db.dba").equals("two");
        assert map.optString("d.db.dba").equals("two");

        // default
        assert map.opt("z", String.class, "test").equals("test");
    }

    @Test
    public void list() {
        LinkedHashMap<String, List<Integer>> map = new LinkedHashMap<String, List<Integer>>(1);
        map.put("a", new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5)));
        MapOptGet optget = new MapOptGet(map);

        List<Long> list = optget.getListLong("a");
        assert list.size() == 5;
        assert list.get(2).equals(3L);
        assert list.get(2).getClass().equals(Long.class);

        List<Integer> list2 = optget.getListInteger("a");
        assert list2.size() == 5;
        assert list2.get(2).equals(3);
        assert list2.get(2).getClass().equals(Integer.class);

        List<String> list3 = optget.getListString("a");
        assert list3.size() == 5;
        assert list3.get(2).equals("3");

    }

    @Test
    public void listWithCast() {
        LinkedHashMap<String, List<Serializable>> map = new LinkedHashMap<String, List<Serializable>>(1);
        map.put("a", new ArrayList<Serializable>(Arrays.asList("1", "2", "3", 4, 5)));
        MapOptGet optget = new MapOptGet(map);

        List<Integer> list2 = optget.getListInteger("a");
        assert list2.size() == 5;
        assert list2.get(2).equals(3);
        assert list2.get(2).getClass().equals(Integer.class);

    }

    @Test
    public void listWithEnumCast() {
        LinkedHashMap<String, List<Constable>> map = new LinkedHashMap<String, List<Constable>>(1);
        map.put("a", new ArrayList<Constable>(Arrays.asList(Bootstrap4Color.DANGER, "PRIMARY")));
        MapOptGet optget = new MapOptGet(map);

        List<Bootstrap4Color> list2 = optget.getListEnum("a", Bootstrap4Color.class);
        assert list2.size() == 2;
        assert list2.get(0).equals(Bootstrap4Color.DANGER);
        assert list2.get(1).equals(Bootstrap4Color.PRIMARY);

    }

    @Test
    public void map() {
        LinkedHashMap<String, LinkedHashMap<Integer, String>> map1 = new LinkedHashMap<String, LinkedHashMap<Integer, String>>(1);
        LinkedHashMap<Integer, String> map5 = new LinkedHashMap<Integer, String>(3);
        map5.put(1, "one");
        map5.put(2, "two");
        map5.put(3, "three");
        map1.put("a", map5);
        MapOptGet optget = new MapOptGet(map1);

        Map<Integer, String> map = optget.getMapIntegerString("a");
        assert map.size() == 3;
        assert map.get(2).equals("two");

        Map<String, String> map2 = optget.getMapStringString("a");
        assert map2.size() == 3;
        assert map2.get("2").equals("two");

        OptGet map3 = optget.getOptGet("a");
        assert map3.getString(1).equals("one");


        OptGet map4 = optget.getOptGet("a");
        assert map4.getString(1).equals("one");
    }

    @Test
    public void cast() {

        Assert.assertEquals("test", CastUtils.cast("test", String.class));
        Assert.assertEquals("1", CastUtils.cast(1, String.class));

        Assert.assertEquals(1, (int) CastUtils.cast(1, Integer.class));
        Assert.assertEquals(1, (int) CastUtils.cast("1", Integer.class));
        Assert.assertEquals(1, (int) CastUtils.cast(1L, Integer.class));

        Assert.assertEquals(Bootstrap4Color.DANGER, CastUtils.cast(Bootstrap4Color.DANGER, Bootstrap4Color.class));
        Assert.assertEquals(Bootstrap4Color.DANGER, CastUtils.cast("DANGER", Bootstrap4Color.class));
        Assert.assertEquals(Bootstrap4Color.DANGER, CastUtils.cast("danger", Bootstrap4Color.class));

    }


    @Test
    public void errorKeyNotFound() {
        // Error is informative
        MapOptGet map = new MapOptGet(new Gson().fromJson("""
                {
                    d: {
                        db: {
                            dba: "two"
                        }
                    }
                }
                                """, Map.class));
        try {
            map.getInteger("d.db.dba");
        } catch (Throwable t) {
            System.out.println(t);
        }
    }

    @Test
    public void errorKeyNull() {
        // Error is informative
        MapOptGet map = new MapOptGet(new Gson().fromJson("""
                {
                    d: {
                        db: {
                            dba: null
                        }
                    }
                }
                                """, Map.class));
        try {
            map.getInteger("d.db.dba");
        } catch (Throwable t) {
            System.out.println(t);
        }
    }

}
