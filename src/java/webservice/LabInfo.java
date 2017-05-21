package webservice;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import model.Laboratory;

@Stateless
@Path("labinfo")
public class LabInfo {
    private final String dbPath = "/GlassFish/lab.db";
    
    @GET
    public String info(){
        return "Laboratories Information Web Service";
    }
    
    @GET
    @Path("/labs")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Laboratory> getAllLaboratories(){
        List<Laboratory> laboratories = new LinkedList<>();
        
        Connection c;
        Statement stm;
        try{
            c = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            
            stm = c.createStatement();
            ResultSet res = stm.executeQuery("SELECT * FROM labs;");
            
            while(res.next()){
                laboratories.add(new Laboratory(res.getInt("id"), res.getString("name"), res.getInt("capacity")));
            }
            
            res.close();
            stm.close();
            c.close();
        } catch (SQLException e) {
            return laboratories;
        }
        return laboratories;
    }
    
    @GET
    @Path("/lab")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Laboratory getLaboratory(@QueryParam("name") String name){
        Laboratory laboratory = null;
                
        Connection c;
        Statement stm;
        try{
            c = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            
            stm = c.createStatement();
            ResultSet res = stm.executeQuery("SELECT * FROM labs WHERE name='" + name + "';");
            
            while(res.next()){
                final int id = res.getInt("id");
                final int capacity = res.getInt("capacity");
                
                laboratory = new Laboratory(id, name, capacity);
            }
            
            res.close();
            stm.close();
            c.close();
        } catch (SQLException e) {
            return laboratory;
        }
        return laboratory;
    }
    
    @POST
    @Path("/addLab")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public boolean addLaboratory(Laboratory lab){
        Connection c;
        Statement stm;
        try{
            c = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            
            stm = c.createStatement();
            
            final String name = lab.getName();
            final int capacity = lab.getCapacity();
            
            stm.executeUpdate("INSERT INTO labs(name,capacity) " +
                    "VALUES ('" + name + "', " + capacity + ");");
            
            stm.close();
            c.close();
        } catch (SQLException e) {
            return false;
        }
        return true;
    }
    
    @GET
    @Path("/delLab")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public boolean deleteLab(@QueryParam("name") String name){
        Connection c;
        Statement stm;
        try{
            c = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            
            stm = c.createStatement();
            stm.executeUpdate("DELETE FROM labs WHERE name='" + name + "';");
            
            stm.close();
            c.close();
        } catch (SQLException e) {
            return false;
        }
        return true;
    }
}
