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
package fr.paris.lutece.plugins.directory.modules.gismap.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.directory.modules.gismap.business.DirectoryGismapSourceQuery;
import fr.paris.lutece.plugins.directory.modules.gismap.utils.GismapDirectoryUtils;
import fr.paris.lutece.plugins.gismap.business.MapParameter;
import fr.paris.lutece.plugins.gismap.business.View;
import fr.paris.lutece.plugins.gismap.business.ViewHome;
import fr.paris.lutece.portal.web.l10n.LocaleService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.html.HtmlTemplate;

import java.util.Locale;

/**
 *
 * GismapService
 *
 */
public class GismapDirectoryService
{
	private static final String PARAM_ID_DIRECTORY = "idDirectory";

	public static final String   GISMAP_DEFAULT_VIEW_PROPERTIES = "gismap.mainmap.defaultview";

	// Markers
	public static final String   GISMAP_VIEW_INIT               = "gismap.view.init";
	private static final String  PARAMETER_MAP_PARAMETER        = "map_parameter";
	private static final String  PARAMETER_ADD_PARAMETER        = "add_parameter";
	private static final String  PARAMETER_DEFAULT_VIEW         = "default_view";

	// Templates
	private static GismapDirectoryService _singleton                     = new GismapDirectoryService( );

	// Constant
	public static final String   GISMAP_URL_REST                = "rest/directory-gismap/listRecord";
	public static final String   PARAM_VIEW_URLGEOJSON1          = "UrlGeoJSON1";
	public static final String   PARAM_VIEW_GEOJSON1            = "GeoJSON1";
	public static final String   PARAM_VIEW_THEMATICSIMPLE1     = "ThematicSimple1";
	public static final String   PARAM_VIEW_POPUP1              = "Popup1";
	public static final String   PARAM_VIEW_URLGEOJSON          = "UrlGeoJSON";
	public static final String   PARAM_VIEW_GEOJSON            = "GeoJSON";
	public static final String   PARAM_VIEW_THEMATICSIMPLE     = "ThematicSimple";
	public static final String   PARAM_VIEW_POPUP              = "Popup";
	public static final String   PARAM_VIEW_SHOWLINK            = "Popup_ShowLink";
	public static final String   PARAM_ID_GEOLOCATION_ENTRY     = "idGeolocationEntry";
	public static final String	 PARAM_GEOJSONINDEX 			= "geoJsonIndex";
	public static final String 	 PARAM_VIEW 					= "view";

	//MESSAGES
    private static final String UNAVAILABILITY_MESSAGE = "module.directory.gismap.message.portlet.unavailable.view.misconfiguration";


	/**
	 * Initialize the GISMAP service
	 *
	 */
	public void init( )
	{
		// TODO
	}

	/**
	 * Returns the instance of the singleton
	 *
	 * @return The instance of the singleton
	 */
	public static GismapDirectoryService getInstance( )
	{
		return _singleton;
	}

	
	public String getMapTemplateWithDirectoryGismapSources( HttpServletRequest request, String viewId, List<DirectoryGismapSourceQuery> listGeojsonSources )
	{
		Map<String, Object> model = new HashMap<>( );

		if ( StringUtils.isEmpty( viewId ) )
		{
			return "<span>" + I18nService.getLocalizedString( UNAVAILABILITY_MESSAGE, Locale.FRENCH ) + "</span>";
		}

		String strDefaultView = AppPropertiesService.getProperty( GISMAP_DEFAULT_VIEW_PROPERTIES , "0" );

		View view = ViewHome.findByPrimaryKey( Integer.parseInt( viewId ) );

		MapParameter tmp = view.getMapParameter( );
		
		String strWSUrl = AppPathService.getBaseUrl( request ).concat( GISMAP_URL_REST );

		for ( DirectoryGismapSourceQuery geojsonSource : listGeojsonSources)
		{
			String strIndex = String.valueOf( geojsonSource.getGeoJsonIndex( ) );
			String strIdDirectory = String.valueOf( geojsonSource.getIdDirectory( ) );
			String strIdEntryGeolocation = String.valueOf( geojsonSource.getIdGeolocationEntry( ) );
			String strView = StringUtils.isEmpty( geojsonSource.getView( ) ) ? strDefaultView : geojsonSource.getView( );
			
			String strWSUrlWithParams = UriBuilder.fromUri(strWSUrl)
					.queryParam( PARAM_ID_GEOLOCATION_ENTRY, strIdEntryGeolocation )
					.queryParam( PARAM_ID_DIRECTORY, strIdDirectory )
					.queryParam( PARAM_GEOJSONINDEX,  strIndex )
					.queryParam( PARAM_VIEW, strView )
							.build( ).toString( );

			tmp.setParameters( PARAM_VIEW_URLGEOJSON.concat( strIndex ), "'" + strWSUrlWithParams+ "'" );
		}
		
		view.setMapParameter( tmp );

		model.put( PARAMETER_MAP_PARAMETER, view.getMapParameter( ) );
		model.put( PARAMETER_ADD_PARAMETER, view.getAddressParam( ) );
		model.put( PARAMETER_DEFAULT_VIEW, strDefaultView );

		Locale locale = ( request == null ) ? LocaleService.getDefault( ) : request.getLocale( );
		HtmlTemplate templateList = AppTemplateService.getTemplate( view.getMapTemplateFile( ), locale, model );

		return templateList.getHtml( );
	}

}
