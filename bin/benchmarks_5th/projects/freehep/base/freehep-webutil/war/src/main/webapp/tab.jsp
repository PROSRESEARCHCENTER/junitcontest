<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
        <link type="text/css" rel="stylesheet" href="tabs.css">
    </head>
    <body>

    <h2> This is to test that only the selected tab body is executed </h2>
      
         
         
       
<a name="ProcessTabs">
</a>
<div style="padding:0px;border-color: white; border-style: solid; border-width: 1px; clear: both;">
	<ul style="list-style: none;padding: 0px; padding-left: 10px;clear: both;margin: 0;">
		<li style="background-color: #d1fae7; margin: 0 3px 0 0;padding: 0;background-repeat: no-repeat;float: left;background-position: top right;background-image: url(/freehep-webutil-web-1.1-SNAPSHOT/tabCornerServlet.jsp?color=0xd1fae7&bkgColor=white&type=1);">
			<a href="http://localhost:8080/freehep-webutil-web-1.1-SNAPSHOT/tab.jsp?process=A#ProcessTabs" style="width: auto; text-decoration: none;white-space: nowrap;display: block;background-repeat: no-repeat;padding: 5px 15px 5px;float: left;font-family: verdana, arial, sans-serif;color: black;background-position: top left;background-image: url(/freehep-webutil-web-1.1-SNAPSHOT/tabCornerServlet.jsp?color=0xd1fae7&bkgColor=white&type=0);">A
</a>
</li>

		<li class="selected" style="background-color: #a2d7c8; margin: 0 3px 0 0;padding: 0;background-repeat: no-repeat;float: left;background-position: top right;background-image: url(/freehep-webutil-web-1.1-SNAPSHOT/tabCornerServlet.jsp?color=0xa2d7c8&bkgColor=white&type=1);">
			<a href="http://localhost:8080/freehep-webutil-web-1.1-SNAPSHOT/tab.jsp?process=B#ProcessTabs" style="width: auto; text-decoration: none;white-space: nowrap;display: block;background-repeat: no-repeat;padding: 5px 15px 5px;float: left;font-family: verdana, arial, sans-serif;color: black;font-weight: bold;;background-position: top left;background-image: url(/freehep-webutil-web-1.1-SNAPSHOT/tabCornerServlet.jsp?color=0xa2d7c8&bkgColor=white&type=0);">B
</a>
</li>
</ul>
</div>

<div style="padding:0px;border-color: #a2d7c8; border-style: solid; border-width: 1px; clear: both;" class="freehepTabBody"> B

</div>
       <p> The value here : <b>B</b> should be identical to the selected tab's body. </p>

    
    </body>
</html>
