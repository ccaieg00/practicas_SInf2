/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practica1_si2;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import modelo.HibernateUtil;
import modulo.Categorias;
import modulo.Empresas;
import modulo.Trabajadorbbdd;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 *
 * @author pablo
 */
public class Practica1_SI2 {

    /**
     * @param args the command line arguments
     */
    static SessionFactory sf;
    static Session session;
    static Transaction tx;
    static String categoriaTrabajador;

    
    public static void main(String[] args) {
        // TODO code application logic here
        sf = HibernateUtil.getSessionFactory();
        session = sf.openSession();
        tx = null;
        Categorias categoria = new Categorias();
        List<Object[]> lista;
         
        System.out.println("Introduce NIF: ");
        Scanner sc = new Scanner(System.in);
        String nif = sc.nextLine();

        String consulta = ""
                + "SELECT t.nombre, t.apellido1, t.apellido2, t.nifnie, c.nombreCategoria, e.nombre, c.salarioBaseCategoria "
                + "FROM Trabajadorbbdd t, Empresas e, Categorias c "
                + "WHERE (t.nifnie=:param1) AND (t.categorias=c.idCategoria) AND (t.empresas=e.idEmpresa)";
        
        Query query = session.createQuery(consulta);
        query.setParameter("param1", nif);
        lista = query.list();
        mostrar(lista);
        //cerrarConexion();

        
        
        // 2. INCREMENTAR SALARIO BASE //
        Categorias arrayCategorias[]= null;
        /*String consultaAux = ""
                + "SELECT t.categorias.idCategoria "
                + "FROM Trabajadorbbdd t "
                + "WHERE t.nifnie=:param1";*/
        String consultaAux = ""
                + "FROM Categorias c "
                + "WHERE c.nombreCategoria!=:param1";
        Query queryAux = session.createQuery(consultaAux);
        queryAux.setParameter("param1", categoriaTrabajador);
        List listaAux = queryAux.list();
        
        if(!listaAux.isEmpty()){
            arrayCategorias = new Categorias[listaAux.size()];
            for(int i=0 ; i<listaAux.size() ; i++)
            {
                arrayCategorias[i] = (Categorias) listaAux.get(i);
                System.out.println("CATEGORIA ACTUAL: " + arrayCategorias[i].getNombreCategoria().toString());
                
                tx = session.beginTransaction();
                String consulta2 = ""
                        + "UPDATE Categorias "
                        + "SET salarioBaseCategoria = salarioBaseCategoria + 200 "
                        + "WHERE nombreCategoria=:param1";
                Query query2 = session.createQuery(consulta2);
                query2.setParameter("param1", arrayCategorias[i].getNombreCategoria().toString());
                query2.executeUpdate();
                tx.commit();                
            }
        }
        
        
        ////////////////////////// 3. DELETE ///////////////////////////////
        Trabajadorbbdd trabajadores = null;
        try {
            //tx = session.beginTransaction();
            consultaAux = "FROM Trabajadorbbdd t WHERE t.nifnie=:param1";
            queryAux = session.createQuery(consultaAux);//.createQuery(consulta2);
            queryAux.setParameter("param1", nif);
            List listaBuena = queryAux.list();
        
            
            if(!listaBuena.isEmpty()){
                    trabajadores = (Trabajadorbbdd) listaBuena.get(0);
            }
            System.out.println(trabajadores.getNombre());
            
            try {
                
            } catch (Exception e) {
            }
            String buscarTrabajadores = "From Trabajadorbbdd t WHERE t.empresas.idEmpresa=:param1";
            Query queryAux2 = session.createQuery(buscarTrabajadores);//.createQuery(consulta2);
            queryAux2.setParameter("param1", trabajadores.getEmpresas().getIdEmpresa());
            List listaBuena2 = queryAux2.list();
        
            Trabajadorbbdd [] trabajadores2=null;
            if(!listaBuena2.isEmpty()){
                trabajadores2 = new Trabajadorbbdd[listaBuena2.size()];
                for(int i=0; i<listaBuena2.size(); i++){
                    trabajadores2[i] = (Trabajadorbbdd) listaBuena2.get(i);
                    System.out.println("numero en el for: " + i);
                    System.out.println(trabajadores2[i].getNombre());
                }
                System.out.println("todo bien");
                //tx = session.beginTransaction();
                Empresas empre = new Empresas();
                Trabajadorbbdd trabaja = new Trabajadorbbdd();

                //empre.setIdEmpresa(a);
                //String consulta2 = "DELETE Trabajadorbbdd t WHERE (t.empresas=:nomEmpresa)";
                //String consulta2 = "DELETE Trabajadorbbdd t  WHERE (t.empresas.idEmpresa=:nomEmpresa)";// AND (trabajadorbbdd.empresas.idEmpresa=:nomEmpresa)";
                //String consulta2 = "DELETE Nomina n WHERE (n.trabajadorbbdd.idTrabajador=:param1)";
                try{
                //tx=session.beginTransaction();
                    for(int i=0 ; i<trabajadores2.length ; i++){
                        tx = session.beginTransaction();
                        System.out.println("ID TRABAJADOR A BORRAR LAS NOMINAS: " + trabajadores2[i].getIdTrabajador());
                        String consultaElimNom = "DELETE Nomina n WHERE n.trabajadorbbdd.idTrabajador = :param1";
                        session.createQuery(consultaElimNom).setParameter("param1", trabajadores2[i].getIdTrabajador()).executeUpdate();//.createQuery(consulta2);
                        tx.commit();
                        
                        tx = session.beginTransaction();
                        String consultaElimTrab = "DELETE Trabajadorbbdd t WHERE t.idTrabajador = :param1";
                        session.createQuery(consultaElimTrab).setParameter("param1", trabajadores2[i].getIdTrabajador()).executeUpdate();//.createQuery(consulta2);
                        tx.commit();
                    }
                }
                catch(Exception e){
                    System.out.println("Error de eliminar nominas "+e.getMessage());
                }



            }else{
                System.out.println("Error en el array de trabjadores");
            }
        
        } catch (Exception e) {
            System.out.println("Error consultaAux: "+e);
        }

       
        cerrarSesion();
        cerrarPrograma();

        System.exit(0);
    }
    
    
    public static void mostrar(List<Object []> lista){
        if(lista.size()==0){
            System.err.println("Ha habido un error. El nif indicado no existe en la base de datos.");
        }
        else{
            for(Object [] datos: lista){
                System.out.println(datos[0]+"  "+datos[1]+"  "+datos[2]+"  "+datos[3]+"  "+datos[4]+"  "+datos[5]+"  "+datos[6]);
                categoriaTrabajador = datos[4].toString();
            }
        }
    }
    
    public static void cerrarSesion()
    {
        session.clear();
        session.close();
    }
    
    public static void cerrarPrograma(){
        System.exit(0);
    }
}
