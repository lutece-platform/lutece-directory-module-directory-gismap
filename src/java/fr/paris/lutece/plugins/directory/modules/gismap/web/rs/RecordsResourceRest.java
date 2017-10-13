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
package fr.paris.lutece.plugins.directory.modules.gismap.web.rs;

import fr.paris.lutece.plugins.directory.modules.gismap.business.RecordsResourceQuery;
import fr.paris.lutece.plugins.directory.modules.gismap.business.RecordsResource;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path( "/rest/directory-gismap/" )
public class RecordsResourceRest
{

	@GET
	@Path( "listRecord" )
	@Produces({"application/javascript"})
	public String getListRecordFieldGetMehod(
			@QueryParam( "idGeolocationEntry" ) String strIdGeolocationEntry,
			@QueryParam( "idDirectory" ) String strIdDirectory,
			@QueryParam( "listIdRecord" ) String strListIdRecord,
			@QueryParam( "callback" ) String strCallback
			)
	{
		RecordsResourceQuery query = new RecordsResourceQuery( strIdGeolocationEntry, strIdDirectory, strListIdRecord);
		String strResponse = RecordsResource.treatListRecordWS( query );
		
		return strCallback + "(" + strResponse + ")";
	}
	

	@POST
	@Path( "listRecord/post" )
	@Produces({"application/javascript"})
	@Consumes( MediaType.APPLICATION_JSON)
	public String getListRecordFieldPostMethod( RecordsResourceQuery query, @QueryParam( "callback" ) String strCallback)
	{
		String strResponse = RecordsResource.treatListRecordWS( query );
				
		//return Response.ok(strResponse, MediaType.APPLICATION_JSON).build( );
		return strCallback +"(" + strResponse + ");";
	}

}
