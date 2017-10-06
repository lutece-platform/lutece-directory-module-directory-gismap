/*
 * Copyright (c) 2002-2013, Mairie de Paris
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
package fr.paris.lutece.plugins.directory.modules.gismap.business;

import fr.paris.lutece.plugins.directory.business.EntryTypeGeolocation;
import fr.paris.lutece.plugins.directory.business.Field;
import fr.paris.lutece.plugins.directory.business.FieldHome;
import fr.paris.lutece.plugins.directory.business.IEntry;
import fr.paris.lutece.plugins.directory.business.Record;
import fr.paris.lutece.plugins.directory.business.RecordField;
import fr.paris.lutece.plugins.directory.business.RecordFieldFilter;
import fr.paris.lutece.plugins.directory.business.RecordFieldHome;
import fr.paris.lutece.plugins.directory.business.RecordHome;
import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

import org.apache.commons.lang.StringUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class RecordsResource
{

	private static final String GISMAP_VIEW = "gismap.view.";
	private static final String PARAMETER = ".parameter.";
	private static final String POPUPSHOWLINK = "Popup_ShowLink";
	private static final String POPUP1 = "Popup1";
	private static final String RMMSHOWCENTROID = "RenderMapManagement.ShowCentroid";
	private static final String URL_DIRECTORY_RECORD_DETAIL = "jsp/admin/plugins/directory/DoVisualisationRecord.jsp?";
	private static final String PARAMETER_DIRECTORY_RECORD_ID = "id_directory_record=";
	private static final String PARAMETER_DIRECTORY_ID = "id_directory=";
    
    
    protected static final Plugin _directoryPlugin = DirectoryUtils.getPlugin( );
    
    
    private static boolean inProperties( String[] properties, String strProperty )
    {
        boolean find = false;

        for ( int i = 0; !find && ( i < properties.length ); i++ )
        {
            find = properties[i].compareTo( "'" + strProperty + "'" ) == 0;
        }

        return find;
    }


    public static String getLink( Record record )
    {
    	String strLink = StringUtils.EMPTY;
    	Integer idDirectoryRecord = record.getIdRecord(  );
    	Integer idDirectory = record.getDirectory(  ).getIdDirectory(  );
    	if ( idDirectoryRecord != null && idDirectory != null)
    	{
    		strLink = URL_DIRECTORY_RECORD_DETAIL + PARAMETER_DIRECTORY_RECORD_ID + idDirectoryRecord + "&" + PARAMETER_DIRECTORY_ID + idDirectory;
    	}
    	return strLink;
    }

 
    public static String getBestProperties( String strProperties ) // ['nom', 'prenom', 'link']
    {
        String strPropertiesReturn = strProperties.replace( "[", "" ).replace( "]", "" );

        return strPropertiesReturn;
    }

    
    
	/** Top-level method preparing the input parameters for building the GeoJSON webservice response. 
     * 
     * @param query The object containing query parameters
     * 
     * @return
     */
    public static String treatListRecordWS( RecordsResourceQuery query)
    {
    	String strIdEntryGeolocation = StringUtils.EMPTY;
    	String strIdDirectory = StringUtils.EMPTY;
    	String strIdRecordTab = StringUtils.EMPTY;
    	List<Record> listRecord = null;
    	Integer nIdGeolocationEntry = null;
    	
    	strIdEntryGeolocation = query.getIdGeolocationEntry();
    	strIdDirectory = query.getIdDirectory();
    	strIdRecordTab = query.getListIdRecord();
    	 if ( StringUtils.isNotEmpty( strIdRecordTab ) )
    			 {
    		 listRecord = getRecordListFromStringParam( strIdRecordTab );
    			 }
    	 else
    	 {
    		if ( StringUtils.isNotEmpty( strIdDirectory ) )
    		{
    			listRecord = getRecordListFromDirectoryId( strIdDirectory );
    		}
    	 }
    	 
    	 if (StringUtils.isNotEmpty( strIdEntryGeolocation ) )
    	 {
    		 nIdGeolocationEntry = Integer.parseInt( strIdEntryGeolocation );
    	 }
    	
    	return treatListRecordWS( nIdGeolocationEntry, listRecord);
    }
    
    
	/** Utility method to retrieve the full Record list from a comma-separated String
	 * 
	 * @param strIdRecordTab The comma-separated list of Record identifiers 
	 * 
	 * @return List of Records
	 */
    private static List<Record> getRecordListFromStringParam(String strIdRecordTab)
    {
    	List<Record> listRecord = new ArrayList<Record>( );
    	String[] idRecordTab = strIdRecordTab.split(",");
    	
    	for ( int i = 0; i < idRecordTab.length; i++ )
        {
        Integer nIdRecord = Integer.parseInt( idRecordTab[i] );
        Record record = RecordHome.findByPrimaryKey( nIdRecord, _directoryPlugin );
        listRecord.add( record );
        }

		return listRecord;
	}


	/** Utility method to retrieve the full Record list of a given directory
	 * 
	 * @param strIdDirectory The directory identifier 
	 * 
	 * @return List of Records
	 */
	private static List<Record> getRecordListFromDirectoryId(String strIdDirectory)
	{
		List<Record> listRecord = null;
		Integer nIdDirectory = Integer.parseInt( strIdDirectory );
		RecordFieldFilter filter = new RecordFieldFilter( );
		filter.setIdDirectory(nIdDirectory);
		
		listRecord = RecordHome.getListRecord(filter, _directoryPlugin);
		
		return listRecord;
	}


	/** Build the GeoJSON webservice response from a list of Record identifiers and a geolocation Entry identifier.
     * See GEOJSON specification: https://tools.ietf.org/html/rfc7946
     * 
     * @param listRecords The list of Record identifiers
     * 
     * @param nIdEntryGeolocation The Geolocation Entry identifier
     * 
     * @return The geoJSON response as a String
     */
    public static String treatListRecordWS( Integer nIdEntryGeolocation, List<Record> listRecords)
    {  	
    	 	
    	String strViewNumberValue = "";
        String strRMMSHOWCENTROIDProperty;
    	String strPopupShowLinkProperty;
    	String strPopup1Property;
    	
        if ( listRecords != null && nIdEntryGeolocation != null)
        {
        	
	        strViewNumberValue = getViewNumber(nIdEntryGeolocation);    	
	        strRMMSHOWCENTROIDProperty = getShowCentroidProperty(strViewNumberValue);    	
	        strPopupShowLinkProperty = getPopupShowLinkProperty(strViewNumberValue);
	        strPopup1Property = getPopup1Property(strViewNumberValue);
	
	        String[] strPopup1PropertyArray = strPopup1Property.split( "," );
	        String[] strProperties = null;
	
	        if ( strPopup1PropertyArray.length > 1 )
	        {
	            strProperties = getBestProperties( strPopup1PropertyArray[1] ).split( ";" );
	        }
	        
	        JSONObject collection = new JSONObject(  );
	        collection.accumulate( "type", "FeatureCollection" );
	
	        JSONArray array = new JSONArray(  );


            for ( Record record : listRecords )
            {
                String strRecordFieldGeometry = "";
                String strRecordFieldX = "";
                String strRecordFieldY = "";
                JSONObject properties = new JSONObject(  );
            	

                Integer nIdRecord = record.getIdRecord( );
                
                RecordFieldFilter rfFilter = new RecordFieldFilter( );
                rfFilter.setIdRecord( nIdRecord ); 
                List<RecordField> listRecordFields = RecordFieldHome.getRecordFieldList(rfFilter, _directoryPlugin);
                
                for (RecordField recordField : listRecordFields )
                {
                	 if ( recordField != null )
                     {
                		 
                         Field field = recordField.getField(  );

                         if ( field != null )
                		 	{
	                    	 	if ( field.getTitle(  ).compareTo( EntryTypeGeolocation.CONSTANT_GEOMETRY ) == 0 ) 
			                         {
			                             strRecordFieldGeometry = recordField.getValue(  );
			                         }
	                    	 	if ( field.getTitle(  ).compareTo( EntryTypeGeolocation.CONSTANT_X ) == 0 )
			                         {
			                             strRecordFieldX = recordField.getValue(  );
			                         }
	                    	 	if ( field.getTitle(  ).compareTo( EntryTypeGeolocation.CONSTANT_Y ) == 0 )
			                         {
			                             strRecordFieldY = recordField.getValue(  );
			                         }
	                         
                    		 }
                         IEntry entry = recordField.getEntry(  );

                         if ( inProperties( strProperties, entry.getTitle(  ) ) )
                         {
                             properties.accumulate( entry.getTitle(  ), recordField.getValue(  ) );
                         }
                     }
                }

                JSONObject jsonElement = new JSONObject(  );
                jsonElement.accumulate( "type", "Feature" );
                jsonElement.accumulate( "id", nIdRecord );
              
                if ( strPopupShowLinkProperty.compareTo( "true" ) == 0 )
                    {
                        properties.accumulate( "link", getLink( record ) );
                    }
    
                properties = (properties.size( )== 0 ) ? null : properties;
                jsonElement.accumulate( "properties",properties );


                jsonElement.accumulate( "geometry", getGeometry( strRMMSHOWCENTROIDProperty, strRecordFieldGeometry, strRecordFieldX, strRecordFieldY ) );

                array.add( jsonElement );
              }

	        if ( array.size(  ) > 0 )
	        {
	            collection.accumulate( "features", array );
	        }
	        
	        return  collection.toString(  );
        }
        else
        {
        	return StringUtils.EMPTY;
        }
    }


	private static String getViewNumber(Integer nIdEntryGeolocation)
	{
		
		String strViewNumberValue = StringUtils.EMPTY;
		if ( nIdEntryGeolocation != null )
        {
            List<Field> fieldList = FieldHome.getFieldListByIdEntry( nIdEntryGeolocation,
                    _directoryPlugin );
            for ( Field field : fieldList )
            {
                if ( ( field != null ) && ( field.getTitle(  ) != null ) &&
                        ( field.getTitle(  ).compareTo( EntryTypeGeolocation.CONSTANT_VIEW_NUMBER_GES ) == 0 ) )
                {
                    strViewNumberValue = field.getValue(  );
                    break;
                }
            }
        }
		return strViewNumberValue;
	}

    
	/** Retrieve the popup1 property value from the provided gismap view number
	 * 
	 * @param strViewNumberValue
	 */
	private static String getPopup1Property(String strViewNumberValue) {
		String strPopup1Property = AppPropertiesService.getProperty( GISMAP_VIEW + strViewNumberValue + PARAMETER + POPUP1 );

        if ( strPopup1Property == null )
        {
            strPopup1Property = "";
            AppLogService.info( "Could not found the " + GISMAP_VIEW + strViewNumberValue + PARAMETER + POPUP1 +
                " property in the property file. Set to empty string" );
        }
        return strPopup1Property;
	}

	/** Retrieve the popupShowLink property value from the provided gismap view number
	 * 
	 * @param strViewNumberValue
	 */
	private static String getPopupShowLinkProperty(String strViewNumberValue) {
		String strPopupShowLinkProperty = AppPropertiesService.getProperty( GISMAP_VIEW + strViewNumberValue + PARAMETER +
                POPUPSHOWLINK );

        if ( strPopupShowLinkProperty == null )
        {
            strPopupShowLinkProperty = Boolean.FALSE.toString(  );
            AppLogService.info( "Could not found the " + GISMAP_VIEW + strViewNumberValue + PARAMETER + POPUPSHOWLINK +
                " property in the property file. Set to " + Boolean.FALSE.toString(  ) );
        }
        return strPopupShowLinkProperty;
	}

	/** Retrieve the ShowCentroid property value from the provided gismap view number
	 * 
	 * @param strViewNumberValue
	 */
	private static String getShowCentroidProperty(String strViewNumberValue) {
		String strRMMSHOWCENTROIDProperty = AppPropertiesService.getProperty( GISMAP_VIEW + strViewNumberValue +
                PARAMETER + RMMSHOWCENTROID );
    	
    	 if ( strRMMSHOWCENTROIDProperty == null )
         {
    		 strRMMSHOWCENTROIDProperty = Boolean.FALSE.toString(  );
             AppLogService.info( "Could not found the " + GISMAP_VIEW + strViewNumberValue + PARAMETER + RMMSHOWCENTROID +
                 " property in the property file. Set to " + Boolean.FALSE.toString(  ) );
         }
    	 return strRMMSHOWCENTROIDProperty;
	}

    
	/** Build the geometry Geojson node for a feature
	 * 
	 * @param strRecordFieldGeometry
	 * @param strRecordFieldX
	 * @param strRecordFieldY
	 * 
	 * @return the geometry node as a string
	 */
	private static String getGeometry(String strRMMSHOWCENTROIDProperty, String strRecordFieldGeometry, String strRecordFieldX, String strRecordFieldY) {
		
		String strGeometry;
        if ( ( strRMMSHOWCENTROIDProperty != null ) &&
                ( strRMMSHOWCENTROIDProperty.compareTo( "true" ) == 0 ) )
        {
            JSONObject jsonCoordinates = new JSONObject(  );
            if ( !strRecordFieldX.isEmpty(  ) && !strRecordFieldY.isEmpty(  ) )
            {
                jsonCoordinates.accumulate( "type", "Point" );
                jsonCoordinates.accumulate( "coordinates", "[" + strRecordFieldX + "," + strRecordFieldY + "]" );
            }
        	
            strGeometry = jsonCoordinates.toString(  ) ;
        }
        else
        {
        	strGeometry =  StringUtils.isBlank( strRecordFieldGeometry ) ? null : strRecordFieldGeometry ;

        }
		return strGeometry;
	}

    
}
