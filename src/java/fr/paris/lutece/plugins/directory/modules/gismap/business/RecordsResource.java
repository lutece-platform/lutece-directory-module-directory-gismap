package fr.paris.lutece.plugins.directory.modules.gismap.business;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import fr.paris.lutece.plugins.directory.business.Field;
import fr.paris.lutece.plugins.directory.business.IEntry;
import fr.paris.lutece.plugins.directory.business.Record;
import fr.paris.lutece.plugins.directory.business.RecordField;
import fr.paris.lutece.plugins.directory.business.RecordFieldHome;
import fr.paris.lutece.plugins.directory.business.RecordHome;
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
    public static final String RMMSHOWCENTROID = "RenderMapManagement.ShowCentroid";
    
	@GET
	@Path("listRecordField/{listId}")
	@Produces( MediaType.APPLICATION_JSON )
	public String getListRecordField(@PathParam("listId") String strListId) 
	{
		String strRMMSHOWCENTROIDProperty = AppPropertiesService.getProperty( GISMAP_VIEW + getViewNumber(strListId) + PARAMETER  + RMMSHOWCENTROID );
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
						
						if(strRMMSHOWCENTROIDProperty != null && strRMMSHOWCENTROIDProperty.compareTo("true")==0)
						{
							jsonElement.accumulate("geometry", getCoordinatesXY(recordField, strListId));
						}
						else
						{
							jsonElement.accumulate("geometry", jsonElementValue.getJSONObject("elementvalue").getJSONObject("geometry"));
						}
						
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
	   String strPopupShowLinkProperty = AppPropertiesService.getProperty( GISMAP_VIEW + nKey + PARAMETER  + POPUPSHOWLINK );
	   String strPopup1Property = AppPropertiesService.getProperty( GISMAP_VIEW + nKey + PARAMETER  + POPUP1 );
	   String[] strrPopup1PropertyArray = strPopup1Property.split(",");
	   String[] strProperties = null;
	   
	   if(strrPopup1PropertyArray.length>1)
	   {
		   strProperties = getBestProperties(strrPopup1PropertyArray[1]).split(";");
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
		   Record record = RecordHome.findByPrimaryKey(recordFieldParam.getRecord().getIdRecord(), DirectoryUtils.getPlugin());
		   if(record.getDirectory()!=null)
		   {
			   if(strPopupShowLinkProperty.compareTo("true")==0)
				   properties.accumulate("link", getLink(record));
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
					if(field!=null && field.getTitle().compareTo("viewNumberGes")==0)
					{
						strViewNumberValue = field.getValue();
						break;
					}
						
				}
			}
		}
	   return strViewNumberValue==null ? 1 : Integer.parseInt(strViewNumberValue);
   }
   
   public String getLink(Record record)
   {
	   int idDirectoryRecord = record.getIdRecord();
	   int idDirectory = record.getDirectory().getIdDirectory();
	   return "jsp/admin/plugins/directory/DoVisualisationRecord.jsp?id_directory_record="+idDirectoryRecord+"&id_directory="+idDirectory;
   }
   
   public String getCoordinatesXY(RecordField recordFieldParam, String strListId)
   {
	 //"geometry":{"type":"Point","coordinates":[2.3009992,48.836666]}}
	   
	   String[] strListIdTab = strListId !=null ? strListId.split(",") : null;
	   
	   String strX="";
	   String strY="";
	   if(strListIdTab!=null)
	   {
		   for(int i=0;i<strListIdTab.length;i++)
		   {
			   int nIdRecordFiels = Integer.parseInt(strListIdTab[i]);
			   RecordField recordField = RecordFieldHome.findByPrimaryKey(nIdRecordFiels, DirectoryUtils.getPlugin());
			   if(recordField!=null && recordField.getRecord().getIdRecord()==recordFieldParam.getRecord().getIdRecord())
			   {
				   Field field = recordField.getField();
				   if(field!=null && field.getTitle().compareTo("X")==0)
				   {
					   strX = recordField.getValue();
					   break;
				   }
			   }
		   }
		   for(int i=0;i<strListIdTab.length;i++)
		   {
			   int nIdRecordFiels = Integer.parseInt(strListIdTab[i]);
			   RecordField recordField = RecordFieldHome.findByPrimaryKey(nIdRecordFiels, DirectoryUtils.getPlugin());
			   if(recordField!=null && recordField.getRecord().getIdRecord()==recordFieldParam.getRecord().getIdRecord())
			   {
				   Field field = recordField.getField();
				   if(field!=null && field.getTitle().compareTo("Y")==0)
				   {
					   strY = recordField.getValue();
					   break;
				   }
			   }
		   }
	   }
	   if(!strX.isEmpty() && !strY.isEmpty())
	   {
		   JSONObject jsonElement = new JSONObject();
		   jsonElement.accumulate("type", "Point");
		   jsonElement.accumulate("coordinates", "["+strX+","+strY+"]");
		   return jsonElement.toString();
	   }
	   
	   return null;
   }
   
   public String getBestProperties(String strProperties) // ['nom', 'prenom', 'link']
   {
	   return strProperties.split("[")[1].split("]")[0];
   }
   
}
