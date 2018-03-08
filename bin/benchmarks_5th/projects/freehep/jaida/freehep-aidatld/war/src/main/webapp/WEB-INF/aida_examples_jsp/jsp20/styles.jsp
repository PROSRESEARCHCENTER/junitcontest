<%@ taglib prefix="aida" uri="http://aida.freehep.org/jsp20" %>


<aida:tree storeName="${rootDataURI}" storeType="root"/>
<aida:objects storeName="${rootDataURI}" var="objs" path="/h110" />

<aida:plotter>
    <aida:region>
        
        <!-- Set IPlotterStyle for the region here -->
        <aida:style> 
            <aida:style type="dataBox" >
                <aida:style type="background" >
                    <aida:attribute name="color" value="#FFFFE8"/>
                </aida:style>
            </aida:style>
            <aida:style type="statisticsBox">
                <aida:attribute name="isVisible" value="true"/>
            </aida:style>
            <aida:style type="legendBox">
                <aida:attribute name="isVisible" value="true"/>
            </aida:style>
            <aida:style type="xAxis">
                <aida:attribute name="label" value="x-axis"/>
            </aida:style>
            <aida:style type="yAxis">
                <aida:attribute name="scale" value="log"/>
                <aida:attribute name="label" value="y-axis"/>
            </aida:style>
        </aida:style>
        
        
        <aida:plot var="${objs[0]}">

            <!-- 
                Set IPlotterStyle for individual plot here,
                note the type of the top style here
            -->
            <aida:style type="plotter">
                <aida:style type="data">
                    <aida:style type="errorBar">
                        <aida:attribute name="isVisible" value="true"/>
                        <aida:attribute name="color" value="blue"/>
                    </aida:style>
                    <aida:style type="fill">
                        <aida:attribute name="isVisible" value="false"/>
                    </aida:style>
                    <aida:style type="line">
                        <aida:attribute name="isVisible" value="true"/>
                        <aida:attribute name="color" value="blue"/>
                    </aida:style>
                </aida:style>
            </aida:style>
        </aida:plot>
        
        
    </aida:region>
</aida:plotter>
