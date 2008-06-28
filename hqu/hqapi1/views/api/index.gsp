<html>

    <h2>Hyperic HQ API Version <%= plugin.descriptor.get('plugin.version') %></h2>

    <%
        def xsdUrl = "/" + urlFor(asset:'HQApi1.xsd')
        def javadocUrl = "/" + urlFor(asset:'javadoc') + "/index.html"
        def apijarUrl = "/" + urlFor(asset:'hqapi1.jar')
    %>

    <p>
        The Javadoc for this API can be found <a href="${javadocUrl}">here</a>
    </p>

    <p>
        The Java API can be found <a href="${apijarUrl}">here</a>
    </p>

    <p>
        The XSD defining the Hyperic HQ web service can be found <a href="${xsdUrl}">here</a>
    </p>
</html>