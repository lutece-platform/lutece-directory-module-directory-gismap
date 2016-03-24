/**
This jquery plugin allows you to change a text input into a auto complexion field to find an address.

You can adjust the address precisely on a map afterwards.

How to use it :

<link rel="stylesheet" type="text/css" href="css/plugins/gis/gis.css" media="screen, projection, print"/>
<link rel="stylesheet" type="text/css" href="js/plugins/gis/openlayers/Legend.css" />
<link rel="stylesheet" type="text/css" href="js/plugins/gis/openlayers/GeolocalizationPanel.css" />
<link rel="stylesheet" type="text/css" href="js/plugins/gis/openlayers/LayerSearchPanel.css" />
<link rel="stylesheet" type="text/css" href="js/plugins/gis/openlayers/default/style.css" />
<script src="js/jquery.geolocalize.js" type="text/javascript"></script>
<script src="js/plugins/gis/LABjs-1.0.2rc1/LAB.js" type="text/javascript"></script>
<script src="js/plugins/gis/gis.js" type="text/javascript"></script>
<script type="text/javascript">
	$("#searchTextField").geolocalize( {gisCode: MAP_GIS_CODE });
</script>

You can change the default settings by overwriting them. Example :
$("#searchTextField").geolocalize({mapHeight : "400px", mapWidth : "100%"});
	
 */
var GeolocUtils = {
	params : null,

	initGeolocalization : function( params )
	{
		GeolocUtils.params = $.extend({
			currentAddressInputField : ""
		}, params);
	},

	initAutocomplete : function( ) 
	{
		var Csslink = document.createElement("link");
		Csslink.rel="STYLESHEET";
		Csslink.type="text/css";
		Csslink.href="js/jquery/plugins/ui/css/jquery-ui.css";
		document.getElementsByTagName('head')[0].appendChild(Csslink);
		
		$.getScript('jsp/plugins/address/modules/autocomplete/autocomplete-js.jsp',function(data, textStatus, jqxhr) {
   			createAutocomplete(GeolocUtils.params.thisOject.selector);
		});
	},
	
	initAutocompleteSuggestPOI : function( ) 
	{
		var Csslink = document.createElement("link");
		Csslink.rel="STYLESHEET";
		Csslink.type="text/css";
		Csslink.href="js/jquery/plugins/ui/css/jquery-ui.css";
		document.getElementsByTagName('head')[0].appendChild(Csslink);
		
		$.getScript("js/jquery/jQuery.onReady.js");
		$.getScript("jsp/site/plugins/address/modules/autocomplete/SetupSuggestPOI.js.jsp");
		$.getScript("js/plugins/address/modules/autocomplete/jQuery.suggestPOI.js", function (d, y, t) {
			setTimeout( function() {
				var jAdresse = GeolocUtils.params.thisOject;
				jAdresse.suggestPOI();
				jAdresse.bind($.suggestPOI.EVT_SELECT, function(event) {
					
					var poi = event.poi;
					
					GeolocUtils.poiSelected(poi);
				});
			}, 1500);
		});
	},
	
	addGisLocalizationListeners : function ( ) {
		
		var updateInputField = function(event) {
			var assertZero = function (data) { return !Number(data)>0 };
			if( assertZero(event.lonLat.lat) && 
				assertZero(event.lonLat.lon) &&
				event.address.length == 0
			){
				if( event.inverse ) {
					GeolocUtils.setAddressInputField( "" );
				}
				GeolocUtils.setLatInputField( "" );	GeolocUtils.setLonInputField( "" );
			} else {
				if ( event.namespace != 'dragComplete' && 
						GeolocUtils.getLonAddressInputField() != "" &&
						GeolocUtils.getLatAddressInputField() != "" && 
						GeolocUtils.getLonInputField() != "" &&
						GeolocUtils.getLatInputField() != "" &&
						GeolocUtils.getLonAddressInputField() != GeolocUtils.getLonInputField() &&
						GeolocUtils.getLatAddressInputField() != GeolocUtils.getLatInputField() ) {
					// repositioner le marker!!!!
					//to get the global map objects
					var $LUTECE = window.LUTECE || (window.LUTECE = {});
					var $GIS = $LUTECE.GIS || ($LUTECE.GIS = {});
					var $maps = $GIS.maps || ($GIS.maps = {});
					
					//get the geomarker
					var marker = $maps[GeolocUtils.params.idMapDiv].geolocalizationMarkerLayers[0].features[0];
					
					//change sys proj
					p = new Proj4js.Point(GeolocUtils.getLonInputField(), GeolocUtils.getLatInputField());   
					Proj4js.transform(GeolocUtils.params.projDestSRID, GeolocUtils.params.projSourceSRID, p);
					
					var lonLat = new OpenLayers.LonLat(p.x, p.y);
					marker.move(lonLat);
				} else {
					//change sys proj
					p = new Proj4js.Point(event.lonLat.lon, event.lonLat.lat);   
					Proj4js.transform(GeolocUtils.params.projSourceMapSRID, GeolocUtils.params.projDestSRID, p);
	
					GeolocUtils.setLonInputField( p.x );
					GeolocUtils.setLatInputField( p.y );
				}
				// FIX change event.address to event.typoLibelle in a other method
				if( event.namespace != 'dragComplete' && event.address.length != 0 ){
					GeolocUtils.setAddressInputField( event.address );
					GeolocUtils.setLonAddressInputField( p.x );
					GeolocUtils.setLatAddressInputField( p.y );
					
				}
				GeolocUtils.params.currentAddressInputField = GeolocUtils.getAddressInputField();
				if( GeolocUtils.params.addressValid == true )
				{
					$(GeolocUtils.params.thisOject).bind("keyup", 
							$.proxy(function (event){
								if( GeolocUtils.params.currentAddressInputField != undefined &&
									GeolocUtils.params.currentAddressInputField != GeolocUtils.getAddressInputField() 	) 
								{
									GeolocUtils.cleanMapEvent( );
									GeolocUtils.setLatInputField( "" );	GeolocUtils.setLonInputField( "" );
									GeolocUtils.setLatAddressInputField( "" );	GeolocUtils.setLonAddressInputField( "" );
									$(GeolocUtils.params.thisOject).unbind("keyup");
								}
							},
							this)
							
					);	
				}
			}		
		};
		
		$("body").bind( 'GisLocalization.done', updateInputField);
		$("body").bind( 'GisLocalization.dragComplete', updateInputField);
	},
	
	addGisMapListeners : function ( ) {
		
		var reloadAddress = function ( ) {
			var addressFieldValue = GeolocUtils.getAddressInputField( );
			if( addressFieldValue != ""){
					$('body').trigger(
							jQuery.Event( 'GisLocalization.send',  { 'address': addressFieldValue } )
					); 
			}
		};
		
		if( GeolocUtils.params.addressAutoReload ) {
			$("body").bind( 'GisMap.displayComplete', reloadAddress );
		}
		
		var onDisplayMap = GeolocUtils.params.onDisplayMap;
		if( onDisplayMap != undefined || onDisplayMap != null ) {
			$("body").bind( 'GisMap.displayComplete', onDisplayMap );
		}
	},
	
	addGisMapListenersSuggestPOI : function ( ) {
		
		var reloadAddress = function ( ) {
			
			var addressFieldValue = GeolocUtils.getAddressInputField( );

			var latFieldValue = GeolocUtils.getLatInputField( );
			var lonFieldValue = GeolocUtils.getLonInputField( );
			
			var latAddressFieldValue = GeolocUtils.getLatAddressInputField( );
			var lonAddressFieldValue = GeolocUtils.getLonAddressInputField( );

			if( addressFieldValue != "" && lonAddressFieldValue != "" && latAddressFieldValue != "") {
				//on recupere les anciennes valeurs (avec la projection de destination donc)
				var poi = {
						libelleTypo: addressFieldValue,
						x: lonAddressFieldValue,
						y: latAddressFieldValue,
						srid: GeolocUtils.params.destSRID
				};
				
				$("body").trigger(
						jQuery.Event("GisLocalization.send.geolocalize.suggestPOI", {
								poi: poi
							})
					);
				
				
			}
		};
		
		if( GeolocUtils.params.addressAutoReload ) {
			$("body").bind( "GisMap.displayComplete", reloadAddress );
		}
		
		var onDisplayMap = GeolocUtils.params.onDisplayMap;
		if( onDisplayMap != undefined || onDisplayMap != null ) {
			$("body").bind( 'GisMap.displayComplete', onDisplayMap );
		}
	},
	
	addGisEventListeners : function( ) 
	{				
		
		GeolocUtils.addGisLocalizationListeners();
		GeolocUtils.addGisMapListeners();
		
		if( GeolocUtils.params.autoComplete != true) 
		{
			$(GeolocUtils.params.thisOject).bind(GeolocUtils.params.onEvent, 
				$.proxy(function (event){
						GeolocUtils.placeChangedEvent(event);
				},
				this)		
			);
		}
		$(GeolocUtils.params.thisOject).bind("keypress", 
			$.proxy(function (event){
				if ( event.keyCode == 13 ){
					GeolocUtils.placeChangedEvent(event);
				}
			},
			this)		
		);
		
		$( GeolocUtils.params.thisOject ).bind( "autocompleteselect", function(event, ui) {
			GeolocUtils.addressSelected(ui.item.value);
		});
	},
	
	addGisEventListenersSuggestPoi: function( ) {
		
		GeolocUtils.addGisLocalizationListeners();
		GeolocUtils.addGisMapListenersSuggestPOI();
		
	},
	
	addressSelected : function (stringAddress) {
		$('body').trigger(
			jQuery.Event('GisLocalization.send', { 
				address: stringAddress
			})
		);
	},
	
	poiSelected : function (poi) {
		
		if (poi != undefined) {
			var poiWithProj = poi;
			poiWithProj.srid = GeolocUtils.params.sourceSRID;
			
			//empty all fields
			GeolocUtils.cleanInputsFields();
			
			$('body').trigger(
				jQuery.Event('GisLocalization.send.geolocalize.suggestPOI', {
						poi: poiWithProj
					})
			);
		}
	},
	
	showMap : function( ) 
	{
		var idMapDiv = GeolocUtils.params.idMapDiv;
		$( "#" +idMapDiv ).css("height", GeolocUtils.params.mapHeight);
		$( "#" +idMapDiv ).css("width", GeolocUtils.params.mapWidth);
		
		var optionalParameters = {};
	
		$( "#" +idMapDiv ).data('optionalParameters', optionalParameters);	
		$(document).ready(function() {
		var baseUrl = $('base').attr('href');
				$LAB
					.script(baseUrl +"js/plugins/gis/openlayers/OpenLayers.js").wait()
					.script(baseUrl +"js/plugins/gis/openlayers/CustomLayerSwitcher.js").wait()
					.script(baseUrl +"js/plugins/gis/openlayers/Legend.js").wait()
					.script(baseUrl +"js/plugins/gis/openlayers/LayerSearchPanel.js").wait()
					.script(baseUrl +"js/plugins/gis/openlayers/GeolocalizationPanel.js").wait()
					.script(baseUrl +"js/plugins/gis/gis.js").wait()
					.wait(function(){
						$.get(baseUrl +'/jsp/site/plugins/gis/DoDisplayMap.jsp?map_name='+idMapDiv+'&gis_code='+GeolocUtils.params.gisCode, 
								function(data){
									$("#"+idMapDiv).html(data);
									$("#"+idMapDiv).show();
								});
					});
		});
	},

	cleanInputsFields: function ( )
	{
		GeolocUtils.setAddressInputField("");
		GeolocUtils.setLonInputField("");
		GeolocUtils.setLatInputField("");
		GeolocUtils.setLonAddressInputField("");
		GeolocUtils.setLatAddressInputField("");
	},
	
	setAddressInputField: function ( data ) 
	{
		$(document.getElementById($(GeolocUtils.params.thisOject).attr('id'))).val(data);
	},
	
	getAddressInputField: function ( )
	{
		return $.trim( 
				$(document.getElementById($(GeolocUtils.params.thisOject).attr('id'))).val() 
		);
	},
	
	setLonInputField: function ( data ) 
	{
		$("input[name='" + GeolocUtils.params.inputLngName + "']").val(data);
	},
	
	setLatInputField: function ( data ) 
	{
		$("input[name='" + GeolocUtils.params.inputLatName + "']").val(data);
	},
	
	getLonInputField: function (  ) 
	{
		return $("input[name='" + GeolocUtils.params.inputLngName + "']").val();
	},
	
	getLatInputField: function (  ) 
	{
		return $("input[name='" + GeolocUtils.params.inputLatName + "']").val();
	},
	
	setLonAddressInputField: function ( data ) 
	{
		return $("input[name='" + GeolocUtils.params.inputLngAddressName + "']").val(data);
	},
	
	setLatAddressInputField: function ( data ) 
	{
		$("input[name='" + GeolocUtils.params.inputLatAddressName + "']").val(data);
	},
	
	getLonAddressInputField: function (  ) 
	{
		return $("input[name='" + GeolocUtils.params.inputLngAddressName + "']").val();
	},
	
	getLatAddressInputField: function (  ) 
	{
		return $("input[name='" + GeolocUtils.params.inputLatAddressName + "']").val();
	},
	
	cleanMapEvent : function ()
	{
		$('body').trigger( jQuery.Event('GisLocalization.clean') );
	},
	
	placeChangedEvent : function() 
	{
		$('body').trigger(
				jQuery.Event('GisLocalization.send', { 
					address: GeolocUtils.getAddressInputField( ) 
				})
		);
	}
};

jQuery.fn.geolocalize = function(params) {
	params = $.extend({
		inputLngName : "lng",	    // ID of the longitude input text field.
		inputLatName : "lat",		// ID of the latitude input text field.
		onEvent :  "change",    	// Exectute a geo-localization request after this event is triggered ( when autoComplete = false ).
		onDisplayMap : undefined,	// Function triggered after map is displayed.
		addressAutoReload : true,	// Execute geolocalization request when address input text field is not empty after the page is fully loaded.
		addressValid : true,		// Clean the map whether address input text field is modified after a geo-localization request.
		idMapDiv : "map_canvas",	// ID of the div that which contain the map.
		mapHeight : "400px",    	// Height of the displayed map.
		mapWidth : "100%",			// Width of the displayed map.
		autoComplete : true		// Activate/deactivate auto-complete feature from the address input text field.
		
	}, params);
	
	params.thisOject = this;
	
	$(this).keypress(function(event) {
		return event.keyCode != 13;
	});
	
	$(document).ready(function(){
		GeolocUtils.initGeolocalization(params);
		if( GeolocUtils.params.autoComplete == true) 
		{
			GeolocUtils.initAutocomplete( );
		}
		GeolocUtils.addGisEventListeners( ); 
		GeolocUtils.showMap( );
		
	});
	return this;
};

jQuery.fn.geolocalizeSuggestPOI = function(params) {
	params = $.extend({
		inputLngName : "lng",	    // ID of the longitude input text field.
		inputLatName : "lat",		// ID of the latitude input text field.
		inputLngAddressName : "lngAddress",
		inputLatAddressName : "latAddress",
		onEvent :  "change",    	// Exectute a geo-localization request after this event is triggered ( when autoComplete = false ).
		onDisplayMap : undefined,	// Function triggered after map is displayed.
		addressAutoReload : true,	// Execute geolocalization request when address input text field is not empty after the page is fully loaded.
		addressValid : true,		// Clean the map whether address input text field is modified after a geo-localization request.
		idMapDiv : "map_canvas",	// ID of the div that which contain the map.
		mapHeight : "400px",    	// Height of the displayed map.
		mapWidth : "100%",			// Width of the displayed map.
		autoComplete : true,		// Activate/deactivate auto-complete feature from the address input text field.
		sourceSRID : "EPSG:27561",  // SRID from source POIs
		sourceMapSRID : "EPSG:27561",  // SRID from the map (ex : carto webmercator aka openstreetmap 900913; 27561 pour carto Ville de Paris en Lambert)
		destSRID : "EPSG:4326"      // SRID from destination fields
	}, params);
	
	params.thisOject = this;
	
	$(this).keypress(function(event) {
		return event.keyCode != 13;
	});
	
	params.projSourceSRID = new Proj4js.Proj(params.sourceSRID);
	params.projSourceMapSRID = new Proj4js.Proj(params.sourceMapSRID);
	params.projDestSRID = new Proj4js.Proj(params.destSRID);
	// FIX for IE
	params.projSourceSRID = new Proj4js.Proj(params.sourceSRID);
	params.projSourceMapSRID = new Proj4js.Proj(params.sourceMapSRID);
	params.projDestSRID = new Proj4js.Proj(params.destSRID);
	
	$(document).ready(function(){
		GeolocUtils.initGeolocalization(params);
		if( GeolocUtils.params.autoComplete == true) 
		{
			GeolocUtils.initAutocompleteSuggestPOI( );
			if ( GeolocUtils.params.requalif == true ) {
				
				GeolocUtils.setLatAddressInputField(GeolocUtils.getLatInputField());
				GeolocUtils.setLonAddressInputField(GeolocUtils.getLonInputField());
			}
		}
		GeolocUtils.addGisEventListenersSuggestPoi( ); 
		GeolocUtils.showMap( );
		
	});
	return this;
};