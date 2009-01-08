<script type="text/javascript">
    document.navTabCat = "Admin";
</script>
<html>
    <%
        def version = plugin.descriptor.get('plugin.version')
        def clientPackage = "hqapi1-" + version + ".tar.gz"
        def apijarUrl = "/" + urlFor(asset:clientPackage)
    %>

    <h2>Hyperic HQ API Version ${version}</h2>

    This page provides resources and documentation for the Hyperic HQ API.

    <ul>
        <li>
            Download <a href="${apijarUrl}">${clientPackage}</a>
        </li>
    </ul>
   
</html>