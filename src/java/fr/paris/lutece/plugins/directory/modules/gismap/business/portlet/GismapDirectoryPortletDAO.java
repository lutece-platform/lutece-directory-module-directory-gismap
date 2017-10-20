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
package fr.paris.lutece.plugins.directory.modules.gismap.business.portlet;

import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.plugins.directory.modules.gismap.business.DirectoryGismapSourceQuery;
import fr.paris.lutece.portal.business.portlet.Portlet;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * This class provides Data Access methods for ArticlesListPortlet objects
 */
public final class GismapDirectoryPortletDAO implements IGismapDirectoryPortletDAO
{
    private static final String SQL_QUERY_INSERT                          = "INSERT INTO gismap_portlet ( id_portlet , id_view ) VALUES ( ? , ? )";
    private static final String SQL_QUERY_SELECT                          = "SELECT id_portlet , id_view FROM gismap_portlet WHERE id_portlet = ? ";
    private static final String SQL_QUERY_UPDATE                          = "UPDATE gismap_portlet SET id_view = ? WHERE id_portlet = ? ";
    private static final String SQL_QUERY_DELETE                          = "DELETE FROM gismap_portlet WHERE id_portlet= ? ";
    private static final String SQL_QUERY_SELECT_DIRECTORY_GISMAP_SOURCES_BY_PORTLET_ID = "SELECT s.id_directory, d.title, s.id_entry_geolocation, e.title, s.geojson_index"
    		+ " FROM directory_gismap_source s INNER JOIN directory_directory d ON s.id_directory = d.id_directory "
    		+ " INNER JOIN directory_entry e ON s.id_entry_geolocation = e.id_entry"
            + " WHERE s.id_portlet= ? ORDER BY s.geojson_index";
  	private static final String SQL_QUERY_DELETE_DIRECTORY_GISMAP_SOURCES_BY_PORTLET_ID = "DELETE from directory_gismap_source WHERE id_portlet= ?";
	private static final String SQL_QUERY_INSERT_DIRECTORY_GISMAP_SOURCES_BY_PORTLET_ID = "INSERT INTO directory_gismap_source ( id_directory_gismap_source, id_portlet, id_directory, id_entry_geolocation, geojson_index) "
			 + " VALUES ( ?, ?, ?, ?, ? ) ";
	private static final String SQL_QUERY_NEW_PK = "SELECT max( id_directory_gismap_source ) FROM directory_gismap_source";



    // /////////////////////////////////////////////////////////////////////////////////////
    // Access methods to data

    /**
     * Insert a new record in the table form_portlet
     *
     *
     * @param portlet the instance of the Portlet object to insert
     */
    @Override
    public void insert( Portlet portlet )
    {
        GismapDirectoryPortlet p = ( GismapDirectoryPortlet ) portlet;

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT );
        daoUtil.setInt( 1, p.getId( ) );
        daoUtil.setInt( 2, p.getView( ) );

        daoUtil.executeUpdate( );
        daoUtil.free( );
        
        removeDirectoryGismapSourcesByPortletId(p.getId( ) );
        addDirectoryGismapSourcesToPortlet(p.getId( ), p.getListMapSource( ) );  
    }

    /**
     * Deletes records for a portlet identifier in the table form_portlet
     *
     *
     * @param nPortletId the portlet identifier
     */
    @Override
    public void delete( int nPortletId )
    {
    	removeDirectoryGismapSourcesByPortletId( nPortletId );
    	
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE );
        daoUtil.setInt( 1, nPortletId );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * Loads the data of Form Portlet whose identifier is specified in parameter
     *
     *
     * @param nPortletId The Portlet identifier
     * @return theDocumentListPortlet object
     */
    @Override
    public Portlet load( int nPortletId )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT );
        daoUtil.setInt( 1, nPortletId );
        daoUtil.executeQuery( );

        GismapDirectoryPortlet portlet = new GismapDirectoryPortlet( );

        if ( daoUtil.next( ) )
        {
            portlet.setId( daoUtil.getInt( 1 ) );
            portlet.setView( daoUtil.getInt( 2 ) );
        }

        daoUtil.free( );
        
        List<DirectoryGismapSourceQuery> listMapSources = loadMapSourcesByPortletId( nPortletId );
        portlet.setListMapSource( listMapSources );

        return portlet;
    }
    

    /**
     * Update the record in the table
     *
     *
     * @param portlet A portlet
     */
    @Override
    public void store( Portlet portlet )
    {
        GismapDirectoryPortlet p = ( GismapDirectoryPortlet ) portlet;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE );
        daoUtil.setInt( 1, p.getView( ) );
        daoUtil.setInt( 2, p.getId( ) );
        daoUtil.executeUpdate( );
        daoUtil.free( );
        
        removeDirectoryGismapSourcesByPortletId(p.getId( ) );
        addDirectoryGismapSourcesToPortlet(p.getId( ), p.getListMapSource( ) );        
        
    }
    
   
    /**
     * returns the list of DirectoryGismapSourceQueries for a given portlet
     * @param portletId
     * @return the list of DirectoryGismapSourceQueries
     */
    public List<DirectoryGismapSourceQuery> loadMapSourcesByPortletId( int portletId )
    {
    	List<DirectoryGismapSourceQuery> listDirectoryGismapSourceQuery = new ArrayList<>( );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_DIRECTORY_GISMAP_SOURCES_BY_PORTLET_ID );
        daoUtil.setInt( 1, portletId );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
        	DirectoryGismapSourceQuery directoryGismapSourceQuery = new DirectoryGismapSourceQuery( );
        	directoryGismapSourceQuery.setIdDirectory( daoUtil.getInt( 1 ) );
        	directoryGismapSourceQuery.setDirectoryTitle( daoUtil.getString( 2 ) );
        	directoryGismapSourceQuery.setIdGeolocationEntry( daoUtil.getInt( 3) );
        	directoryGismapSourceQuery.setGeolocationEntryTitle( daoUtil.getString( 4 ) );
        	directoryGismapSourceQuery.setGeoJsonIndex( daoUtil.getInt( 5 ) );

        	listDirectoryGismapSourceQuery.add( directoryGismapSourceQuery );
        }
        
        daoUtil.free();
        
        return listDirectoryGismapSourceQuery;
    }
    
    
    
	/**
	 * remove the  DirectoryGismapSourceQueries for a given portlet
	 * @param portletId
	 */
	public void removeDirectoryGismapSourcesByPortletId(int portletId) {
		DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_DIRECTORY_GISMAP_SOURCES_BY_PORTLET_ID );
        daoUtil.setInt( 1, portletId );
        daoUtil.executeUpdate( );
        daoUtil.free();
	}


	/**
	 * Create the DirectoryGismapSourceQueries for a given portlet
	 * @param portletId
	 * @param listDirectoryGismapSource
	 */
	public void addDirectoryGismapSourcesToPortlet(int idPortlet, List<DirectoryGismapSourceQuery> listDirectoryGismapSource) {
		
		if ( idPortlet != 0)
		{			
			for (DirectoryGismapSourceQuery directoryGismapSource :  listDirectoryGismapSource)
			{
				DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT_DIRECTORY_GISMAP_SOURCES_BY_PORTLET_ID );
				int nIndex = 1;
				daoUtil.setInt( nIndex++, newPrimaryKey( ));
				daoUtil.setInt( nIndex++, idPortlet );
				daoUtil.setInt( nIndex++, directoryGismapSource.getIdDirectory( ) );
				daoUtil.setInt( nIndex++, directoryGismapSource.getIdGeolocationEntry( ) );
				daoUtil.setInt( nIndex++, directoryGismapSource.getGeoJsonIndex( ) );
		        daoUtil.executeUpdate( );
		        daoUtil.free();
			}
		}		
	}
	
	
	/**
     * Generates a new primary key
     *
     * @param plugin
     *            The Plugin
     * @return The new primary key
     */
    public int newPrimaryKey( )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_PK );
        daoUtil.executeQuery( );

        int nKey = 1;

        if ( daoUtil.next( ) )
        {
            nKey = daoUtil.getInt( 1 ) + 1;
        }

        daoUtil.free( );

        return nKey;
    }

    
}
