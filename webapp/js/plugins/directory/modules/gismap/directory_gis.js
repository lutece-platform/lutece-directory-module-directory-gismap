/*
 * GIS
 */

function gis_result_list( id_field ) 
{
	$("#" + id_field + "_map-button").show();
	$("#" + id_field + "_map-button").click(function () {
		$(".directory-result-list").hide();
		$("#" + id_field + "_map-button").hide();
		$("#" + id_field + "_map-container").show();
		initMapGis( id_field );
		$("#results_list").find("input[name='geolocation']").each( function(index) 
		{
			if ( this.value == id_field )
			{
				/* get x and y sibilings */
				var xs = $(this).siblings("input[name=x]");
				var ys = $(this).siblings("input[name=y]");
				if ( xs != null && xs.length > 0 && ys != null && ys.length > 0 )
				{
					var xValue = xs[0].value;
					var yValue = ys[0].value;
					
					var parent = $(this).parent().parent("tr");
					var labelHtml = $(parent).children("td.link-directory-record").html();
					if ( !labelHtml )
					{
						// get actions [back office]
						labelHtml = $(this).siblings("#" + id_field + "_span_actions").html();
					}
					labelHtml += "&#160;";
					
					var icons = $(this).siblings("input[name=state_icon]");
					var icon = null;
					if ( icons != null && icons.length > 0 )
					{
						icon = icons[0].value;
					}
					
					var text = "<br/>";
					
					var record_ids = $(this).siblings("input[name=record_id]");
					if ( record_ids != null && record_ids.length > 0 )
					{
						recordId = record_ids[0].value;
						if ( record_titles != null )
						{
							text += record_titles[recordId];
						}
					}
					
					labelHtml += text;
					var labelText = text;
					
					if( ( !isNaN( xValue ) && !isNaN( yValue ) ) && ( xValue != 0 && yValue != 0 ) )
					{
						addEntityOnMap( id_field, xValue, yValue, labelHtml, labelText, $("#" + id_field + "_elements-list"), icon );
					}
				}
			}
		});
	});
}

function initMapGis( idField ) {
	var idMapDiv = idField + "_map_view";
	
	var optionalParameters = {};

	$( "#" +idMapDiv ).data('optionalParameters', optionalParameters);	
	var baseUrl = $('base').attr('href');
	$.get(baseUrl +'/jsp/site/plugins/gis/DoDisplayMap.jsp?map_name='+idMapDiv+'&gis_code='+"DIRECTORY", 
			function(data){
				$("#"+idMapDiv).html(data);
				$("#"+idMapDiv).show();
			});

}

function addEntityOnMap ( idField, xValue, yValue, labelHtml, labelText, sideNode, icon) {
	$('body').bind('GisMap.displayComplete', function () {
		var poi = {
			x: xValue,
			y: yValue,
			srid: "EPSG:4326"
		};
		setTimeout(function() {
			$("body").trigger(
				jQuery.Event("GisLocalization.addFeature", {
						poi: poi,
						label: labelHtml
					})
			);
			// Activate popup
			$("body").trigger(
					jQuery.Event("GisLocalization.activatePopup")
				);
		}, 1000);
	});

}

/**
 * Trim function
 */
function trim( strToTrim ) {

	if ( strToTrim == null )
	{
		return null;
	}
	
    return strToTrim.replace(/^\s+/, "").replace(/\s+$/, "");
}