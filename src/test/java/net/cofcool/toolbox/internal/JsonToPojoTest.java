package net.cofcool.toolbox.internal;

import net.cofcool.toolbox.Tool;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JsonToPojoTest {

    @BeforeEach
    void setup() {
        System.setProperty("logging.debug", "true");
    }

    @Test
    void run() throws Exception {
        new JsonToPojo().run(
                new Tool.Args()
                        .arg("json", """
                                {
                                    "str": "strVal",
                                    "obj": {
                                        "objKey0": 12313,
                                        "objKey1": "objVal1"
                                    },
                                    "arrObj": [
                                        {"arrObjKey0": "arrObjVal0", "arrObjKey01": 573, "arrObjKey02": false},
                                        {"arrObjKey1": "arrObjVal1", "arrObjKey11": 141, "arrObjKey12": true}
                                    ],
                                    "boolStr": true,
                                    "arrStr": ["arrStr1", "arrStr2", "arrStr3"],
                                    "arrNum": [123, 321, 111]
                                }
                                """)
                        .arg("out", "./target/pojo/run")
                        .arg("pkg", "json.demo")
        );
    }
}