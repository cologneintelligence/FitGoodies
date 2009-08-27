You must have several additional libraries in lib/ directory of the project which are not checked in.

To compile add:
--------------------------------------------------------------------
fit.jar                      | http://fit.c2.com
log4j.jar (version 1.2)      | http://logging.apache.org/log4j
bcel-5.2.jar                 | http://jakarta.apache.org/bcel
mailapi.jar (version 1.4.2)  | http://java.sun.com/products/javamail

For junit-tests, you also need:
--------------------------------------------------------------------
junit-3.8.2.jar              | http://www.junit.org
jmock-2.5.1.jar              | http://jmock.org
jmock-junit3-2.5.1.jar       | http://jmock.org
jmock-legacy-2.5.1.jar       | http://jmock.org
hamcrest-core-1.1.jar        | http://code.google.com/p/hamcrest
hamcrest-library-1.1.jar     | http://code.google.com/p/hamcrest
cglib-nodep-2.2.jar          | http://cglib.sourceforge.net
objenesis-1.0.jar            | http://code.google.com/p/objenesis

For generating the docs, you need:
--------------------------------------------------------------------
doccheck.jar (version 1.2b2) | http://java.sun.com/j2se/javadoc/doccheck

And finally, to run the demo, also you need:
--------------------------------------------------------------------
derby.jar                    | http://developers.sun.com/javadb
(tested with 10.5.1.1; note: this is included in java windows installations by default)


last updated 2009-08-27
 