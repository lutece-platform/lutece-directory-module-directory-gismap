<%@ page errorPage="../../../../ErrorPage.jsp" %>
<jsp:include page="../../../../PortletAdminHeader.jsp" />
<jsp:useBean id="gismapPortlet" scope="session" class="fr.paris.lutece.plugins.directory.modules.gismap.web.portlet.GismapDirectoryPortletJspBean" />
<% gismapPortlet.init( request, fr.paris.lutece.plugins.gismap.web.GismapJspBean.RIGHT_MANAGE_GISMAP); %>
<%= gismapPortlet.getCreate(request) %>

<%@ include file="../../../../AdminFooter.jsp" %>