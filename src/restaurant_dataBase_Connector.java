import java.sql.*; 
public class restaurant_dataBase_Connector 
{
	Connection connector; 
	Statement query;
	public restaurant_dataBase_Connector()
	{
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			//can change below to match your database.
			connector=DriverManager.getConnection("jdbc:mysql://localhost:3306/restaurantapplication","root","");
			query=connector.createStatement();
		}
		catch(Exception e)
		{
			System.out.print(e);
		}
		
	}
	public ResultSet executeStatement(String sql)
	{
		try
		{
			return query.executeQuery(sql);
		}
		catch(Exception e)
		{
			return null;
		}
	}
	public void closeConnection()
	{
		try 
		{
			connector.close();
		} catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
