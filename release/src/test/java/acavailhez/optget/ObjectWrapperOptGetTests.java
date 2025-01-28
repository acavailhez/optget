package acavailhez.optget;

import acavailhez.optget.casts.CastMode;
import acavailhez.optget.wraps.ObjectWrapperOptGet;
import org.junit.Test;

public class ObjectWrapperOptGetTests extends AbstractTests {
    @Test
    public void wrap() throws Exception {
        ObjectWrapperOptGet wrapper = new ObjectWrapperOptGet(new Wrapped());
        wrapper.setCastMode(CastMode.UNSAFE_BEST_EFFORT);

//        assert wrapper.get('publicField', Integer) == 10
        assert wrapper.get("publicProperty", String.class).equals("publicProperty");

        assert wrapper.opt("missing") == null;

        assert wrapper.get("nested.publicField").equals(10);

        try {
            wrapper.get("privateField");
            assert false;
        } catch (IllegalArgumentException ex) {
        }


        try {
            wrapper.get("privateProperty");
            assert false;
        } catch (IllegalArgumentException ex) {
        }

    }

    public static class Wrapped {
        public int publicField = 10;
        private int privateField = 20;

        public String getPublicProperty() {
            return "publicProperty";
        }

        private String getPrivateProperty() {
            return "privateProperty";
        }

        public Wrapped getNested() {
            return new Wrapped();
        }
    }
}
