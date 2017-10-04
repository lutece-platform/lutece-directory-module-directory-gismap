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

import fr.paris.lutece.plugins.directory.business.Entry;
import fr.paris.lutece.plugins.directory.business.EntryFilter;
import fr.paris.lutece.plugins.directory.business.EntryHome;
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;


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
    
    
    protected static final Plugin _DirectoryPlugin = DirectoryUtils.getPlugin( );
    
    private static final String PROPERTY_ENTRY_TYPE_GEOLOCATION = "directory.entry_type.geolocation";
    
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

    
    
    /** Build the GeoJSON webservice response from a list of RecordField identifiers.
     * See GEOJSON specification: https://tools.ietf.org/html/rfc7946
     * 
     * TODO: change the method signature in order to handle a geolocation Entry id and a list of Records Ids as input parameters
     *  
     * @param strListId The list of RecordField identifiers separated by commas
     * 
     * @return The geoJSON response as a String
     */
    public static String treatListRecordFieldsWS( String strListId )
    {
    	
    	Integer[] idRecordTab = getRecordListFromRecordFieldList ( strListId);
    	if ( idRecordTab.length == 0 )
    	{
    		return StringUtils.EMPTY;
    	}
    	
    	// TODO : Handle several Geolocation entries inside a directory
    	// and pass the idEntryGeolocation as WS input parameter
    	Integer idEntryGeolocation = getGeolocationEntry ( idRecordTab[0] );
    	String strViewNumberValue = "";
        String strRMMSHOWCENTROIDProperty;
    	String strPopupShowLinkProperty;
    	String strPopup1Property;
    	
        if ( idEntryGeolocation != null )
        {
            List<Field> fieldList = FieldHome.getFieldListByIdEntry( idEntryGeolocation,
                    _DirectoryPlugin );
            for ( Field field : fieldList )
            {
                if ( ( field != null ) && ( field.getTitle(  ) != null ) &&
                        ( field.getTitle(  ).compareTo( "viewNumberGes" ) == 0 ) )
                {
                    strViewNumberValue = field.getValue(  );
                    break;
                }
            }
        }    	
        strRMMSHOWCENTROIDProperty = getShowCentroidProperty(strViewNumberValue);    	
        strPopupShowLinkProperty = getPopupShowLinkProperty(strViewNumberValue);
        strPopup1Property = getPopup1Property(strViewNumberValue);

        String[] strrPopup1PropertyArray = strPopup1Property.split( "," );
        String[] strProperties = null;

        if ( strrPopup1PropertyArray.length > 1 )
        {
            strProperties = getBestProperties( strrPopup1PropertyArray[1] ).split( ";" );
        }
        
        JSONObject collection = new JSONObject(  );
        collection.accumulate( "type", "FeatureCollection" );

        JSONArray array = new JSONArray(  );

        if ( idRecordTab != null )
        {
            for ( int i = 0; i < idRecordTab.length; i++ )
            {
                String strRecordFieldGeometry = "";
                String strRecordFieldX = "";
                String strRecordFieldY = "";
                JSONObject properties = new JSONObject(  );
            	
                int nIdRecord = idRecordTab[i];
                Record record = RecordHome.findByPrimaryKey( nIdRecord, _DirectoryPlugin );
                
                RecordFieldFilter rfFilter = new RecordFieldFilter( );
                rfFilter.setIdRecord( nIdRecord ); 
                List<RecordField> listRecordFields = RecordFieldHome.getRecordFieldList(rfFilter, _DirectoryPlugin);
                
                for (RecordField recordField : listRecordFields )
                {
                	 if ( recordField != null )
                     {
                		 
                         Field field = recordField.getField(  );

                         if ( field != null )
                		 	{
	                    	 	if ( field.getTitle(  ).compareTo( "geometry" ) == 0 ) 
			                         {
			                             strRecordFieldGeometry = recordField.getValue(  );
			                         }
	                    	 	if ( field.getTitle(  ).compareTo( "X" ) == 0 )
			                         {
			                             strRecordFieldX = recordField.getValue(  );
			                         }
	                    	 	if ( field.getTitle(  ).compareTo( "Y" ) == 0 )
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
        }

        return  collection.toString(  );
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

	/**
	 * Return the Identifier of the first Geolocation Entry inside a given DirectoryRecord
	 * 
	 * TODO : Remove this method and change the WS input parameter instead
	 * 
	 * @param identifier of DirectoryRecord
	 * 
	 * @return the geolocation entry identifier
	 */
	private static Integer getGeolocationEntry(Integer nIdDirectoryRecord) {
		Record record = RecordHome.findByPrimaryKey(nIdDirectoryRecord, _DirectoryPlugin );
		Integer nIdDirectory = record.getDirectory( ).getIdDirectory( );
		
		  EntryFilter filterGeolocation = new EntryFilter( );
          filterGeolocation.setIdDirectory( nIdDirectory );
          filterGeolocation.setIdType( AppPropertiesService.getPropertyInt( PROPERTY_ENTRY_TYPE_GEOLOCATION, 16 ) );
          filterGeolocation.setIsShownInResultRecord( 1 );

          List<IEntry> entriesGeolocationList = EntryHome.getEntryList( filterGeolocation, _DirectoryPlugin );
          Entry geolocEntry = (Entry) entriesGeolocationList.get( 0 );
          if (geolocEntry != null )
          {
        	  return geolocEntry.getIdEntry( );
          }

		return null;
	}

	/** Return an array of unique Record identifiers built from a list of RecordFields
	 * 
	 *  TODO : Remove this method and change the WS input parameter instead
	 *  
	 * @param strListIdRecordFields
	 * 
	 * @return array of Record Ids
	 */
	private static Integer[] getRecordListFromRecordFieldList(String strListIdRecordFields) {
		String[] strListIdTab = ( strListIdRecordFields != null ) ? strListIdRecordFields.split( "," ) : null;
		Set<Integer> hsRecordIdList = new HashSet<>();

		for ( int i = 0; i < strListIdTab.length; i++ )
		{
			int nIdRecordField = Integer.parseInt( strListIdTab[i] );
			RecordField recordField = RecordFieldHome.findByPrimaryKey(nIdRecordField, _DirectoryPlugin);
			hsRecordIdList.add( recordField.getRecord().getIdRecord() );
		}
		Integer[] recordIdTab = hsRecordIdList.toArray( new Integer[hsRecordIdList.size( )]) ;
		return recordIdTab;
	}
    
}
