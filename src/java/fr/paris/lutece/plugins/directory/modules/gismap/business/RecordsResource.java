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

import java.util.List;

import fr.paris.lutece.plugins.directory.business.Entry;
import fr.paris.lutece.plugins.directory.business.Field;
import fr.paris.lutece.plugins.directory.business.FieldHome;
import fr.paris.lutece.plugins.directory.business.IEntry;
import fr.paris.lutece.plugins.directory.business.Record;
import fr.paris.lutece.plugins.directory.business.RecordField;
import fr.paris.lutece.plugins.directory.business.RecordFieldHome;
import fr.paris.lutece.plugins.directory.business.RecordHome;
import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


@Path( "/rest/directory-gismap/" )
public class RecordsResource
{
    public static final String GISMAP_VIEW = "gismap.view.";
    public static final String PARAMETER = ".parameter.";
    public static final String POPUPSHOWLINK = "Popup_ShowLink";
    public static final String POPUP1 = "Popup1";
    public static final String RMMSHOWCENTROID = "RenderMapManagement.ShowCentroid";

    @GET
    @Path( "listRecordField/{listId}" )
    @Produces( MediaType.APPLICATION_JSON )
    public String getListRecordFieldGetMehod( @PathParam( "listId" ) String strListId )
    {
        return treatListRecordWS( strListId );
    }

    @POST
    @Path( "listRecordField/post" )
    @Produces( MediaType.APPLICATION_JSON )
    public String getListRecordFieldPostMethod( String strListId )
    {
        return treatListRecordWS( strListId );
    }

    public JSONObject getProperties( RecordField recordFieldParam, String strListId, int nKey )
    {
        String strPopupShowLinkProperty = AppPropertiesService.getProperty( GISMAP_VIEW + nKey + PARAMETER +
                POPUPSHOWLINK );

        if ( strPopupShowLinkProperty == null )
        {
            strPopupShowLinkProperty = Boolean.FALSE.toString(  );
            AppLogService.info( "Could not found the " + GISMAP_VIEW + nKey + PARAMETER + POPUPSHOWLINK +
                " property in the property file. Set to " + Boolean.FALSE.toString(  ) );
        }

        String strPopup1Property = AppPropertiesService.getProperty( GISMAP_VIEW + nKey + PARAMETER + POPUP1 );

        if ( strPopup1Property == null )
        {
            strPopup1Property = "";
            AppLogService.info( "Could not found the " + GISMAP_VIEW + nKey + PARAMETER + POPUP1 +
                " property in the property file. Set to empty string" );
        }

        String[] strrPopup1PropertyArray = strPopup1Property.split( "," );
        String[] strProperties = null;

        if ( strrPopup1PropertyArray.length > 1 )
        {
            strProperties = getBestProperties( strrPopup1PropertyArray[1] ).split( ";" );
        }

        String[] strListIdTab = strListId.split( "," );

        JSONObject properties = new JSONObject(  );

        if ( strProperties != null )
        {
            for ( int i = 0; i < strListIdTab.length; i++ )
            {
                int nIdRecordFiels = Integer.parseInt( strListIdTab[i] );
                RecordField recordField = RecordFieldHome.findByPrimaryKey( nIdRecordFiels, DirectoryUtils.getPlugin(  ) );

                //Field field = recordField.getField();
                if ( ( recordField.getEntry(  ).getIdEntry(  ) != recordFieldParam.getEntry(  ).getIdEntry(  ) ) &&
                        ( recordField.getRecord(  ).getIdRecord(  ) == recordFieldParam.getRecord(  ).getIdRecord(  ) ) )
                {
                    IEntry entry = recordField.getEntry(  );

                    if ( inProperties( strProperties, entry.getTitle(  ) ) )
                    {
                        properties.accumulate( entry.getTitle(  ), recordField.getValue(  ) );
                    }
                }
            }

            Record record = RecordHome.findByPrimaryKey( recordFieldParam.getRecord(  ).getIdRecord(  ),
                    DirectoryUtils.getPlugin(  ) );

            if ( record.getDirectory(  ) != null )
            {
                if ( strPopupShowLinkProperty.compareTo( "true" ) == 0 )
                {
                    properties.accumulate( "link", getLink( record ) );
                }
            }
        }

        if ( properties.size(  ) == 0 )
        {
            return null;
        }

        return properties;
    }

    public boolean inProperties( String[] properties, String strProperty )
    {
        boolean find = false;

        for ( int i = 0; !find && ( i < properties.length ); i++ )
        {
            find = properties[i].compareTo( "'" + strProperty + "'" ) == 0;
        }

        return find;
    }

    public int getViewNumber( String strListId )
    {
        String[] strListIdTab = strListId.split( "," );
        String strViewNumberValue = null;

        if ( strListIdTab != null )
        {
            for ( int i = 0; i < strListIdTab.length; i++ )
            {
                int nIdRecordFiels = Integer.parseInt( strListIdTab[i] );
                RecordField recordField = RecordFieldHome.findByPrimaryKey( nIdRecordFiels, DirectoryUtils.getPlugin(  ) );

                if ( recordField != null )
                {
                    Entry entry = (Entry) recordField.getEntry();
                    int idEntry = entry.getIdEntry();
                    List<Field> fieldlist = FieldHome.getFieldListByIdEntry(idEntry, DirectoryUtils.getPlugin(  ));
                    
                    for (Field field : fieldlist) {
                    	if ( ( field != null ) && ( field.getTitle(  ).compareTo( "viewNumberGes" ) == 0 ) )
                        {
                    		strViewNumberValue = field.getValue(  );
                            break;
                        }
					}
                }
            }
        }
        //( strViewNumberValue == null ) ? 1 : Integer.parseInt( strViewNumberValue );
        return Integer.parseInt( strViewNumberValue );
    }

    public String getLink( Record record )
    {
        int idDirectoryRecord = record.getIdRecord(  );
        int idDirectory = record.getDirectory(  ).getIdDirectory(  );

        return "jsp/admin/plugins/directory/DoVisualisationRecord.jsp?id_directory_record=" + idDirectoryRecord +
        "&id_directory=" + idDirectory;
    }

    public String getCoordinatesXY( RecordField recordFieldParam, String strListId )
    {
        //"geometry":{"type":"Point","coordinates":[2.3009992,48.836666]}}
        String[] strListIdTab = ( strListId != null ) ? strListId.split( "," ) : null;

        String strX = "";
        String strY = "";

        if ( strListIdTab != null )
        {
            for ( int i = 0; i < strListIdTab.length; i++ )
            {
                int nIdRecordFiels = Integer.parseInt( strListIdTab[i] );
                RecordField recordField = RecordFieldHome.findByPrimaryKey( nIdRecordFiels, DirectoryUtils.getPlugin(  ) );

                if ( ( recordField != null ) &&
                        ( recordField.getRecord(  ).getIdRecord(  ) == recordFieldParam.getRecord(  ).getIdRecord(  ) ) )
                {
                    Field field = recordField.getField(  );

                    if ( ( field != null ) && ( field.getTitle(  ).compareTo( "X" ) == 0 ) )
                    {
                        strX = recordField.getValue(  );

                        break;
                    }
                }
            }

            for ( int i = 0; i < strListIdTab.length; i++ )
            {
                int nIdRecordFiels = Integer.parseInt( strListIdTab[i] );
                RecordField recordField = RecordFieldHome.findByPrimaryKey( nIdRecordFiels, DirectoryUtils.getPlugin(  ) );

                if ( ( recordField != null ) &&
                        ( recordField.getRecord(  ).getIdRecord(  ) == recordFieldParam.getRecord(  ).getIdRecord(  ) ) )
                {
                    Field field = recordField.getField(  );

                    if ( ( field != null ) && ( field.getTitle(  ).compareTo( "Y" ) == 0 ) )
                    {
                        strY = recordField.getValue(  );

                        break;
                    }
                }
            }
        }

        if ( !strX.isEmpty(  ) && !strY.isEmpty(  ) )
        {
            JSONObject jsonElement = new JSONObject(  );
            jsonElement.accumulate( "type", "Point" );
            jsonElement.accumulate( "coordinates", "[" + strX + "," + strY + "]" );

            return jsonElement.toString(  );
        }

        return null;
    }

    public String getBestProperties( String strProperties ) // ['nom', 'prenom', 'link']
    {
        String strPropertiesReturn = strProperties.replace( "[", "" ).replace( "]", "" );

        return strPropertiesReturn;
    }

    private String treatListRecordWS( String strListId )
    {
    	String strRMMSHOWCENTROIDProperty = AppPropertiesService.getProperty( GISMAP_VIEW + getViewNumber( strListId ) +
                PARAMETER + RMMSHOWCENTROID );
    
        String[] strListIdTab = ( strListId != null ) ? strListId.split( "," ) : null;
        JSONObject collection = new JSONObject(  );
        collection.accumulate( "type", "FeatureCollection" );

        JSONArray array = new JSONArray(  );

        if ( strListIdTab != null )
        {
            for ( int i = 0; i < strListIdTab.length; i++ )
            {
                int nIdRecordFiels = Integer.parseInt( strListIdTab[i] );
                RecordField recordField = RecordFieldHome.findByPrimaryKey( nIdRecordFiels, DirectoryUtils.getPlugin(  ) );

                if ( recordField != null )
                {
                    Field field = recordField.getField(  );

                    if ( ( field != null ) && ( field.getTitle(  ).compareTo( "geometry" ) == 0 ) )
                    {
                        JSONObject jsonElementValue = new JSONObject(  );
                        String strRecordField = recordField.getValue(  );

                        if ( ( strRecordField != null ) && ( strRecordField.trim(  ).equals( "" ) ) )
                        {
                            strRecordField = null;
                        }

                        jsonElementValue.accumulate( "elementvalue", strRecordField );

                        JSONObject jsonElement = new JSONObject(  );
                        jsonElement.accumulate( "type", "Feature" );
                        jsonElement.accumulate( "id", recordField.getRecord(  ).getIdRecord(  ) );
                        jsonElement.accumulate( "properties",
                            getProperties( recordField, strListId, getViewNumber( strListId ) ) );

                        if ( ( strRMMSHOWCENTROIDProperty != null ) &&
                                ( strRMMSHOWCENTROIDProperty.compareTo( "true" ) == 0 ) )
                        {
                            jsonElement.accumulate( "geometry", getCoordinatesXY( recordField, strListId ) );
                        }
                        else
                        {
                            JSONObject elementValue = jsonElementValue.getJSONObject( "elementvalue" );

                            if ( elementValue.isNullObject(  ) )
                            {
                                jsonElement.accumulate( "geometry", null );
                            }
                            else
                            {
                                jsonElement.accumulate( "geometry", elementValue.getJSONObject( "geometry" ) );
                            }
                        }

                        array.add( jsonElement );
                    }
                }
            }
        }

        if ( array.size(  ) > 0 )
        {
            collection.accumulate( "features", array );
        }

        return "callback(" + collection.toString(  ) + ")";
    }
}
