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
package fr.paris.lutece.plugins.directory.modules.gismap.utils;

import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.plugins.directory.business.Directory;
import fr.paris.lutece.plugins.directory.business.DirectoryHome;
import fr.paris.lutece.plugins.directory.business.Entry;
import fr.paris.lutece.plugins.directory.business.EntryFilter;
import fr.paris.lutece.plugins.directory.business.EntryHome;
import fr.paris.lutece.plugins.directory.business.Field;
import fr.paris.lutece.plugins.directory.business.IEntry;
import fr.paris.lutece.plugins.directory.service.DirectoryPlugin;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;


// TODO: Auto-generated Javadoc
/**
 * Utility class for plugin Gismap.
 */
public final class GismapDirectoryUtils
{

    private static final Plugin _DirectoryPlugin = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );

	/** The Constant GISMAP_VIEW. */
    public static final String GISMAP_VIEW      = "gismap.view.";

    /** The Constant GISMAP_PARAMETER. */
    public static final String GISMAP_PARAMETER = ".parameter";

    
    private static final String PROPERTY_ENTRY_TYPE_GEOLOCATION = "directory.entry_type.geolocation";

    /**
     * GismapUtils.
     */
    private GismapDirectoryUtils( )
    {
    }

    /**
     * Gets the nb view by directory id.
     *
     * @param directoryId the directory id
     * @return the nb view by directory id
     */
    public static String getNbViewByDirectoryId( int directoryId )
    {
        String nbView = "";

        Directory directory = DirectoryHome.findByPrimaryKey( directoryId, _DirectoryPlugin );
        EntryFilter filter = new EntryFilter( );
        filter.setIdDirectory( directory.getIdDirectory( ) );
        filter.setIsComment( EntryFilter.FILTER_FALSE );
        filter.setIsEntryParentNull( EntryFilter.FILTER_TRUE );

        List<IEntry> listEntry = new ArrayList<>( );

        List<IEntry> listEntryFirstLevel = EntryHome.getEntryList( filter, _DirectoryPlugin );

        filter.setIsEntryParentNull( EntryFilter.ALL_INT );

        for ( IEntry entry : listEntryFirstLevel )
        {
            if ( !entry.getEntryType( ).getGroup( ) )
            {
                listEntry.add( EntryHome.findByPrimaryKey( entry.getIdEntry( ), _DirectoryPlugin ) );
            }

            filter.setIdEntryParent( entry.getIdEntry( ) );

            List<IEntry> listChildren = EntryHome.getEntryList( filter, _DirectoryPlugin );

            for ( IEntry entryChild : listChildren )
            {
                listEntry.add( EntryHome.findByPrimaryKey( entryChild.getIdEntry( ), _DirectoryPlugin ) );
            }
        }

        for ( IEntry entry : listEntry )
        {
            if ( "fr.paris.lutece.plugins.directory.business.EntryTypeGeolocation".equals( entry.getEntryType( ).getClassName( ) ) )
            {
                for ( Field field : entry.getFields( ) )
                {
                    if ( "viewNumberGes".equals( field.getTitle( ) ) )
                    {
                        nbView = field.getValue( );
                    }
                }
            }
        }
        return nbView;
    }

    
	/**
	 * Return the Identifier of the first Geolocation Entry inside a given Directory
	 * 
	 * TODO : change the output parameter to List<Integer> to handle several geolocationEntries within a directory
	 * 
	 * @param identifier of Directory
	 * 
	 * @return the geolocation entry identifier
	 */
	public static Integer getGeolocationEntry(Integer nIdDirectory) {
		
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
    
}
