/*
 * Copyright (c) 2002-2014, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.directory.modules.gismap.web.portlet;

import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.directory.business.DirectoryFilter;
import fr.paris.lutece.plugins.directory.business.DirectoryHome;
import fr.paris.lutece.plugins.directory.business.EntryFilter;
import fr.paris.lutece.plugins.directory.business.EntryHome;
import fr.paris.lutece.plugins.directory.business.EntryTypeGeolocation;
import fr.paris.lutece.plugins.directory.business.Field;
import fr.paris.lutece.plugins.directory.business.FieldHome;
import fr.paris.lutece.plugins.directory.business.IEntry;
import fr.paris.lutece.plugins.directory.business.Directory;
import fr.paris.lutece.plugins.directory.modules.gismap.business.portlet.GismapDirectoryPortlet;
import fr.paris.lutece.plugins.directory.modules.gismap.business.portlet.GismapDirectoryPortletHome;
import fr.paris.lutece.plugins.directory.modules.gismap.service.GismapProvider;
import fr.paris.lutece.plugins.directory.service.DirectoryPlugin;
import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.plugins.gismap.web.GismapJspBean;
import fr.paris.lutece.portal.business.portlet.PortletHome;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.web.portlet.PortletJspBean;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;

/**
 * This class provides the user interface to manage view Portlet
 */
public class GismapDirectoryPortletJspBean extends PortletJspBean
{
    // //////////////////////////////////////////////////////////////////////////
    // Constants
    /**
     * Right to manage gismap
     */
    public static final String  RIGHT_MANAGE_GISMAP                 = GismapJspBean.RIGHT_MANAGE_GISMAP;

    private static final long   serialVersionUID                    = -2619049973871862337L;
    private static final String MARK_DIRECTORY_LIST                 = "directory_list";
    private static final String MARK_DIRECTORY_ID                 = "directory_id";
    private static final String PARAMETER_ID_DIRECTORY              = "id_directory";
    private static final String MESSAGE_YOU_MUST_CHOOSE_A_DIRECTORY = "module.directory.gismap.message.mandatory.directory";

   
    private static final String JSP_ADMIN_SITE = "../../../../site/AdminSite.jsp";

	private static final String TYPE_GEOLOC_CLASSNAME = EntryTypeGeolocation.class.getName( );

	private static final Object GISMAPPROVIDER_CLASSNAME = GismapProvider.class.getName( );

	private static final String MESSAGE_ERROR_PLUGIN_DIRECTORY_DISABLED = "module.directory.gismap.message.plugin.directory.disabled";

	private static final String MESSAGE_DIRECTORY_GEOLOCATION_MISCONFIG = "module.directory.gismap.message.directory.geolocation_misconfiguration";
    
    // //////////////////////////////////////////////////////////////////////////
    // Class attributes
    
    private static Plugin _directoryPlugin = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );
    private static boolean _bDirectoryAvailable = PluginService.isPluginEnable( DirectoryPlugin.PLUGIN_NAME  );

    /**
     * Returns the Download portlet creation view
     *
     * @param request The HTTP request
     * @return The HTML view
     */
    @Override
    public String getCreate( HttpServletRequest request )
    {
    	if (!_bDirectoryAvailable)
    	{
    		return AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_PLUGIN_DIRECTORY_DISABLED,
                    AdminMessage.TYPE_STOP );
    	}
        HashMap<String, Object> model = new HashMap<>( );
        String strIdPage = request.getParameter( PARAMETER_PAGE_ID );
        String strIdPortletType = request.getParameter( PARAMETER_PORTLET_TYPE_ID );
        		
        model.put( MARK_DIRECTORY_LIST, GetDirectoriesWithGeolocation( ) );
        model.put( MARK_DIRECTORY_ID, 1);
        HtmlTemplate template = getCreateTemplate( strIdPage, strIdPortletType, model );

        return template.getHtml( );
    }


	/**
     * Returns the Download portlet modification view
     *
     * @param request The Http request
     * @return The HTML view
     */
    @Override
    public String getModify( HttpServletRequest request )
    {
    	if (!_bDirectoryAvailable)
    	{
    		return AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_PLUGIN_DIRECTORY_DISABLED,
                    AdminMessage.TYPE_STOP );
    	}
        HashMap<String, Object> model = new HashMap<>( );
        String strPortletId = request.getParameter( PARAMETER_PORTLET_ID );
        int nPortletId = -1;
        int nDirectoryId = -1;

        try
        {
            nPortletId = Integer.parseInt( strPortletId );
        } catch ( NumberFormatException ne )
        {
            AppLogService.error( ne );
        }

        GismapDirectoryPortlet portlet = ( GismapDirectoryPortlet ) PortletHome.findByPrimaryKey( nPortletId );
        nDirectoryId = portlet.getDirectoryId( );
        
        model.put( MARK_DIRECTORY_LIST, GetDirectoriesWithGeolocation( ) );
        model.put( MARK_DIRECTORY_ID, nDirectoryId);

        HtmlTemplate template = getModifyTemplate( portlet, model );

        return template.getHtml( );
    }

    /**
     * Process portlet's creation
     *
     * @param request The Http request
     * @return The Jsp management URL of the process result
     */
    @Override
    public String doCreate( HttpServletRequest request )
    {
        GismapDirectoryPortlet portlet = new GismapDirectoryPortlet( );
        String strPageId = request.getParameter( PARAMETER_PAGE_ID );
        String strDirectoryId = request.getParameter( PARAMETER_ID_DIRECTORY );
        int nPageId = -1;
        int nDirectoryId = -1;

        // get portlet common attributes
        String strErrorUrl = setPortletCommonData( request, portlet );

        try
        {
            nPageId = Integer.parseInt( strPageId );
            nDirectoryId = Integer.parseInt( strDirectoryId );
        } catch ( NumberFormatException ne )
        {
            AppLogService.error( ne );
        }

        if ( ( strErrorUrl == null ) && ( nDirectoryId == -1 ) )
        {
            strErrorUrl = AdminMessageService.getMessageUrl( request, MESSAGE_YOU_MUST_CHOOSE_A_DIRECTORY, AdminMessage.TYPE_STOP );
        }

        if ( strErrorUrl != null )
        {
            return strErrorUrl;
        }

        if (!CheckGeolocationParams( nDirectoryId ) )
        {
        	return AdminMessageService.getMessageUrl( request, MESSAGE_DIRECTORY_GEOLOCATION_MISCONFIG, AdminMessage.TYPE_STOP );
        }
        
        portlet.setPageId( nPageId );
        portlet.setDirectoryId( nDirectoryId );

        // Creating portlet
        GismapDirectoryPortletHome.getInstance( ).create( portlet );

        // Displays the page with the new Portlet
        return getPageUrl( nPageId );
    }

    /**
     * Process portlet's modification
     *
     * @param request The http request
     * @return Management's Url
     */
    @Override
    public String doModify( HttpServletRequest request )
    {
        // recovers portlet attributes
        String strPortletId = request.getParameter( PARAMETER_PORTLET_ID );
        String strDirectoryId = request.getParameter( PARAMETER_ID_DIRECTORY );
        int nPortletId = -1;
        int nDirectoryId = -1;

        try
        {
            nPortletId = Integer.parseInt( strPortletId );
            nDirectoryId = Integer.parseInt( strDirectoryId );
        } catch ( NumberFormatException ne )
        {
            AppLogService.error( ne );
        }

        GismapDirectoryPortlet portlet = ( GismapDirectoryPortlet ) PortletHome.findByPrimaryKey( nPortletId );

        // retrieve portlet common attributes
        String strErrorUrl = setPortletCommonData( request, portlet );

        if ( ( strErrorUrl == null ) && ( nDirectoryId == -1 ) )
        {
            strErrorUrl = AdminMessageService.getMessageUrl( request, MESSAGE_YOU_MUST_CHOOSE_A_DIRECTORY, AdminMessage.TYPE_STOP );
        }

        if ( strErrorUrl != null )
        {
            return strErrorUrl;
        }

        if (!CheckGeolocationParams( nDirectoryId ) )
        {
        	return AdminMessageService.getMessageUrl( request, MESSAGE_DIRECTORY_GEOLOCATION_MISCONFIG, AdminMessage.TYPE_STOP );
        }
        portlet.setDirectoryId( nDirectoryId );
        // updates the portlet
        portlet.update( );

        // displays the page withe the potlet updated
        return getPageUrl( portlet.getPageId( ) );
    }
    
    
	/**
     * Check for missing View configuration inside Geolocation fields of the provided directory identifier.
     * Return true if check passed
     * 
     * @param nDirectoryId
     *            Directory identifier
     * @return True if view configuration is OK for this directory
     */
    private boolean CheckGeolocationParams(int nDirectoryId) {

    	boolean bViewsConfigured = true;
    	
    	EntryFilter filterEntry = new EntryFilter( );
    	filterEntry.setIsComment( EntryFilter.FILTER_FALSE );
    	filterEntry.setIsGroup( EntryFilter.FILTER_FALSE );
    	filterEntry.setIdDirectory( nDirectoryId );
        List<IEntry> listEntry = EntryHome.getEntryList( filterEntry, _directoryPlugin );
    	
        for (IEntry entry : listEntry)
        {
        	if (entry.getEntryType( ).getClassName().equals(  TYPE_GEOLOC_CLASSNAME ) )
        	{

        		 List<Field> fieldList = FieldHome.getFieldListByIdEntry( entry.getIdEntry(  ),
                         DirectoryUtils.getPlugin(  ) );

                 for ( Field field : fieldList )
                 {
                     if ( ( field != null ) && ( field.getTitle(  ) != null ) &&
                             ( field.getTitle(  ).equals(EntryTypeGeolocation.CONSTANT_VIEW_NUMBER_GES ) ) )
                     {
                         if ( StringUtils.isEmpty(field.getValue(  ) ) )
                        		 {
                        	 return false;
                        		 };
                     }
                 }

        	}
        }    	
		return bViewsConfigured;
	}


	/**
     * Gets the page URL with relative path
     * 
     * @param nIdPage
     *            Page ID
     * @return The page URL
     */
    @Override
    protected String getPageUrl( int nIdPage )
    {
        return JSP_ADMIN_SITE + "?" + PARAMETER_PAGE_ID + "=" + nIdPage;
    }
    
    /**
     * Fetch all Enabled Directories containing geolocation entries with gismap as MapProvider
     * and return them as a referenceList
     * 
     * @return The Directory reference List
     */
    private ReferenceList GetDirectoriesWithGeolocation( )
    {
    	ReferenceList refDirectory = new ReferenceList( );
    	
    	DirectoryFilter filter = new DirectoryFilter( );
    	filter.setIsDisabled( DirectoryFilter.FILTER_TRUE );    	
        List<Directory> directoryList = DirectoryHome.getDirectoryList(filter, PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME ) );
        
    	EntryFilter filterEntry = new EntryFilter( );
    	filterEntry.setIsComment( EntryFilter.FILTER_FALSE );
    	filterEntry.setIsGroup( EntryFilter.FILTER_FALSE );
        
    	
        for (Directory directory : directoryList)
        {
      	
        	filterEntry.setIdDirectory( directory.getIdDirectory( )  );
            List<IEntry> listEntry = EntryHome.getEntryList( filterEntry, _directoryPlugin );
        	
            for (IEntry entry : listEntry)
            {
            	if (entry.getEntryType( ).getClassName().equals( TYPE_GEOLOC_CLASSNAME ) )
            	{

            		if (entry.getMapProvider( )!= null && entry.getMapProvider( ).getClass( ).getName( ).equals( GISMAPPROVIDER_CLASSNAME)  )
            		{
                		refDirectory.addItem( directory.getIdDirectory( ), directory.getTitle( ) );
                		break;
            		}

            	}
            }
        	
        }
    	
		return refDirectory;
	}
    
}
