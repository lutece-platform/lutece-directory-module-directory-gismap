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

import java.util.ArrayList;
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
import fr.paris.lutece.plugins.directory.modules.gismap.business.DirectoryGismapSourceQuery;
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
import fr.paris.lutece.portal.service.util.AppPropertiesService;
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
    private static final String PARAMETER_CLOSE_PAGE				= "close";

   
    private static final String JSP_ADMIN_SITE = "../../../../site/AdminSite.jsp";

	private static final String TYPE_GEOLOC_CLASSNAME = EntryTypeGeolocation.class.getName( );

	private static final Object GISMAPPROVIDER_CLASSNAME = GismapProvider.class.getName( );

	private static final String MESSAGE_ERROR_PLUGIN_DIRECTORY_DISABLED = "module.directory.gismap.message.plugin.directory.disabled";

	private static final String MESSAGE_DIRECTORY_GEOLOCATION_MISCONFIG = "module.directory.gismap.message.directory.geolocation_misconfiguration";

	private static final String MESSAGE_YOU_MUST_ENTER_VIEW = "module.directory.gismap.message.mandatory.view";
	
	private static final String MARK_MAP_VIEW_ID = "map_view_id";

	private static final String MARK_MAP_SOURCE_LIST = "map_source_list";
	private static final String MARK_MAX_LAYER_NUMBER = "maxLayerNumber";

	private static final String PARAMETER_MAP_VIEW_ID = "map_view_id";
	private static final String PARAMETER_GEOJSON = "GeoJSON";



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
        model.put( MARK_MAP_VIEW_ID, StringUtils.EMPTY );
        
        String strMaxLayerGeojson = AppPropertiesService.getProperty( GismapDirectoryPortlet.MAX_LAYER_GEOJSON_PROPERTY, "1" );
        int nMaxLayerGeojson = Integer.parseInt( strMaxLayerGeojson );        
        
        model.put( MARK_MAX_LAYER_NUMBER, nMaxLayerGeojson);
        for (int i =0; i<=nMaxLayerGeojson; i++ )
        {  
        	model.put( PARAMETER_GEOJSON + String.valueOf( i ), "0" );
        }  
        
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
        int nViewId = 0;

        try
        {
            nPortletId = Integer.parseInt( strPortletId );
        } catch ( NumberFormatException ne )
        {
            AppLogService.error( ne );
        }

        GismapDirectoryPortlet portlet = ( GismapDirectoryPortlet ) PortletHome.findByPrimaryKey( nPortletId );
        nViewId = portlet.getView( );
        List<DirectoryGismapSourceQuery> listPortletSources = portlet.getListMapSource( );
        
        model.put( MARK_DIRECTORY_LIST, GetDirectoriesWithGeolocation( ) );
        model.put( MARK_MAP_VIEW_ID, nViewId);
        
        String strMaxLayerGeojson = AppPropertiesService.getProperty( GismapDirectoryPortlet.MAX_LAYER_GEOJSON_PROPERTY, "1" );
        int nMaxLayerGeojson = Integer.parseInt( strMaxLayerGeojson );        
        
        model.put( MARK_MAX_LAYER_NUMBER, nMaxLayerGeojson);
        boolean bfound;
        
        for (int i =1; i<=nMaxLayerGeojson; i++ )
        {  
        	bfound = false;
        	
        	for ( DirectoryGismapSourceQuery portletSource : listPortletSources)
        	{
        		if ( portletSource.getGeoJsonIndex( ) == i)
        		{
        			bfound = true;
        			String strMapSourceKey = String.valueOf( portletSource.getIdDirectory( ) )
        					.concat( "-" ).concat( String.valueOf( portletSource.getIdGeolocationEntry( ) ) );
        			model.put( PARAMETER_GEOJSON + String.valueOf( i ), strMapSourceKey );
        			
        		}
        	}
        	if ( !bfound )
        	{
        		model.put( PARAMETER_GEOJSON + String.valueOf( i ), "0" );
        	}        		        	
        }  
        

        HtmlTemplate template = getModifyTemplate( portlet, model );

        return template.getHtml( );
    }

    private void removeMapSourceFromPortlet(int nPortletId, String directoryMapSourceToRemove) {
		// TODO Auto-generated method stub
		
	}


	private void addMapSourceToPortlet(int nPortletId, List<DirectoryGismapSourceQuery> listMapSources) {
		// TODO Auto-generated method stub
		
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
        
        //String strView = AppPropertiesService.getProperty( GismapDirectoryPortlet.GISMAP_DEFAULT_VIEW_PROPERTIES );
        String strView = request.getParameter( PARAMETER_MAP_VIEW_ID );
        
        int nPageId = -1;
        int nView = -1;
        List<DirectoryGismapSourceQuery> listDirectoryGismapSource = new ArrayList<DirectoryGismapSourceQuery>( );

        // get portlet common attributes
        String strErrorUrl = setPortletCommonData( request, portlet );

        
        if ( ( strErrorUrl == null ) && ( StringUtils.isEmpty( strView ) ) )
        {
            strErrorUrl = AdminMessageService.getMessageUrl( request, MESSAGE_YOU_MUST_ENTER_VIEW, AdminMessage.TYPE_STOP );
        }

        if ( strErrorUrl != null )
        {
            return strErrorUrl;
        }

        try
        {
            nPageId = Integer.parseInt( strPageId );
            nView = Integer.parseInt( strView );
        } catch ( NumberFormatException ne )
        {
            AppLogService.error( ne );
        }
        
        /*
        if (!CheckGeolocationParams( nDirectoryId ) )
        {
        	return AdminMessageService.getMessageUrl( request, MESSAGE_DIRECTORY_GEOLOCATION_MISCONFIG, AdminMessage.TYPE_STOP );
        }
        */
        
        portlet.setPageId( nPageId );
        //portlet.setDirectoryId( nDirectoryId );
        portlet.setView(nView);

        
        String strMaxLayerGeojson = AppPropertiesService.getProperty( GismapDirectoryPortlet.MAX_LAYER_GEOJSON_PROPERTY, "1" );
        int nMaxLayerGeojson = Integer.parseInt( strMaxLayerGeojson );
        
        for (int i =0; i<=nMaxLayerGeojson; i++ )
        {  
        	String directoryMapSourceToAdd = request.getParameter( PARAMETER_GEOJSON + String.valueOf( i ) );
	        if ( StringUtils.isNotEmpty( directoryMapSourceToAdd ) && !directoryMapSourceToAdd.equals( "0" ) )
	        {
	        	try
	        	{
	        	String strDirectoryId = directoryMapSourceToAdd.split("-")[0];
	        	int nDirectoryId = Integer.parseInt( strDirectoryId );
	        	String strGeolocationEntryId = directoryMapSourceToAdd.split("-")[1];
	        	int nGeolocationEntryId = Integer.parseInt( strGeolocationEntryId );
	        	
	        	DirectoryGismapSourceQuery directoryGismapSource = new DirectoryGismapSourceQuery( );
	        	directoryGismapSource.setIdDirectory( nDirectoryId );
	        	directoryGismapSource.setIdGeolocationEntry( nGeolocationEntryId );
	        	directoryGismapSource.setGeoJsonIndex( i );
	        	
	        	listDirectoryGismapSource.add( directoryGismapSource );
	        	}
	        	catch (NumberFormatException e)
	        	{
	        		AppLogService.error("An error occured while reading geojson"+i+" source parameters in GismapDirectoryPortlet creation.", e);
	        	}
	        }
        }
        
        portlet.setListMapSource( listDirectoryGismapSource );
        
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
        int nPortletId = -1;

        try
        {
            nPortletId = Integer.parseInt( strPortletId );
        } catch ( NumberFormatException ne )
        {
            AppLogService.error( ne );
        }

        GismapDirectoryPortlet portlet = ( GismapDirectoryPortlet ) PortletHome.findByPrimaryKey( nPortletId );
        
        String strPageId = request.getParameter( PARAMETER_PAGE_ID );        
        
        //String strView = AppPropertiesService.getProperty( GismapDirectoryPortlet.GISMAP_DEFAULT_VIEW_PROPERTIES );
        String strView = request.getParameter( PARAMETER_MAP_VIEW_ID );
        
        int nPageId = -1;
        int nView = -1;
        List<DirectoryGismapSourceQuery> listDirectoryGismapSource = new ArrayList<DirectoryGismapSourceQuery>( );

        // get portlet common attributes
        String strErrorUrl = setPortletCommonData( request, portlet );

        
        if ( ( strErrorUrl == null ) && ( StringUtils.isEmpty( strView ) ) )
        {
            strErrorUrl = AdminMessageService.getMessageUrl( request, MESSAGE_YOU_MUST_ENTER_VIEW, AdminMessage.TYPE_STOP );
        }

        if ( strErrorUrl != null )
        {
            return strErrorUrl;
        }

        try
        {
            nPageId = Integer.parseInt( strPageId );
            nView = Integer.parseInt( strView );
        } catch ( NumberFormatException ne )
        {
            AppLogService.error( ne );
        }
        
        /*
        if (!CheckGeolocationParams( nDirectoryId ) )
        {
        	return AdminMessageService.getMessageUrl( request, MESSAGE_DIRECTORY_GEOLOCATION_MISCONFIG, AdminMessage.TYPE_STOP );
        }
        */
        
        portlet.setPageId( nPageId );
        portlet.setView(nView);

        
        String strMaxLayerGeojson = AppPropertiesService.getProperty( GismapDirectoryPortlet.MAX_LAYER_GEOJSON_PROPERTY, "1" );
        int nMaxLayerGeojson = Integer.parseInt( strMaxLayerGeojson );
        
        for (int i =0; i<=nMaxLayerGeojson; i++ )
        {  
        	String directoryMapSourceToAdd = request.getParameter( PARAMETER_GEOJSON + String.valueOf( i ) );
	        if ( StringUtils.isNotEmpty( directoryMapSourceToAdd ) && !directoryMapSourceToAdd.equals( "0" ) )
	        {
	        	try
	        	{
	        	String strDirectoryId = directoryMapSourceToAdd.split("-")[0];
	        	int nDirectoryId = Integer.parseInt( strDirectoryId );
	        	String strGeolocationEntryId = directoryMapSourceToAdd.split("-")[1];
	        	int nGeolocationEntryId = Integer.parseInt( strGeolocationEntryId );
	        	
	        	DirectoryGismapSourceQuery directoryGismapSource = new DirectoryGismapSourceQuery( );
	        	directoryGismapSource.setIdDirectory( nDirectoryId );
	        	directoryGismapSource.setIdGeolocationEntry( nGeolocationEntryId );
	        	directoryGismapSource.setGeoJsonIndex( i );
	        	
	        	listDirectoryGismapSource.add( directoryGismapSource );
	        	}
	        	catch (NumberFormatException e)
	        	{
	        		AppLogService.error("An error occured while reading geojson"+i+" source parameters in GismapDirectoryPortlet creation.", e);
	        	}
	        }
        }
        
        portlet.setListMapSource( listDirectoryGismapSource );
        
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
    	refDirectory.addItem( "0", StringUtils.EMPTY );
    	
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
                		refDirectory.addItem( String.valueOf( directory.getIdDirectory( ) ).concat( "-" ).concat( String.valueOf( entry.getIdEntry( ) ) )
                				, directory.getTitle( ).concat( " - " ).concat( entry.getTitle( ) ) );
                		break;
            		}

            	}
            }
        	
        }
    	
		return refDirectory;
	}
    
}
