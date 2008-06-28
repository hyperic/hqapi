<html>

    <h2>Hyperic HQ API Version <%= plugin.descriptor.get('plugin.version') %></h2>

    <%
        def xsd = urlFor(asset:'HQApi1.xsd')
        def javadoc = urlFor(asset:'javadoc')
        def apijar = urlFor(asset:'hqapi1.jar')
    %>

    <p>
        The Javadoc for this API can be found <a href="/${javadoc}">here</a>
    </p>

    <p>
        The Java API can be found <a href="/${apijar}">here</a>
    </p>

    <p>
        The XSD defining the Hyperic HQ web service can be found <a href="/${xsd}">here</a>
    </p>
</html>