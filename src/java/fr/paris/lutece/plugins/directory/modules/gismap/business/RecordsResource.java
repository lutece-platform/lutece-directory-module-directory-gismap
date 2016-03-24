package fr.paris.lutece.plugins.directory.modules.gismap.business;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import fr.paris.lutece.plugins.directory.business.Field;
import fr.paris.lutece.plugins.directory.business.IEntry;
import fr.paris.lutece.plugins.directory.business.RecordField;
import fr.paris.lutece.plugins.directory.business.RecordFieldHome;
import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


@Path("/rest/directory-gismap/")
public class RecordsResource 
{
	public static final String GISMAP_VIEW = "gismap.view.";
	public static final String PARAMETER = ".parameter.";
	public static final String POPUPSHOWLINK = "Popup_ShowLink";
    public static final String POPUP1 = "Popup1";
    
	@GET
	@Path("listRecordField/{listId}")
	@Produces( MediaType.APPLICATION_JSON )
	public String getListRecordField(@PathParam("listId") String strListId) 
	{
		String[] strListIdTab = strListId !=null ? strListId.split(",") : null;
		JSONObject collection = new JSONObject();
		collection.accumulate("type", "FeatureCollection");
		
		JSONArray array = new JSONArray();
		if(strListIdTab!=null)
		{
			for(int i=0;i<strListIdTab.length;i++)
			{
				int nIdRecordFiels = Integer.parseInt(strListIdTab[i]);
				RecordField recordField = RecordFieldHome.findByPrimaryKey(nIdRecordFiels, DirectoryUtils.getPlugin());
				if(recordField!=null)
				{
					Field field = recordField.getField();
					if(field!=null && field.getTitle().compareTo("geometry")==0)
					{
						JSONObject jsonElementValue = new JSONObject();
						jsonElementValue.accumulate("elementvalue", recordField.getValue());
						  
						JSONObject jsonElement = new JSONObject();
						jsonElement.accumulate("type", "Feature");
						jsonElement.accumulate("id", recordField.getRecord().getIdRecord());
						jsonElement.accumulate("properties", getProperties(recordField, strListId, getViewNumber(strListId)));
						jsonElement.accumulate("geometry", jsonElementValue.getJSONObject("elementvalue").getJSONObject("geometry"));
						
						array.add(jsonElement);
					 }
				  }
			 }
		}
		if(array.size()>0)
			collection.accumulate("features", array);
		
		return "callback("+collection.toString()+")";
   }
   
   public JSONObject getProperties(RecordField recordFieldParam, String strListId, int nKey)
   {
	   //String strPopupShowLinkProperty = AppPropertiesService.getProperty( GISMAP_VIEW + nKey + PARAMETER  + POPUPSHOWLINK );
	   String strPopup1Property = AppPropertiesService.getProperty( GISMAP_VIEW + nKey + PARAMETER  + POPUP1 );
	   String[] strrPopup1PropertyArray = strPopup1Property.split(",");
	   String[] strProperties = null;
	   if(strrPopup1PropertyArray.length>1)
	   {
		   strProperties = strrPopup1PropertyArray[1].split(";");
	   }
	   
	   String[] strListIdTab = strListId.split(",");
	   
	   JSONObject properties = new JSONObject();
	   
	   if(strProperties!=null)
	   {
		   for(int i=0;i<strListIdTab.length;i++)
		   {
			   int nIdRecordFiels = Integer.parseInt(strListIdTab[i]);
			   RecordField recordField = RecordFieldHome.findByPrimaryKey(nIdRecordFiels, DirectoryUtils.getPlugin());
			   //Field field = recordField.getField();
			   if(recordField.getEntry().getIdEntry() != recordFieldParam.getEntry().getIdEntry() && 
					   recordField.getRecord().getIdRecord() == recordFieldParam.getRecord().getIdRecord())
			   {
				   IEntry entry = recordField.getEntry();
				   if(inProperties(strProperties,entry.getTitle()))
					   properties.accumulate(entry.getTitle(), recordField.getValue());
			   }
		   }
	   }
	   
	   
	   if(properties.size()==0)
		   return null;
	   
	   return properties;
   }
   
   public boolean inProperties(String[] properties, String strProperty)
   {
	   boolean find = false;
	   for(int i=0; !find && i<properties.length; i++)
	   {
		   find = properties[i].compareTo("'"+strProperty+"'")==0;
	   }
	   return find;
   }
   
   public int getViewNumber( String strListId )
   {
	   String[] strListIdTab = strListId.split(",");
	   String strViewNumberValue = null;
	   if(strListIdTab!=null)
		{
			for(int i=0;i<strListIdTab.length;i++)
			{
				int nIdRecordFiels = Integer.parseInt(strListIdTab[i]);
				RecordField recordField = RecordFieldHome.findByPrimaryKey(nIdRecordFiels, DirectoryUtils.getPlugin());
				if(recordField!=null)
				{
					Field field = recordField.getField();
					if(field!=null && field.getTitle().compareTo("viewNumber")==0)
					{
						strViewNumberValue = field.getValue();
						break;
					}
						
				}
			}
		}
	   return strViewNumberValue==null ? 1 : Integer.parseInt(strViewNumberValue);
   }
}
