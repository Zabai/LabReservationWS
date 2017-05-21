package webservice;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import model.Laboratory;
import model.Reservation;

@Stateless
@Path("labres")
public class LabRes {
    private final String dbPath = "/GlassFish/lab.db";
    
    @GET
    public String info(){
        return "Laboratories Reservation Web Application";
    }
    
    @GET
    @Path("/test")
    @Produces(MediaType.APPLICATION_JSON)
    public Laboratory test(){
        return new Laboratory(1123, "JAJA", 12);
    }
    
    @GET
    @Path("/labsize")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public List<Laboratory> getLabsSize(@QueryParam("size") String size){
        List<Laboratory> laboratories = new LinkedList<>();
        
        Connection c;
        Statement stm;
        try{
            c = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            
            stm = c.createStatement();
            ResultSet res = stm.executeQuery("SELECT * FROM labs WHERE capacity>=" + size + ";");
            
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
    
    @POST
    @Path("/reservation")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public boolean addReservation(Reservation reservation) throws ParseException{
        Connection c;
        Statement stm;
        try{
            c = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            
            stm = c.createStatement();
            
            final String userName = reservation.getUserName();
            final String labName = reservation.getLabName();
            final String startTimeString = reservation.getStartTime();
            final String endTimeString = reservation.getEndTime();

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            
            long startTime = sdf.parse(startTimeString).getTime()/1000;
            long endTime = sdf.parse(endTimeString).getTime()/1000;
            
            ResultSet res = stm.executeQuery("SELECT COUNT(*) AS total FROM reservations WHERE " + 
                    "lab_name='" + labName + "' AND ((startTime BETWEEN " + startTime + " AND " + endTime + ")" +
                    " OR (endTime BETWEEN " + startTime + " AND " + endTime + "));");
            
            if(res.getInt("total") == 0){
                stm.executeUpdate("INSERT INTO reservations(lab_name,startTime,endTime,userName) " +
                                  "VALUES ('" + labName + "', " + startTime + ", " + endTime + ", '" + userName + "');");
            }else{
                res.close();
                stm.close();
                c.close();
                return false;
            }
            
            res.close();
            stm.close();
            c.close();
        } catch (SQLException e) {
            return false;
        }
        return true;
    }
    
    @GET
    @Path("/myReservations")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public List<Reservation> myReservations(@QueryParam("name") String name){
        List<Reservation> reservations = new LinkedList<>();
        
        Connection c;
        Statement stm;
        try{
            c = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            
            stm = c.createStatement();
            
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            
            ResultSet res = stm.executeQuery("SELECT * FROM reservations WHERE " + 
                    "userName='" + name + "';");
            
            while(res.next()) reservations.add(new Reservation(res.getInt("id"), 
                    res.getString("lab_name"), 
                    sdf.format(new Date(res.getLong("startTime")*1000)), 
                    sdf.format(new Date(res.getLong("endTime")*1000)), 
                    res.getString("userName")));
            
            res.close();
            stm.close();
            c.close();
        } catch (SQLException e) {
            return reservations;
        }
        return reservations;
    }
    
    @GET
    @Path("/showReservations")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Reservation> showReservations() throws ParseException{
        List<Reservation> reservations = new LinkedList<>();
        
        Connection c;
        Statement stm;
        try{
            c = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            
            stm = c.createStatement();
            
            final long startTime = System.currentTimeMillis()/1000;
            final long endTime = startTime + 604800;
            
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

            ResultSet res = stm.executeQuery("SELECT * FROM reservations WHERE " + 
                    "endTime BETWEEN " + startTime + " AND " + endTime + ";");
            
            while(res.next()) reservations.add(new Reservation(res.getInt("id"), 
                    res.getString("lab_name"), 
                    sdf.format(new Date(res.getLong("startTime")*1000)), 
                    sdf.format(new Date(res.getLong("endTime")*1000)), 
                    "---"));
            
            res.close();
            stm.close();
            c.close();
        } catch (SQLException e) {
            return reservations;
        }
        return reservations;
    }
    
    @GET
    @Path("/getFreeLabs")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Integer> getFreeLabs(){
        List<Integer> laboratories = new LinkedList<>();
        
        Connection c;
        Statement stm;
        try{
            c = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            
            stm = c.createStatement();
            
            long currentTime = System.currentTimeMillis();
            ResultSet res = stm.executeQuery("SELECT DISTINCT id FROM reservations WHERE startTime>'" + currentTime + " AND endTime <'" + currentTime + "';");
            ResultSet res1 = stm.executeQuery("SELECT id FROM labs WHERE id NOT'" + res + "';");

            while(res1.next()){
                laboratories.add(res.getInt("id"));
            }
            
            res.close();
            res1.close();
            stm.close();
            c.close();
        } catch (SQLException e) {
            return laboratories;
        }
        return laboratories;
    }
}
