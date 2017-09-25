<%@ page errorPage="../../../../ErrorPage.jsp" %>

<jsp:useBean id="gismapPortlet" scope="session" class="fr.paris.lutece.plugins.directory.modules.gismap.web.portlet.GismapDirectoryPortletJspBean" />
<% 
	gismapPortlet.init( request, fr.paris.lutece.plugins.gismap.web.GismapJspBean.RIGHT_MANAGE_GISMAP);
    response.sendRedirect( gismapPortlet.doCreate( request ) );
%>
