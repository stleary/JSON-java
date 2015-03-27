package org.json.junit;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
@RunWith(Suite.class)
@Suite.SuiteClasses({
   CDLTest.class,
   CookieTest.class,
   PropertyTest.class,
   XMLTest.class,
   JSONMLTest.class
})
public class JunitTestSuite {   
}  