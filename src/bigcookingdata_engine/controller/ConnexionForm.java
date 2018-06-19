package bigcookingdata_engine.controller;

import bigcookingdata_engine.business.engine.Session;
import bigcookingdata_engine.database.Neo4J;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet("/LoginServlet")
public class ConnexionForm extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.getRequestDispatcher("login.jsp").forward(request, response);

	}
	
	
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String user = request.getParameter("email");
		String pwd = request.getParameter("pwd");
		String name = Session.getInstance().connection( user,pwd );
        
        if (name != null && Neo4J.connection(user, pwd)!=null){
            Cookie loginCookie = new Cookie("user",name);
            loginCookie.setMaxAge(30*60);
            response.addCookie(loginCookie);
            RequestDispatcher rd = getServletContext().getRequestDispatcher("/profile.jsp");
            PrintWriter out= response.getWriter();
            out.println("<script>\n" + 
            		"\n" + 
            		"    alert(\"Heureu de vous revoir parmis nous Mr "+ name +" !\");\n" + 
            		"\n" + 
            		"</script>\n" + 
            		"");
            rd.include(request, response);
            
            
            //request.setAttribute("user", user);
    		//request.getRequestDispatcher("profile.jsp").forward(request, response);
           // response.sendRedirect("profile.jsp");
        }
        else {
            RequestDispatcher rd = getServletContext().getRequestDispatcher("/login.jsp");
            PrintWriter out= response.getWriter();
            out.println("<font color=red>Nom d'utilisateur ou mot de passe incorrecte.</font>");
            rd.include(request, response);
        }
	}

}
