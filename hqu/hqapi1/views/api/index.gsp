<html>
    <head>
        <script type="text/javascript">
            function popup(link, name) {
                if (! window.focus) return true;
                var href;
                if (typeof(link) == 'string')
                    href = link;
                else
                    href = link.href

                window.open(href, name, 'width=800,height=600,scrollbars=yes')
                return false;
            }
        </script>
    </head>

    <h2>Hyperic HQ API Version <%= plugin.descriptor.get('plugin.version') %></h2>

    <%
        def javadocUrl = "/" + urlFor(asset:'javadoc') + "/index.html"
        def apijarUrl = "/" + urlFor(asset:'hqapi1.tar.gz')
    %>

    This page provides resources and documentation for the Hyperic HQ API.

    <ul>
        <li>
            Download <a href="${apijarUrl}">hqapi1.tar.gz</a>
        </li>
        <!-- li>
            View <a href="${javadocUrl}" onclick="return popup(this, 'Hyperic HQ Api Javadoc')">Javadoc Documentation</a> for this API.
        </li -->
    </ul>
   
</html>