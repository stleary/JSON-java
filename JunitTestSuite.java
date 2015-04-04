package org.json.junit;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
@RunWith(Suite.class)
@Suite.SuiteClasses({
   CDLTest.class,
   CookieTest.class,
   CookieListTest.class,
   PropertyTest.class,
   XMLTest.class,
   JSONMLTest.class,
   HTTPTest.class
})
public class JunitTestSuite {   
}  