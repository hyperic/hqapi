<script type="text/javascript">
    document.navTabCat = "Admin";
</script>
<html>
    <%
        def version = plugin.descriptor.get('plugin.version')
        def clientPackageName = "hqapi1-client-" + version
        def clientPackageTgz =  clientPackageName + ".tar.gz"
        def clientPackageZip =  clientPackageName + ".zip"
        def clientPackageTgzUrl = "/" + urlFor(asset:clientPackageTgz)
        def clientPackageZipUrl = "/" + urlFor(asset:clientPackageZip)        
    %>

    <h2>Hyperic HQ API Version ${version}</h2>

    This page provides resources and documentation for the Hyperic HQ API.<br>

    <h3>Client Download</h3>
    <ul>
        <li>
            Download <a href="${clientPackageTgzUrl}">${clientPackageTgz}</a>
        </li>
        <li>
            Download <a href="${clientPackageZipUrl}">${clientPackageZip}</a>
        </li>
    </ul>

    Requires a Java JRE 1.5 or newer

    <h3>Quick Help</h3>
    
    <p>HQ Api provides a java client with a command line wrapper.  To utilize HQ Api please download the 
    client package above and extract it to your local filesystem. It is not required to install HQ Api
    on the Hyperic server.  

    <p>Once downloaded it is recommended to setup a ~/.hq/client.properties or conf/client.properties file
    to contain the Hyperic server connection properties. Example contents of this file are
    
    <pre>
    host=localhost
    port=7080
    user=hqadmin
    password=hqadmin
    </pre>

    <h3>Current method statistics</h3>

    <table border="1">
        <thead>
            <tr>
                <td>Method</td><td>Total Calls</td><td>Min Time</td><td>Max Time</td><td>Total Time</td>                
            </tr>
        <%
            stats.each { k, v ->
        %>
                <tr>
                    <td>${k}</td><td>${v.calls}</td><td>${v.minTime}</td><td>${v.maxTime}</td><td>${v.totalTime}</td>
                </tr>
        <%
            }
        %>
        </thead>
    </table>
   
</html>