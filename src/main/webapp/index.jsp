<html>
<body>
	<p>
		<%@ page import="fr.iut.p2p.Client"%> 
   		<%  
   			Client client = new Client(InetAddress.getByName("localhost"), 5001);
			client.register();
			client.receiveUuid();
			client.initInformations();
			client.receiveList();
			out.println(client.getList());
    	%>
	</p>
	</body>
</html>
