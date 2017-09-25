-- Update gismap portlet tables

-- ------------------------------------------------------------------------------------------------------
-- WARNING!!!! If You are upgrading from GISMAP version 1.1.3 with existing gismap portlet instances, just execute the 'Replace' query below and stop:
-- ---------------------------------------------------------------------------------------------------------------------------------


REPLACE INTO core_portlet_type (id_portlet_type,name,url_creation,url_update,home_class,plugin_name,url_docreate,create_script,create_specific,create_specific_form,url_domodify,modify_script,modify_specific,modify_specific_form) VALUES 
('GISMAP_PORTLET','gismap.portlet.name','plugins/directory/modules/gismap/CreatePortletDirectoryGismap.jsp','plugins/directory/modules/gismap/ModifyPortletDirectoryGismap.jsp','fr.paris.lutece.plugins.directory.modules.gismap.business.portlet.GismapDirectoryPortletHome','gismap','plugins/directory/modules/gismap/DoCreatePortletDirectoryGismap.jsp','','/admin/plugins/directory/modules/gismap/list_directory.html','','plugins/directory/modules/gismap/DoModifyPortletDirectoryGismap.jsp','','/admin/plugins/directory/modules/gismap/list_directory.html','');


-- ------------------------------------------------------------------------------------------------------
-- WARNING!!!! If You are upgrading from GISMAP version BEFORE 1.1.3 without existing gismap portlet instances,
--  you need to uncomment and execute the queries below to create the GISMAP portlet model
-- 
-- ---------------------------------------------------------------------------------------------------------------------------------

--
-- Table structure for table gismap_portlet
--
-- DROP TABLE IF EXISTS gismap_portlet;
-- CREATE TABLE gismap_portlet (
--	 id_portlet int default NULL,
--	 id_directory int default NULL
-- );

-- Dumping data for table core_portlet_type
--
-- INSERT INTO core_portlet_type (id_portlet_type,name,url_creation,url_update,home_class,plugin_name,url_docreate,create_script,create_specific,create_specific_form,url_domodify,modify_script,modify_specific,modify_specific_form) VALUES 
-- ('GISMAP_PORTLET','gismap.portlet.name','plugins/directory/modules/gismap/CreatePortletDirectoryGismap.jsp','plugins/directory/modules/gismap/ModifyPortletDirectoryGismap.jsp','fr.paris.lutece.plugins.directory.modules.gismap.business.portlet.GismapDirectoryPortletHome','gismap','plugins/directory/modules/gismap/DoCreatePortletDirectoryGismap.jsp','','/admin/plugins/directory/modules/gismap/list_directory.html','','plugins/directory/modules/gismap/DoModifyPortletDirectoryGismap.jsp','','/admin/plugins/directory/modules/gismap/list_directory.html','');

--
-- Dumping data for table `core_style`
--
-- INSERT INTO core_style (id_style, description_style, id_portlet_type, id_portal_component) VALUES (1501,'Défaut','GISMAP_PORTLET',0);

-- INSERT INTO core_stylesheet (id_stylesheet, description, file_name, source) VALUES (9000,'Rubrique gismap - Défaut','portlet_gismap.xsl',0x3C3F786D6C2076657273696F6E3D22312E30223F3E0D0A3C78736C3A7374796C6573686565742076657273696F6E3D22312E302220786D6C6E733A78736C3D22687474703A2F2F7777772E77332E6F72672F313939392F58534C2F5472616E73666F726D223E0D0A3C78736C3A6F7574707574206D6574686F643D2268746D6C2220696E64656E743D22796573222F3E0D0A3C78736C3A74656D706C617465206D617463683D22706F72746C6574223E0D0A093C64697620636C6173733D22706F72746C6574202D6C75746563652D626F726465722D72616469757320617070656E642D626F74746F6D223E0D0A09093C78736C3A6170706C792D74656D706C617465732073656C6563743D226769736D61702D706F72746C657422202F3E0D0A093C2F6469763E0D0A3C2F78736C3A74656D706C6174653E0D0A0D0A3C78736C3A74656D706C617465206D617463683D226769736D61702D706F72746C6574223E0D0A093C78736C3A6170706C792D74656D706C617465732073656C6563743D226769736D61702D706F72746C65742D636F6E74656E7422202F3E0D0A3C2F78736C3A74656D706C6174653E0D0A0D0A3C78736C3A74656D706C617465206D617463683D226769736D61702D706F72746C65742D636F6E74656E74223E0D0A093C78736C3A76616C75652D6F662064697361626C652D6F75747075742D6573636170696E673D22796573222073656C6563743D222E22202F3E0D0A3C2F78736C3A74656D706C6174653E0D0A0D0A3C2F78736C3A7374796C6573686565743E);
-- INSERT INTO core_style_mode_stylesheet (id_style, id_mode, id_stylesheet) VALUES (1501,0,9000);