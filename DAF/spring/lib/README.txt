About springtonucleus.jar
-------------------------

The springtonucleus.jar file contains the
atg.nucleus.spring.NucleusPublisher class. The NucleusPublisher can be
added to a spring configuration to make a spring context visible to
the Nucleus global scope.

For example, one could add:

	<bean name="/NucleusPublisher" class="atg.nucleus.spring.NucleusPublisher" singleton="true">
		<property name="nucleusPath"><value>/atg/spring/FromSpring</value></property>
        </bean>

to one's Spring configuration XML to make the Spring beans available
at "/atg/spring/FromSpring" in Nucleus. All the Spring beans would
then be available as components in the "FromSpring" context. For
example, a Spring bean named "foo" would be available from Nucleus at
"/atg/spring/FromSpring/foo".

The springtonucleus.jar is not the ATG classpath, since it needs to
be in the same class loader as the spring classes. The
springtonucleus.jar would be added to a spring-enabled web app (in
WEB-INF/lib) in a typical use case. See the ATG documentation for more
information.
