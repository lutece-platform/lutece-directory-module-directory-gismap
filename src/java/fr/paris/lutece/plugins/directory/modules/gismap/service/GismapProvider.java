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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import fr.paris.lutece.plugins.directory.business.IMapProvider;
import fr.paris.lutece.plugins.gismap.business.View;
import fr.paris.lutece.plugins.gismap.business.ViewHome;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.ReferenceItem;


/**
 * 
 * GismapProvider : provides Gismap support for Directory
 * 
 */
public class GismapProvider implements IMapProvider
{
	private static final String PROPERTY_KEY = "directory-gismap.key";
	private static final String PROPERTY_DISPLAYED_NAME = "directory-gismap.displayName";
	private static final String TEMPLATE_HTML = "../modules/gismap/GismapTemplate.html";
	private static final String TEMPLATE_FRONT_HTML = "modules/gismap/GismapTemplate.html";
	private static final String TEMPLATE_FRONT_LIST_HTML = "modules/gismap/GismapListTemplate.html";
	private static final String TEMPLATE_BACK_LIST_HTML = "modules/gismap/GismapListTemplate.html";
	
	private static final boolean CONSTANT_MAP_LIST_SUPPORTED = true;
	
	/**
	 * {@inheritDoc}
	 */
	public String getKey(  ) 
	{
		return AppPropertiesService.getProperty( PROPERTY_KEY );
	}
		
	/**
	 * {@inheritDoc}
	 */
	public String getDisplayedName(  ) 
	{
		return AppPropertiesService.getProperty( PROPERTY_DISPLAYED_NAME );
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String getHtmlCode(  ) 
	{
		return TEMPLATE_HTML;
	}
		
	/**
	 * {@inheritDoc}
	 */
	public ReferenceItem toRefItem(  )
	{
		ReferenceItem refItem = new ReferenceItem(  );
		
		refItem.setCode( getKey(  ) );
		refItem.setName( getDisplayedName(  ) );
		
		return refItem;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString(  ) 
	{
		return "Directory Provider";
	}

	/**
	 * {@inheritDoc}
	 */
	public String getFrontHtmlCode(  ) 
	{
		return TEMPLATE_FRONT_HTML;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getFrontListHtmlCode(  ) 
	{
		return TEMPLATE_FRONT_LIST_HTML;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getBackListHtmlCode(  )
	{
		return TEMPLATE_BACK_LIST_HTML;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isMapListSupported(  )
	{
		return CONSTANT_MAP_LIST_SUPPORTED;
	}

	/**
	 * {@inheritDoc}
	 */
	public Object getParameter( int nKey) {
        View view = ViewHome.findByPrimaryKey( nKey );
		return view;
	}
	
	public HttpSession getHttpSession(HttpServletRequest request)
	{
		String strGismapEntry = request.getParameter(  "gismap_entry" ); 
        String strExtentCurrent = request.getParameter(  strGismapEntry + "_extent_current" ); 
        String strVisibleLayer = request.getParameter(  strGismapEntry + "_visible_layer" ); 
        HttpSession session = request.getSession( false );
        session.setAttribute("ttt", "ttt");
        session.setAttribute(strGismapEntry + "_extent_current", strExtentCurrent);
        session.setAttribute(strGismapEntry + "_visible_layer", strVisibleLayer);
        
        return session;
	}
}
