/*
 * Copyright (c) 2002-2017, Mairie de Paris
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

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "DirectoryGismapSourceQuery")
public class DirectoryGismapSourceQuery {
	
	private int _idGeolocationEntry;
	private String _geolocationEntryTitle;
	private String _listIdRecord;
	private int _idDirectory;
	private String _directoryTitle;
	private int _geoJsonIndex;
	private String _view;

	public DirectoryGismapSourceQuery() {
	}

	
	@Override
	public String toString() {
		return "DirectoryGismapSourceQuery [idGeolocationEntry=" + _idGeolocationEntry 
				+ ", IdDirectory=" + _idDirectory + ", listIdRecord=" + _listIdRecord + ", geoJsonIndex=" + _geoJsonIndex+ ", view="+ _view + "]";
	}

	/** Getter for idGeolocationEntry
	 * @return idGeolocationEntry
	 */
	public int getIdGeolocationEntry() {
		return _idGeolocationEntry;
	}


	/** Setter for idGeolocationEntry
	 * @param idGeolocationEntry
	 */
	public void setIdGeolocationEntry(int idGeolocationEntry) {
		_idGeolocationEntry = idGeolocationEntry;
	}

	/** Getter for idDirectory
	 * @return idDirectory
	 */
	public int getIdDirectory() {
		return _idDirectory;
	}


	/** Setter for idDirectory
	 * @param idDirectory
	 */
	public void setIdDirectory(int idDirectory) {
		_idDirectory = idDirectory;
	}
	
	/** Getter for listIdRecord
	 * @return listIdRecord
	 */
	public String getListIdRecord( ) {
		return _listIdRecord;
	}

	/** Setter for listIdRecord
	 * @param listIdRecord
	 */
	public void setListIdRecord(String listIdRecord) {
		_listIdRecord = listIdRecord;
	}

	/** Getter for geoJsonIndex
	 * @return geoJsonIndex
	 */
	public int getGeoJsonIndex( ) {
		return _geoJsonIndex;
	}
	
	/** Setter for geoJsonIndex
	 * @param geoJsonIndex
	 */
	public void setGeoJsonIndex(int geoJsonIndex) {
		_geoJsonIndex = geoJsonIndex;
		
	}

	/** Getter for view
	 * @return _view
	 */
	public String getView() {
		return _view;
	}

	/** Setter for view
	 * @param strView
	 */
	public void setView(String strView) {
		this._view = strView;
	}


	/** Getter for geolocationEntryTitle
	 * @return _geolocationEntryTitle
	 */
	public String getGeolocationEntryTitle() {
		return _geolocationEntryTitle;
	}


	/** Setter for geolocationEntryTitle
	 * @param geolocationEntryTitle
	 */
	public void setGeolocationEntryTitle(String geolocationEntryTitle) {
		this._geolocationEntryTitle = geolocationEntryTitle;
	}


	/** Getter for directoryTitle
	 * @return _directoryTitle
	 */
	public String getDirectoryTitle() {
		return _directoryTitle;
	}


	/** Setter for directoryTitle
	 * @param directoryTitle
	 */
	public void setDirectoryTitle(String directoryTitle) {
		this._directoryTitle = directoryTitle;
	}

}
