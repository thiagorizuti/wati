/*
 * To change this template, choose Tools | Templates
 * and open the template in 
 the editor.
 */
package wati.controller;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.naming.NamingException;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import wati.model.ProntoParaParar;
import wati.model.User;
import wati.persistence.GenericDAO;
import wati.utility.EMailSSL;

/**
 *
 * @author hedersb
 */
@ManagedBean(name = "prontoParaPararController")
@SessionScoped
public class ProntoParaPararController extends BaseController<ProntoParaParar> {

    private ProntoParaParar prontoParaParar = null;
    private String[] vencendoAFissuraMarcados = null;
    private static final int VENCENDO_FISSURA_BEBER_AGUA = 0;
    private static final int VENCENDO_FISSURA_COMER = 1;
    private static final int VENCENDO_FISSURA_RELAXAMENTO = 2;
    private static final int VENCENDO_FISSURA_LER_RAZOES = 3;
    private Map<String, String> anos = new LinkedHashMap<String, String>();
    private GregorianCalendar gregorianCalendar = null;
    private StreamedContent planoPersonalizado;

    public ProntoParaPararController() {
        //super(ProntoParaParar.class);
        try {
            this.daoBase = new GenericDAO<ProntoParaParar>(ProntoParaParar.class);
        } catch (NamingException ex) {
            String message = this.getText("mensagem.erro");
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, message, null));
            Logger.getLogger(ProntoParaPararController.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }

        GregorianCalendar gc = (GregorianCalendar) GregorianCalendar.getInstance();
        int firstYear = gc.get(GregorianCalendar.YEAR);
        for (int i = firstYear; i < firstYear + 5; i++) {
            anos.put(String.valueOf(i), String.valueOf(i));
        }

    }

    public String vencendoAFissura() {
        this.prontoParaParar.limparVencendoFissura();

        for (String string : vencendoAFissuraMarcados) {

            switch (Integer.valueOf(string)) {
                case VENCENDO_FISSURA_BEBER_AGUA:
                    this.prontoParaParar.setEnfrentarFissuraBeberAgua(true);
                    break;
                case VENCENDO_FISSURA_COMER:
                    this.prontoParaParar.setEnfrentarFissuraComer(true);
                    break;
                case VENCENDO_FISSURA_LER_RAZOES:
                    this.prontoParaParar.setEnfrentarFissuraLerRazoes(true);
                    break;
                case VENCENDO_FISSURA_RELAXAMENTO:
                    this.prontoParaParar.setEnfrentarFissuraRelaxamento(true);
                    break;
            }

        }
        try {
            this.getDaoBase().insertOrUpdate(prontoParaParar, this.getEntityManager());
            return "pronto-para-parar-de-fumar-medicamentos.xhtml";
        } catch (SQLException ex) {
            Logger.getLogger(ProntoParaPararController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String fumarMetodosDeParar() {

        return "pronto-para-parar-de-fumar-metodos-de-parar.xhtml";

    }

    public String dataParaParar() {

//		if ( this.ano.equals("Atual") ) {
//			this.ano = String.valueOf( new GregorianCalendar().get( GregorianCalendar.YEAR ) );
//		}
//		
//		if ( this.mes.equals("Atual") ) {
//			this.mes = String.valueOf( new GregorianCalendar().get( GregorianCalendar.MONTH ) );
//		}
//		
//		if ( this.dia.equals("Atual") ) {
//			this.dia = String.valueOf( new GregorianCalendar().get( GregorianCalendar.DAY_OF_MONTH ) );
//		}
        this.prontoParaParar.setDataParar(this.gregorianCalendar.getTime());

        try {
            this.getDaoBase().insertOrUpdate(prontoParaParar, this.getEntityManager());
            return "pronto-para-parar-de-fumar-como-evitar-recaidas.xhtml";
        } catch (SQLException ex) {
            Logger.getLogger(ProntoParaPararController.class.getName()).log(Level.SEVERE, null, ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, this.getText("problemas.data"), null));
        }
        return null;
    }

    public String recaidaTentouParar() {

        try {
            this.getDaoBase().insertOrUpdate(this.prontoParaParar, this.getEntityManager());
            if (this.prontoParaParar.isTentouParar()) {
                return "pronto-para-parar-de-fumar-como-evitar-recaidas-sim.xhtml";
            } else {
                return "pronto-para-parar-de-fumar-como-evitar-recaidas-completo.xhtml";
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProntoParaPararController.class.getName()).log(Level.SEVERE, null, ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, this.getText("problemas.data"), null));
        }
        return null;
    }

    public String recaidasAjudar() {

        try {
            this.getDaoBase().insertOrUpdate(this.prontoParaParar, this.getEntityManager());
            return "pronto-para-parar-de-fumar-como-evitar-recaidas-completo.xhtml";
            //return "pronto-para-parar-de-fumar-ganho-de-peso.xhtml";
        } catch (SQLException ex) {
            Logger.getLogger(ProntoParaPararController.class.getName()).log(Level.SEVERE, null, ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, this.getText("problemas.data"), null));
        }
        return null;
    }

    public String recaidasEvitar() {

        try {
            this.getDaoBase().insertOrUpdate(this.prontoParaParar, this.getEntityManager());
            return "pronto-para-parar-de-fumar-ganho-de-peso.xhtml";
        } catch (SQLException ex) {
            Logger.getLogger(ProntoParaPararController.class.getName()).log(Level.SEVERE, null, ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, this.getText("problemas.data"), null));
        }
        return null;
    }

    /**
     * @return the vencendoAFissuraMarcados
     */
    public String[] getVencendoAFissuraMarcados() {

        if (this.vencendoAFissuraMarcados == null) {

            ProntoParaParar ppp = this.getProntoParaParar();

            int count = 0;
            if (ppp.isEnfrentarFissuraBeberAgua()) {
                count++;
            }
            if (ppp.isEnfrentarFissuraComer()) {
                count++;
            }
            if (ppp.isEnfrentarFissuraLerRazoes()) {
                count++;
            }
            if (ppp.isEnfrentarFissuraRelaxamento()) {
                count++;
            }
            this.vencendoAFissuraMarcados = new String[count];
            count = 0;
            if (ppp.isEnfrentarFissuraBeberAgua()) {
                this.vencendoAFissuraMarcados[ count] = String.valueOf(ProntoParaPararController.VENCENDO_FISSURA_BEBER_AGUA);
                count++;
            }
            if (ppp.isEnfrentarFissuraComer()) {
                this.vencendoAFissuraMarcados[ count] = String.valueOf(ProntoParaPararController.VENCENDO_FISSURA_COMER);
                count++;
            }
            if (ppp.isEnfrentarFissuraLerRazoes()) {
                this.vencendoAFissuraMarcados[ count] = String.valueOf(ProntoParaPararController.VENCENDO_FISSURA_LER_RAZOES);
                count++;
            }
            if (ppp.isEnfrentarFissuraRelaxamento()) {
                this.vencendoAFissuraMarcados[ count] = String.valueOf(ProntoParaPararController.VENCENDO_FISSURA_RELAXAMENTO);
                count++;
            }
        }

        return vencendoAFissuraMarcados;
    }

    /**
     * @param vencendoAFissuraMarcados the vencendoAFissuraMarcados to set
     */
    public void setVencendoAFissuraMarcados(String[] vencendoAFissuraMarcados) {
        this.vencendoAFissuraMarcados = vencendoAFissuraMarcados;
    }

    public void save(ActionEvent actionEvent) {
    }

    /**
     * @return the dia
     */
    public int getDia() {
        if (this.gregorianCalendar == null) {
            getProntoParaParar();
        }
        return this.gregorianCalendar.get(GregorianCalendar.DAY_OF_MONTH);
    }

    /**
     * @param dia the dia to set
     */
    public void setDia(int dia) {
        if (this.gregorianCalendar == null) {
            getProntoParaParar();
        }
        this.gregorianCalendar.set(GregorianCalendar.DAY_OF_MONTH, dia);
    }

    /**
     * @return the mes
     */
    public int getMes() {
        if (this.gregorianCalendar == null) {
            getProntoParaParar();
        }
        return this.gregorianCalendar.get(GregorianCalendar.MONTH);
    }

    /**
     * @param mes the mes to set
     */
    public void setMes(int mes) {
        if (this.gregorianCalendar == null) {
            getProntoParaParar();
        }
        this.gregorianCalendar.set(GregorianCalendar.MONTH, mes);
    }

    /**
     * @return the ano
     */
    public int getAno() {
        if (this.gregorianCalendar == null) {
            getProntoParaParar();
        }
        return this.gregorianCalendar.get(GregorianCalendar.YEAR);
    }

    /**
     * @param ano the ano to set
     */
    public void setAno(int ano) {
        if (this.gregorianCalendar == null) {
            getProntoParaParar();
        }
        this.gregorianCalendar.set(GregorianCalendar.YEAR, ano);
    }

    /**
     * @return the prontoParaParar
     */
    public ProntoParaParar getProntoParaParar() {
        if (this.prontoParaParar == null) {
            this.gregorianCalendar = (GregorianCalendar) GregorianCalendar.getInstance();
            Object object = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("loggedUser");
            if (object != null) {
                try {
                    //System.out.println( "inject: " + this.loginController.getUser().toString() );
                    //List<ProntoParaParar> ppps = this.getDaoBase().list("usuario.id", ((User)object).getId(), this.getEntityManager());
                    List<ProntoParaParar> ppps = this.getDaoBase().list("usuario", object, this.getEntityManager());
                    if (ppps.size() > 0) {
                        this.prontoParaParar = ppps.get(0);
                        this.gregorianCalendar.setTime(this.prontoParaParar.getDataParar());
                    } else {
                        this.prontoParaParar = new ProntoParaParar();
                        this.prontoParaParar.setDataParar(this.gregorianCalendar.getTime());
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(ProntoParaPararController.class.getName()).log(Level.SEVERE, null, ex);
                }
                this.prontoParaParar.setUsuario((User) object);
            } else {
                Logger.getLogger(ProntoParaPararController.class.getName()).log(Level.SEVERE, this.getText("usuario.preenchendo.ficha"));
            }
        }

        return prontoParaParar;
    }

    /**
     * @param prontoParaParar the prontoParaParar to set
     */
    public void setProntoParaParar(ProntoParaParar prontoParaParar) {
        this.prontoParaParar = prontoParaParar;
    }

    /**
     * @return the anos
     */
    public Map<String, String> getAnos() {
        return anos;
    }

    /**
     * @param anos the anos to set
     */
    public void setAnos(Map<String, String> anos) {
        this.anos = anos;
    }

    public List<ProntoParaParar> getProntoParaPararList() {
        ArrayList<ProntoParaParar> arrayList = new ArrayList<ProntoParaParar>();
        arrayList.add(prontoParaParar);
        return arrayList;
    }

    public boolean isEvitarRecaidaFara11() {
        return prontoParaParar.getEvitarRecaidaFara1() != null && !prontoParaParar.getEvitarRecaidaFara1().trim().equals("");
    }

    public boolean isEvitarRecaidaFara22() {
        return prontoParaParar.getEvitarRecaidaFara2() != null && !prontoParaParar.getEvitarRecaidaFara2().trim().equals("");
    }

    public boolean isEvitarRecaidaFara33() {
        return prontoParaParar.getEvitarRecaidaFara3() != null && !prontoParaParar.getEvitarRecaidaFara3().trim().equals("");
    }

    public boolean isEvitarRecaida11() {
        return prontoParaParar.getEvitarRecaida1() != null && !prontoParaParar.getEvitarRecaida1().trim().equals("");
    }

    public boolean isEvitarRecaida22() {
        return prontoParaParar.getEvitarRecaida2() != null && !prontoParaParar.getEvitarRecaida2().trim().equals("");
    }

    public boolean isEvitarRecaida33() {
        return prontoParaParar.getEvitarRecaida3() != null && !prontoParaParar.getEvitarRecaida3().trim().equals("");
    }

    public boolean tecnicaFissura() {
        return false;
    }

    public boolean fissuras() {
        if (prontoParaParar.isEnfrentarFissuraBeberAgua() == false && prontoParaParar.isEnfrentarFissuraComer() == false && prontoParaParar.isEnfrentarFissuraLerRazoes() == false && prontoParaParar.isEnfrentarFissuraRelaxamento() == false) {
            return false;
        } else {
            return true;
        }
    }

    public boolean estrategias() {
        return isEvitarRecaidaFara11() || isEvitarRecaidaFara22() || isEvitarRecaidaFara33();
    }

    public boolean deucerto() {
        return isEvitarRecaida11() || isEvitarRecaida22() || isEvitarRecaida33();
    }

    public void enviarEmail() {

        Object object = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("loggedUser");

        if (object == null) {

            Logger.getLogger(ProntoParaPararController.class.getName()).log(Level.SEVERE, this.getText("user.not.logged"));
            //message to the user
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, this.getText("deve.estar.logado"), null));

        } else {

            User user = (User) object;

            try {
                String from = "watiufjf@gmail.com";
                String to = user.getEmail();
                String subject = this.getText("plano.wati2");

                String html = this.defaultEmail(this.getText("plano.personalizado"), this.planoToHTML(user));

                EMailSSL eMailSSL = new EMailSSL();
                
                eMailSSL.send(from, to, subject, this.planoToText(user), html, gerarPdf());

                Logger.getLogger(ProntoParaPararController.class.getName()).log(Level.INFO, this.getText("plano.enviado2") + user.getEmail() + ".");
                //message to the user
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, this.getText("email.enviado"), null));

            } catch (Exception ex) {

                Logger.getLogger(ProntoParaPararController.class.getName()).log(Level.SEVERE, this.getText("problemas.enviar.email") + user.getEmail() + ".");
                //message to the user
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, this.getText("problemas.enviar.email2"), null));

            }

        }

    }

    public String planoToHTML(User user) {
        String html = this.getText("dear") + user.getName() + ",<br>"
                + "<br>" + this.getText("plano.email2") + "<br>"
                + "<br>" + this.getText("pronto.plano.padrao.h2.1") + "<br>"
                + this.getText("dicas.p1") + "<br>"
                + this.getText("dicas.p2") + "<br>"
                + this.getText("dicas.p3") + "<br>"
                + this.getText("dicas.p4") + "<br>"
                + this.getText("dicas.p5") + "<br>"
                + this.getText("dicas.p5.1") + "<br>"
                + this.getText("dicas.p5.2") + "<br>"
                + this.getText("dicas.p6") + "<br>"
                + "<br>" + this.getText("pronto.plano.h2.1") + ":<br>"
                + this.prontoParaParar.getDataPararStr();
        if (this.fissuras()) {
            html += "<br>" + this.getText("pronto.plano.h2.2") + ":<br>"
                    + this.prontoParaParar.getFissuraStr();
        }
        if (this.estrategias()) {
            html += "<br>" + this.getText("pronto.plano.h2.3") + ":<br>";

            if (this.prontoParaParar.getEvitarRecaidaFara1() != null && !this.prontoParaParar.getEvitarRecaidaFara1().isEmpty()) {
                html += this.prontoParaParar.getEvitarRecaidaFara1() + "<br>";
            }
            if (this.prontoParaParar.getEvitarRecaidaFara2() != null && !this.prontoParaParar.getEvitarRecaidaFara2().isEmpty()) {
                html += this.prontoParaParar.getEvitarRecaidaFara2() + "<br>";
            }
            if (this.prontoParaParar.getEvitarRecaidaFara3() != null && !this.prontoParaParar.getEvitarRecaidaFara3().isEmpty()) {
                html += this.prontoParaParar.getEvitarRecaidaFara3() + "<br>";
            }
        }
        if (this.deucerto()) {
            html += "<br>" + this.getText("pronto.plano.h2.4") + ":<br>";
            if (this.prontoParaParar.getEvitarRecaida1() != null && !this.prontoParaParar.getEvitarRecaida1().isEmpty()) {
                html += this.prontoParaParar.getEvitarRecaida1() + "<br>";
            }
            if (this.prontoParaParar.getEvitarRecaida2() != null && !this.prontoParaParar.getEvitarRecaida2().isEmpty()) {
                html += this.prontoParaParar.getEvitarRecaida2() + "<br>";
            }
            if (this.prontoParaParar.getEvitarRecaida3() != null && !this.prontoParaParar.getEvitarRecaida3().isEmpty()) {
                html += this.prontoParaParar.getEvitarRecaida3() + "<br>";
            }
        }
        html += "<br><br>" + this.getText("att") + "<br>";
        return html;
    }

    public String planoToText(User user) {
        String text = this.getText("dear") + user.getName() + ",\n"
                + "\n" + this.getText("plano.email2") + "\n"
                + "\n" + this.getText("pronto.plano.padrao.h2.1") + "\n"
                + this.getText("dicas.p1") + "\n"
                + this.getText("dicas.p2") + "\n"
                + this.getText("dicas.p3") + "\n"
                + this.getText("dicas.p4") + "\n"
                + this.getText("dicas.p5") + "\n"
                + this.getText("dicas.p5.1") + "\n"
                + this.getText("dicas.p5.2") + "\n"
                + this.getText("dicas.p6") + "\n"
                + "\n" + this.getText("pronto.plano.h2.1") + ":\n"
                + this.prontoParaParar.getDataPararStr();
        if (this.fissuras()) {
            text += "\n" + this.getText("pronto.plano.h2.2") + ":\n"
                    + this.prontoParaParar.getFissuraStr();
        }
        if (this.estrategias()) {
            text += "\n" + this.getText("pronto.plano.h2.3") + ":\n";

            if (this.prontoParaParar.getEvitarRecaidaFara1() != null && !this.prontoParaParar.getEvitarRecaidaFara1().isEmpty()) {
                text += this.prontoParaParar.getEvitarRecaidaFara1() + "\n";
            }
            if (this.prontoParaParar.getEvitarRecaidaFara2() != null && !this.prontoParaParar.getEvitarRecaidaFara2().isEmpty()) {
                text += this.prontoParaParar.getEvitarRecaidaFara2() + "\n";
            }
            if (this.prontoParaParar.getEvitarRecaidaFara3() != null && !this.prontoParaParar.getEvitarRecaidaFara3().isEmpty()) {
                text += this.prontoParaParar.getEvitarRecaidaFara3() + "\n";
            }
        }
        if (this.deucerto()) {
            text += "\n" + this.getText("pronto.plano.h2.4") + ":\n";
            if (this.prontoParaParar.getEvitarRecaida1() != null && !this.prontoParaParar.getEvitarRecaida1().isEmpty()) {
                text += this.prontoParaParar.getEvitarRecaida1() + "\n";
            }
            if (this.prontoParaParar.getEvitarRecaida2() != null && !this.prontoParaParar.getEvitarRecaida2().isEmpty()) {
                text += this.prontoParaParar.getEvitarRecaida2() + "\n";
            }
            if (this.prontoParaParar.getEvitarRecaida3() != null && !this.prontoParaParar.getEvitarRecaida3().isEmpty()) {
                text += this.prontoParaParar.getEvitarRecaida3() + "\n";
            }
        }
        text += "\n\n" + this.getText("att") + "\n";
        return text;
    }

    public ByteArrayOutputStream gerarPdf() {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            Document document = new Document();
            PdfWriter.getInstance(document, os);
            document.open();

            document.addTitle(this.getText("plano.personalizado"));
            document.addAuthor("vivasemtabaco.com.br");

            URL url = FacesContext.getCurrentInstance().getExternalContext().getResource("/resources/default/images/viva-sem-tabaco-new.png");
            Image img = Image.getInstance(url);
            img.setAlignment(Element.ALIGN_CENTER);
            img.scaleToFit(75, 75);
            document.add(img);

            Color color = Color.getHSBColor(214, 81, 46);
            Font f1 = new Font(FontFamily.HELVETICA, 20, Font.BOLD, BaseColor.BLUE);
            f1.setColor(22, 63, 117);
            Font f2 = new Font(FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.BLUE);
            f2.setColor(22, 63, 117);
            Font f3 = new Font(FontFamily.HELVETICA, 11);

            Paragraph paragraph = new Paragraph(this.getText("meu.plano"), f1);
            paragraph.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraph);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);

            paragraph = new Paragraph(this.getText("pronto.plano.padrao.h2.1"), f2);
            document.add(paragraph);
            paragraph = new Paragraph(this.getText("dicas.p1"), f3);
            document.add(paragraph);
            paragraph = new Paragraph(this.getText("dicas.p2"), f3);
            document.add(paragraph);
            paragraph = new Paragraph(this.getText("dicas.p3"), f3);
            document.add(paragraph);
            paragraph = new Paragraph(this.getText("dicas.p4"), f3);
            document.add(paragraph);
            paragraph = new Paragraph(this.getText("dicas.p5"), f3);
            document.add(paragraph);
            paragraph = new Paragraph(this.getText("dicas.p5.1"), f3);
            document.add(paragraph);
            paragraph = new Paragraph(this.getText("dicas.p5.2"), f3);
            document.add(paragraph);
            paragraph = new Paragraph(this.getText("dicas.p6"), f3);
            document.add(paragraph);
            document.add(Chunk.NEWLINE);

            paragraph = new Paragraph(this.getText("pronto.plano.h2.1"), f2);
            document.add(paragraph);
            paragraph = new Paragraph(this.prontoParaParar.getDataPararStr(), f3);
            document.add(paragraph);
            document.add(Chunk.NEWLINE);

            if (this.fissuras()) {
                paragraph = new Paragraph(this.getText("pronto.plano.h2.2"), f2);
                document.add(paragraph);
                paragraph = new Paragraph(this.prontoParaParar.getFissuraStr(), f3);
                document.add(paragraph);
                document.add(Chunk.NEWLINE);
            } else {
                paragraph.add(new Paragraph(" "));
            }

            if (this.estrategias()) {
                paragraph = new Paragraph(this.getText("pronto.plano.h2.3"), f2);
                document.add(paragraph);
                paragraph = new Paragraph(this.prontoParaParar.getEvitarRecaidaFara1(), f3);
                document.add(paragraph);
                paragraph = new Paragraph(this.prontoParaParar.getEvitarRecaidaFara2(), f3);
                document.add(paragraph);
                paragraph = new Paragraph(this.prontoParaParar.getEvitarRecaidaFara3(), f3);
                document.add(paragraph);
                document.add(Chunk.NEWLINE);
            } else {
                paragraph.add(new Paragraph(" "));
            }

            if (this.deucerto()) {
                paragraph = new Paragraph(this.getText("pronto.plano.h2.4"), f2);
                document.add(paragraph);
                paragraph = new Paragraph(this.prontoParaParar.getEvitarRecaida1(), f3);
                document.add(paragraph);
                paragraph = new Paragraph(this.prontoParaParar.getEvitarRecaida2(), f3);
                document.add(paragraph);
                paragraph = new Paragraph(this.prontoParaParar.getEvitarRecaida3(), f3);
                document.add(paragraph);
            } else {
                paragraph.add(new Paragraph(" "));
            }

            document.close();

            return os;
        } catch (Exception e) {
            return null;
        }

    }

    public StreamedContent getPlanoPersonalizado() {

        InputStream is;
        try {
            is = new ByteArrayInputStream(gerarPdf().toByteArray());
            return new DefaultStreamedContent(is, "application/pdf", "plano.pdf");
        } catch (Exception e) {
            Logger.getLogger(ProntoParaPararController.class.getName()).log(Level.SEVERE, this.getText("erro.pdf"));
            return null;
        }

    }

}
