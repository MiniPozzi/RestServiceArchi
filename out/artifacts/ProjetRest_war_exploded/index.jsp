<%--
  Created by IntelliJ IDEA.
  User: Pozzi
  Date: 20/05/2019
  Time: 20:18
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>$Title$</title>
  </head>
  <body>
  $END$
  <a href="rest/files/get">Download Text File</a>

  <h2>File Upload Example</h2>
  <form action="rest/files/upload" method="post" enctype="multipart/form-data">
    <p>
      Select a file : <input type="file" name="file" size="45" />
    </p>
    <input type="submit" value="Upload File" />
  </form>

  </body>
</html>
