<%@ taglib prefix="aida" uri="http://aida.freehep.org/jsp20" %>


<aida:tree storeName="${rootDataURI}" storeType="root"/>
<aida:objects storeName="${rootDataURI}" var="objs" path="/" />

<aida:plotter>
	<aida:region x="0" y="0" width=".5" height="1">
		<aida:plot var="${objs[0]}"/>
	</aida:region>

	<aida:region x=".5" y="0" width=".5" height=".5">
		<aida:plot var="${objs[1]}"/>
	</aida:region>

	<aida:region x=".5" y=".5" width=".5" height=".5">
		<aida:plot var="${objs[2]}"/>
	</aida:region>
</aida:plotter>
