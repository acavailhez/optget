package acavailhez.optget

import org.junit.Assert
import org.junit.Test

public class GetOptTests extends AbstractTests {

    @Test
    public void testSimple() throws Exception {
        OptGetMap map = new OptGetMap([
                a: 1,
                b: "string",
                c: Locale.FRENCH,
                d: [
                        da: 1,
                        db: [
                                dba: "two"
                        ]
                ],
                e: [1, 2],
                f: "12"
        ])

        assert map.get('a') == 1
        assert map.get('b') == 'string'
        assert map.get('c') == Locale.FRENCH
        assert map.get('d.da') == 1
        assert map.get('d.db.dba') == "two"

        // cast
        assert map.get('a', String) == '1'
        assert map.get('e', List)[0] == 1
        assert map.get('f', Long) == 12L

        // opt
        assert map.opt('z') == null
        assert map.opt('a.b') == null
        assert map.opt('d.da.da') == null

        // default
        assert map.opt('z', String, "test") == "test"
    }


    @Test
    public void list() {
        OptGetMap optget = new OptGetMap([
                a: [1, 2, 3, 4, 5]
        ])

        List<Long> list = optget.getListLong('a')
        assert list.size() == 5
        assert list[2] == 3L
        assert list[2].class == Long.class

        List<Integer> list2 = optget.getListInteger('a')
        assert list2.size() == 5
        assert list2[2] == 3
        assert list2[2].class == Integer.class

        List<String> list3 = optget.getListString('a')
        assert list3.size() == 5
        assert list3[2] == "3"

    }

    @Test
    public void listWithCast() {
        OptGetMap optget = new OptGetMap([
                a: ['1', '2', '3', 4, 5]
        ])

        List<Integer> list2 = optget.getListInteger('a')
        assert list2.size() == 5
        assert list2[2] == 3
        assert list2[2].class == Integer.class

    }

    @Test
    public void listWithEnumCast() {
        OptGetMap optget = new OptGetMap([
                a: [Bootstrap4Color.DANGER, 'PRIMARY']
        ])

        List<Bootstrap4Color> list2 = optget.getListEnum('a', Bootstrap4Color)
        assert list2.size() == 2
        assert list2[0] == Bootstrap4Color.DANGER
        assert list2[1] == Bootstrap4Color.PRIMARY

    }

    @Test
    public void map() {
        OptGetMap optget = new OptGetMap([
                a: [1: 'one', 2: 'two', 3: 'three']
        ])

        Map<Integer, String> map = optget.getMapIntegerString('a')
        assert map.size() == 3
        assert map[2] == 'two'

        Map<String, String> map2 = optget.getMapStringString('a')
        assert map2.size() == 3
        assert map2['2'] == 'two'

        OptGet map3 = optget.getOptGet('a')
        assert map3.getString('1') == 'one'
        assert map3.getString(1) == 'one'


        OptGetMap map4 = optget.getOptGetMap('a')
        assert map4.getString('1') == 'one'
        assert map4.getString(1) == 'one'
    }

    @Test
    public void cast() {

        Assert.assertEquals("test", CastUtils.cast("test", String.class))
        Assert.assertEquals("1", CastUtils.cast(1, String.class))

        Assert.assertEquals(1, CastUtils.cast(1, Integer.class))
        Assert.assertEquals(1, CastUtils.cast("1", Integer.class))
        Assert.assertEquals(1, CastUtils.cast(1L, Integer.class))

        Assert.assertEquals(Bootstrap4Color.DANGER, CastUtils.cast(Bootstrap4Color.DANGER, Bootstrap4Color.class))
        Assert.assertEquals(Bootstrap4Color.DANGER, CastUtils.cast('DANGER', Bootstrap4Color.class))
        Assert.assertEquals(Bootstrap4Color.DANGER, CastUtils.cast('danger', Bootstrap4Color.class))

    }
}